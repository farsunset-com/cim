package com.farsunset.cim.auth;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

import java.util.function.Predicate;

/**
 * 鉴权处理器
 */
@ChannelHandler.Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter {

    private static final AttributeKey<Boolean> REJECTED = AttributeKey.valueOf(AuthHandler.class, "rejected");

    private final Predicate<AuthPredicateInfo> authPredicate;


    public AuthHandler(Predicate<AuthPredicateInfo> authPredicate) {
        this.authPredicate = authPredicate;
    }


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (Boolean.TRUE.equals(ctx.channel().attr(REJECTED).get())) {
            ReferenceCountUtil.release(msg);
            return;
        }
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;

            boolean authenticated;
            try {
                authenticated = authPredicate == null || authPredicate.test(AuthPredicateInfo.of(request, ctx));
            } catch (RuntimeException | Error exception) {
                ctx.channel().attr(REJECTED).set(true);
                ReferenceCountUtil.release(request);
                ctx.close();
                throw exception;
            }
            if (!authenticated) {
                ctx.channel().attr(REJECTED).set(true);
                DefaultFullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.UNAUTHORIZED);
                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, 0);
                ReferenceCountUtil.release(request);
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return;
            }
        }

        //other protocols
        super.channelRead(ctx, msg);
    }

}
