package com.farsunset.cim.coder;

import com.farsunset.cim.constant.ChannelAttr;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import java.net.InetAddress;

/**
 * 解析获取真实客户端IP
 */
public class ProxyMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HAProxyMessage) {
            HAProxyMessage proxyMessage = (HAProxyMessage) msg;
            ctx.channel().attr(ChannelAttr.REMOTE_IP).set(proxyMessage.sourceAddress());
            return;
        }

        if (msg instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            ctx.channel().attr(ChannelAttr.REMOTE_IP).set(getRemoteIp(httpRequest));
        }

        ctx.fireChannelRead(msg);
    }


    private String getRemoteIp(FullHttpRequest request) {
        // 1. 尝试从 X-Forwarded-For 头部获取
        String xForwardedFor = request.headers().get("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For 可能包含多个 IP（逗号分隔），第一个是真实客户端 IP
            String[] ips = xForwardedFor.split(",");
            String clientIp = ips[0].trim();
            if (isValidIp(clientIp)) {
                return clientIp;
            }
        }

        // 2. 尝试从 X-Real-IP 头部获取
        String xRealIp = request.headers().get("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && isValidIp(xRealIp)) {
            return xRealIp;
        }

        // 3. 尝试从 Proxy-Client-IP 头部获取
        String proxyClientIp = request.headers().get("Proxy-Client-IP");
        if (proxyClientIp != null && !proxyClientIp.isEmpty() && isValidIp(proxyClientIp)) {
            return proxyClientIp;
        }

        // 4. 尝试从 WL-Proxy-Client-IP 头部获取
        String wlProxyClientIp = request.headers().get("WL-Proxy-Client-IP");
        if (wlProxyClientIp != null && !wlProxyClientIp.isEmpty() && isValidIp(wlProxyClientIp)) {
            return wlProxyClientIp;
        }

        // 5. 尝试从 HTTP_CLIENT_IP 头部获取
        String httpClientIp = request.headers().get("HTTP_CLIENT_IP");
        if (httpClientIp != null && !httpClientIp.isEmpty() && isValidIp(httpClientIp)) {
            return httpClientIp;
        }

        // 6. 尝试从 HTTP_X_FORWARDED_FOR 头部获取
        String httpXForwardedFor = request.headers().get("HTTP_X_FORWARDED_FOR");
        if (httpXForwardedFor != null && !httpXForwardedFor.isEmpty()) {
            String[] ips = httpXForwardedFor.split(",");
            String clientIp = ips[0].trim();
            if (isValidIp(clientIp)) {
                return clientIp;
            }
        }
        return null;
    }

    private boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        try {
            if (ip.equalsIgnoreCase("unknown")) {
                return false;
            }
            return InetAddress.getByName(ip) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
