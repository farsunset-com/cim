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
package com.farsunset.cim.constant;

import io.netty.util.AttributeKey;

public interface ChannelAttr {

    /**
     * 心跳请求次数
     */
    AttributeKey<Integer> PING_COUNT = AttributeKey.valueOf("ping_count");

    /**
     * UID标识
     */
    AttributeKey<String> UID = AttributeKey.valueOf("uid");

    /**
     * 客户端类型 web、app
     */
    AttributeKey<String> CHANNEL = AttributeKey.valueOf("channel");

    /**
     * 客户端设备ID
     */
    AttributeKey<String> DEVICE_ID = AttributeKey.valueOf("device_id");

    /**
     * 客户端语言偏好(en_US)
     */
    AttributeKey<String> LANGUAGE = AttributeKey.valueOf("language");

    /**
     * Nio链接的ID
     */
    AttributeKey<String> ID = AttributeKey.valueOf("id");

    /**
     * Nio链接的标签
     */
    AttributeKey<String> TAG = AttributeKey.valueOf("tag");

    /**
     * 客户端真实IP
     */
    AttributeKey<String> REMOTE_IP = AttributeKey.valueOf("remote_ip");
}
