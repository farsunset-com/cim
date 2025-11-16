package com.farsunset.cim.auth;

import com.farsunset.cim.constant.ChannelAttr;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.function.Predicate;

/**
 * IP黑名单处理
 */
@ChannelHandler.Sharable
public class BlacklistHandler extends ChannelInboundHandlerAdapter {

    private final Predicate<String> predicate;

    public BlacklistHandler(Predicate<String> blacklistPredicate) {
        this.predicate = blacklistPredicate;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (predicate == null || predicate.test(getClientIp(ctx))) {
            super.channelRegistered(ctx);
            return;
        }
        ctx.close().addListener(ChannelFutureListener.CLOSE);
    }

    private String getClientIp(ChannelHandlerContext ctx) {

        String remoteIp = ctx.channel().attr(ChannelAttr.REMOTE_IP).get();
        if (remoteIp != null) {
            return remoteIp;
        }

        if (ctx.channel().remoteAddress() instanceof InetSocketAddress) {
            InetSocketAddress remoteAddr = (InetSocketAddress) ctx.channel().remoteAddress();
            return remoteAddr.getAddress().getHostAddress();
        }

        return null;
    }
}
