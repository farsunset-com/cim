/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***************************************************************************************
 *                                                                                     *
 *                        Website : http://www.farsunset.com                           *
 *                                                                                     *
 ***************************************************************************************
 */
package com.farsunset.cim;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.farsunset.cim.coder.ClientMessageDecoder;
import com.farsunset.cim.coder.ClientMessageEncoder;
import com.farsunset.cim.constant.BundleKey;
import com.farsunset.cim.constant.CIMConstant;
import com.farsunset.cim.constant.IntentAction;
import com.farsunset.cim.logger.CIMLogger;
import com.farsunset.cim.model.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 */
class CIMConnectManager {

    private static final int READ_BUFFER_SIZE = 2048;

    private static final int WRITE_BUFFER_SIZE = 1024;

    private static final int CONNECT_TIME_OUT = 5 * 1000;

    /*
     服务端在连接写空闲120秒的时候发送心跳请求给客户端，所以客户端在空闲150秒后都没有收到任何数据，则关闭链接，并重新创建
     */
    private static final int CONNECT_ALIVE_TIME_OUT = 120 * 1000;

    private static final CIMLogger LOGGER = CIMLogger.getLogger();

    private SocketChannel socketChannel;

    private final Context context;

    private final ExecutorService workerExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r, "worker-"));

    private final ExecutorService bossExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r, "boss-"));

    private final ClientMessageEncoder messageEncoder = new ClientMessageEncoder();
    private final ClientMessageDecoder messageDecoder = new ClientMessageDecoder();

    private final Random random = new Random();

    private boolean destroyed;

    public CIMConnectManager(Context context) {
        this.context = context;
    }

    public synchronized void connect(final String host, final int port) {

        if (destroyed || socketChannel != null
                || CIMPushManager.isDestroyed(context) || CIMPushManager.isStopped(context)) {
            return;
        }

        if (!CIMPushManager.isNetworkConnected(context)) {

            Intent intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction(IntentAction.ACTION_CONNECT_FAILED);
            context.sendBroadcast(intent);

            return;
        }

        final SocketChannel channel;
        try {
            channel = SocketChannel.open();
        } catch (IOException exception) {
            handleConnectFailedEvent(exception);
            return;
        }
        socketChannel = channel;

        bossExecutor.execute(() -> {

            try {

                synchronized (this) {
                    if (destroyed || socketChannel != channel) {
                        return;
                    }
                    LOGGER.startConnect(host, port);
                    CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);
                }

                channel.configureBlocking(true);
                channel.socket().setTcpNoDelay(true);
                channel.socket().setKeepAlive(true);
                channel.socket().setReceiveBufferSize(READ_BUFFER_SIZE);
                channel.socket().setSendBufferSize(WRITE_BUFFER_SIZE);

                channel.socket().connect(new InetSocketAddress(host, port), CONNECT_TIME_OUT);

                synchronized (this) {
                    if (destroyed || socketChannel != channel
                            || CIMPushManager.isDestroyed(context) || CIMPushManager.isStopped(context)) {
                        return;
                    }
                    handleConnectedEvent();
                }

                /*
                 *开始读取来自服务端的消息，先读取3个字节的消息头
                 */
                ByteBuffer headerBuffer = ByteBuffer.allocate(CIMConstant.DATA_HEADER_LENGTH);
                while (channel.read(headerBuffer) >= 0) {
                    if (!headerBuffer.hasRemaining()) {
                        handleSocketReadEvent(headerBuffer, channel);
                    }
                }

                close(channel, true);

            } catch (ConnectException | SocketTimeoutException | UnknownHostException exception) {
                synchronized (this) {
                    if (!destroyed && socketChannel == channel) {
                        close(channel, false);
                        handleConnectFailedEvent(exception);
                    }
                }
            } catch (IOException exception) {
                close(channel, true);
            }finally {
                close(channel, false);
            }
        });
    }

    public synchronized void close() {
        close(socketChannel, true);
    }

    public synchronized void destroy() {
        destroyed = true;
        close(socketChannel, false);
        idleHandler.removeMessages(0);
        workerExecutor.shutdownNow();
        bossExecutor.shutdownNow();
    }

    public synchronized boolean isConnected() {
        return socketChannel != null && socketChannel.isOpen() && socketChannel.isConnected();
    }

    public void pong() {
        send(Pong.getInstance());
    }

    public synchronized void send(final BinaryBody body) {

        if (destroyed || !isConnected()) {
            return;
        }

        final SocketChannel channel = socketChannel;
        workerExecutor.execute(() -> {
            ByteBuffer buffer;
            try {
                buffer = messageEncoder.encode(body);
            } catch (RuntimeException exception) {
                Log.e("CIM", "Failed to encode outgoing message", exception);
                return;
            }

            try {
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                synchronized (this) {
                    if (!destroyed && socketChannel == channel) {
                        onMessageSent(body);
                    }
                }
            } catch (IOException exception) {
                Log.e("CIM", "Failed to send outgoing message", exception);
                close(channel, true);
            }
        });

    }

    private synchronized void close(SocketChannel channel, boolean notify) {
        if (channel == null || socketChannel != channel) {
            return;
        }
        socketChannel = null;
        idleHandler.removeMessages(0);
        try {
            channel.close();
        } catch (IOException ignore) {
        }
        CIMCacheManager.putBoolean(context, CIMCacheManager.KEY_CIM_CONNECTION_STATE, false);
        if (notify && !destroyed) {
            onSessionClosed(channel);
        }
    }


    private void onSessionCreated() {
        LOGGER.sessionCreated(socketChannel);

        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction(IntentAction.ACTION_CONNECT_FINISHED);
        context.sendBroadcast(intent);

    }

    private void onSessionClosed(SocketChannel channel) {

        idleHandler.removeMessages(0);

        LOGGER.sessionClosed(channel);

        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction(IntentAction.ACTION_CONNECTION_CLOSED);
        context.sendBroadcast(intent);

    }

    private synchronized void onSessionIdle(SocketChannel channel) {

        if (destroyed || channel == null || socketChannel != channel) {
            return;
        }

        LOGGER.sessionIdle(channel);

        close(channel, true);
    }


    private void onMessageReceived(Object obj) {

        if (obj instanceof Message) {

            Intent intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction(IntentAction.ACTION_MESSAGE_RECEIVED);
            intent.putExtra(Message.class.getName(), (Message) obj);
            context.sendBroadcast(intent);

        }
        if (obj instanceof ReplyBody) {

            Intent intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction(IntentAction.ACTION_REPLY_RECEIVED);
            intent.putExtra(ReplyBody.class.getName(), (ReplyBody) obj);
            context.sendBroadcast(intent);
        }
    }


    private void onMessageSent(Object message) {

        LOGGER.messageSent(socketChannel, message);

        if (message instanceof SentBody) {
            Intent intent = new Intent();
            intent.setPackage(context.getPackageName());
            intent.setAction(IntentAction.ACTION_SEND_FINISHED);
            intent.putExtra(SentBody.class.getName(), (SentBody) message);
            context.sendBroadcast(intent);
        }
    }

    private final Handler idleHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message m) {
            onSessionIdle((SocketChannel) m.obj);
        }
    };

    private void handleConnectFailedEvent(Exception exception) {

        long retryAfter;

        if (exception instanceof UnknownHostException){
            /*
             * 通常是网络由WIFI切换为移动网出现这个异常
             */
            retryAfter = 3000L;
        }else {
            /*
             * 随机3-10秒后重连
             */
            retryAfter = 3000L + random.nextInt(7001) ;
        }

        LOGGER.connectFailure(retryAfter);

        Intent intent = new Intent();
        intent.setPackage(context.getPackageName());
        intent.setAction(IntentAction.ACTION_CONNECT_FAILED);
        intent.putExtra(BundleKey.KEY_RECONNECT_AFTER, retryAfter);
        context.sendBroadcast(intent);

    }

    private void handleConnectedEvent() {

        closeCountDown();

        onSessionCreated();

    }

    private void handleSocketReadEvent(ByteBuffer headerBuffer, SocketChannel channel) throws IOException {

        synchronized (this) {
            if (destroyed || socketChannel != channel) {
                return;
            }
            closeCountDown();
        }

        Object message = messageDecoder.doDecode(headerBuffer, channel);

        synchronized (this) {
            if (destroyed || socketChannel != channel) {
                return;
            }
            LOGGER.messageReceived(channel, message);

            if (message instanceof Ping) {
                send(Pong.getInstance());
                return;
            }

            this.onMessageReceived(message);
        }
    }


    private void closeCountDown() {

        idleHandler.removeMessages(0);

        idleHandler.sendMessageDelayed(idleHandler.obtainMessage(0, socketChannel), CONNECT_ALIVE_TIME_OUT);
    }

}
