package com.farsunset.cim.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;

/**
 * 鉴权相关信息
 */

public class AuthPredicateInfo {

    private final String uri;

    private final HttpHeaders header;

    private final ChannelHandlerContext ctx;

    public AuthPredicateInfo(String uri, HttpHeaders header, ChannelHandlerContext ctx) {
        this.uri = uri;
        this.header = header;
        this.ctx = ctx;
    }

    public String getHeader(String name) {
        return header.get(name);
    }

    public List<String> getHeaders(String name) {
        return header.getAll(name);
    }

    public Integer getIntHeader(String name) {
        return header.getInt(name);
    }

    public String getParameter(String name) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        List<String> valueList = decoder.parameters().get(name);
        return valueList == null || valueList.isEmpty() ? null : valueList.get(0);
    }

    public List<String> getParameters(String name) {
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        return decoder.parameters().get(name);
    }

    public String getUri() {
        return this.uri;
    }

    public ChannelHandlerContext getCtx() {
        return this.ctx;
    }

    public static AuthPredicateInfo of(FullHttpRequest request, ChannelHandlerContext context) {
        return new AuthPredicateInfo(request.uri(), request.headers(), context);
    }


}
