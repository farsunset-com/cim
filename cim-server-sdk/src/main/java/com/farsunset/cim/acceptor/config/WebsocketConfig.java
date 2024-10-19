/*
 * Copyright 2013-2022 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.acceptor.config;

import com.farsunset.cim.auth.AuthPredicateInfo;
import com.farsunset.cim.constant.WebsocketProtocol;
import com.farsunset.cim.handler.LoggingHandler;

import java.util.function.Predicate;

/**
 * 基于websocket的服务配置
 */
public class WebsocketConfig extends SocketConfig{

    private static final int DEFAULT_PORT = 34567;

    private static final String DEFAULT_PATH = "/";

    private static final WebsocketProtocol DEFAULT_PROTOCOL = WebsocketProtocol.PROTOBUF;

    /**
     * websocket端点地址
     */
    private String path;

    /**
     * 消息体协议，JSON 或者 Protobuf
     */
    private WebsocketProtocol protocol;

    /**
     * 鉴权实现
     */
    private Predicate<AuthPredicateInfo> authPredicate;

    /**
     * 自定义日志打印处理器，可不设置
     */

    private LoggingHandler loggingHandler;


    @Override
    public Integer getPort() {
        return super.getPort() == null || super.getPort() <= 0 ? DEFAULT_PORT : super.getPort();
    }

    public String getPath() {
        return path == null ? DEFAULT_PATH : path;
    }

    public WebsocketProtocol getProtocol() {
        return protocol == null ? DEFAULT_PROTOCOL : protocol;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProtocol(WebsocketProtocol protocol) {
        this.protocol = protocol;
    }

    public void setAuthPredicate(Predicate<AuthPredicateInfo> authPredicate) {
        this.authPredicate = authPredicate;
    }

    public Predicate<AuthPredicateInfo> getAuthPredicate() {
        return authPredicate;
    }

    public LoggingHandler getLoggingHandler() {
        return loggingHandler;
    }

    public void setLoggingHandler(LoggingHandler loggingHandler) {
        this.loggingHandler = loggingHandler;
    }
}
