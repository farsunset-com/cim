package com.farsunset.cim.component.logger;

import com.farsunset.cim.handler.LoggingHandler;
import com.farsunset.cim.model.Ping;
import com.farsunset.cim.model.Pong;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.springframework.stereotype.Component;

/**
 * 自定义 CIM事件日志打印，重写方法时注意需要有以下2种之一！！！
 * 1、调用对应的ctx.fireXX(msg)方法;
 * 2、调用supper
 */
@ChannelHandler.Sharable
@Component
public class CIMEventLogger extends LoggingHandler {

    /**
     * 不打印客户端发送的Pong日志
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof Pong){
            ctx.fireChannelRead(msg);
            return;
        }

        super.channelRead(ctx,msg);
    }


    /**
     * 不打印服务端发送的Ping日志
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        if (msg instanceof Ping){
            ctx.write(msg, promise);
            return;
        }

        super.write(ctx,msg,promise);
    }

    /**
     * 不打长连接空闲事件日志
     * @param ctx
     * @param evt
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        ctx.fireUserEventTriggered(evt);
    }

}
