package com.farsunset.cim.coder;

import com.farsunset.cim.constant.ChannelAttr;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.NetUtil;
import io.netty.util.ReferenceCountUtil;
import java.net.InetSocketAddress;
import java.util.function.Predicate;

/**
 * 解析获取真实客户端IP
 */
public class ProxyMessageHandler extends ChannelInboundHandlerAdapter {

    private final Predicate<String> trustedProxyPredicate;

    private boolean rejected;

    public ProxyMessageHandler() {
        this(null);
    }

    public ProxyMessageHandler(Predicate<String> trustedProxyPredicate) {
        this.trustedProxyPredicate = trustedProxyPredicate;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (rejected || !ctx.channel().isActive()) {
            ReferenceCountUtil.release(msg);
            return;
        }
        if (msg instanceof HAProxyMessage) {
            HAProxyMessage proxyMessage = (HAProxyMessage) msg;
            try {
                if (!isTrustedProxy(getRemoteHostAddress(ctx))) {
                    rejected = true;
                    ctx.close();
                    return;
                }
                String sourceAddress = proxyMessage.sourceAddress();
                if (sourceAddress != null && !isValidIp(sourceAddress)) {
                    rejected = true;
                    ctx.close();
                    return;
                }
                ctx.channel().attr(ChannelAttr.REMOTE_IP).set(sourceAddress);
            } catch (RuntimeException | Error exception) {
                rejected = true;
                ctx.close();
                throw exception;
            } finally {
                ReferenceCountUtil.release(proxyMessage);
            }
            return;
        }

        if (msg instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            try {
                ctx.channel().attr(ChannelAttr.REMOTE_IP).set(getRemoteIp(httpRequest,ctx));
            } catch (RuntimeException | Error exception) {
                rejected = true;
                ReferenceCountUtil.release(httpRequest);
                ctx.close();
                throw exception;
            }
        }

        ctx.fireChannelRead(msg);
    }


    private String getRemoteIp(FullHttpRequest request,ChannelHandlerContext ctx) {
        String remoteIp = getRemoteHostAddress(ctx);
        if (!isTrustedProxy(remoteIp)) {
            return remoteIp;
        }

        String xForwardedFor = request.headers().get("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return getForwardedIp(xForwardedFor, remoteIp);
        }

        String xRealIp = request.headers().get("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && isValidIp(xRealIp)) {
            return xRealIp;
        }

        String proxyClientIp = request.headers().get("Proxy-Client-IP");
        if (proxyClientIp != null && !proxyClientIp.isEmpty() && isValidIp(proxyClientIp)) {
            return proxyClientIp;
        }

        String wlProxyClientIp = request.headers().get("WL-Proxy-Client-IP");
        if (wlProxyClientIp != null && !wlProxyClientIp.isEmpty() && isValidIp(wlProxyClientIp)) {
            return wlProxyClientIp;
        }

        String httpClientIp = request.headers().get("HTTP_CLIENT_IP");
        if (httpClientIp != null && !httpClientIp.isEmpty() && isValidIp(httpClientIp)) {
            return httpClientIp;
        }

        String httpXForwardedFor = request.headers().get("HTTP_X_FORWARDED_FOR");
        if (httpXForwardedFor != null && !httpXForwardedFor.isEmpty()) {
            return getForwardedIp(httpXForwardedFor, remoteIp);
        }
        return remoteIp;
    }

    private String getForwardedIp(String forwardedFor, String remoteIp) {
        String clientIp = remoteIp;
        String[] addresses = forwardedFor.split(",", -1);
        for (int index = addresses.length - 1; index >= 0 && isTrustedProxy(clientIp); index--) {
            String address = addresses[index].trim();
            if (!isValidIp(address)) {
                return remoteIp;
            }
            clientIp = address;
        }
        return clientIp;
    }

    private boolean isTrustedProxy(String ip) {
        return ip != null && trustedProxyPredicate != null && trustedProxyPredicate.test(ip);
    }

    private boolean isValidIp(String ip) {
        return ip != null && (NetUtil.isValidIpV4Address(ip) || NetUtil.isValidIpV6Address(ip));
    }

    private String getRemoteHostAddress(ChannelHandlerContext ctx) {
        if (ctx.channel().remoteAddress() instanceof InetSocketAddress) {
            return ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
        }
        return null;
    }
}
