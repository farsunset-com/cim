package com.farsunset.cim.auth;

import com.farsunset.cim.constant.CIMConstant;
import com.farsunset.cim.model.ReplyBody;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.function.Predicate;

/**
 * 鉴权处理器
 */
@ChannelHandler.Sharable
public class AuthHandler extends ChannelInboundHandlerAdapter {

    /*
     *认证失败，返回replyBody给客户端
     */
    private final ReplyBody failedBody = ReplyBody.make(CIMConstant.CLIENT_HANDSHAKE,
            HttpResponseStatus.UNAUTHORIZED.code(),
            HttpResponseStatus.UNAUTHORIZED.reasonPhrase());


    private final Predicate<AuthPredicateInfo> authPredicate;


    public AuthHandler(Predicate<AuthPredicateInfo> authPredicate) {
        this.authPredicate = authPredicate;
    }


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;

            /*
             * 鉴权不通过，发送响应体并关闭链接
             */
            if (authPredicate != null && ! authPredicate.test(AuthPredicateInfo.of(request, ctx))) {
                ctx.channel().writeAndFlush(failedBody).addListener(ChannelFutureListener.CLOSE);
                return;
            }

            //鉴权通过后移除本handler
            ctx.pipeline().remove(this);
        }

        //other protocols
        super.channelRead(ctx, msg);
    }

}
