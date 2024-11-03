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
package com.farsunset.cim.component.handler;

import com.farsunset.cim.component.handler.annotation.CIMHandler;
import com.farsunset.cim.component.push.DefaultMessagePusher;
import com.farsunset.cim.component.redis.SignalRedisTemplate;
import com.farsunset.cim.constant.ChannelAttr;
import com.farsunset.cim.constants.Constants;
import com.farsunset.cim.entity.Session;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.handler.CIMRequestHandler;
import com.farsunset.cim.model.Message;
import com.farsunset.cim.model.ReplyBody;
import com.farsunset.cim.model.SentBody;
import com.farsunset.cim.service.SessionService;
import io.netty.channel.Channel;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;

/**
 * 客户端长连接通道发消息
 */
@CIMHandler(key = "client_message")
public class MessageHandler implements CIMRequestHandler {

	@Resource
	private DefaultMessagePusher defaultMessagePusher;


	@Override
	public void process(Channel channel, SentBody body) {

		Message message = new Message();
		/*
		 获取到当前链接的UID
		 */
		message.setSender(channel.attr(ChannelAttr.UID).get());
		message.setReceiver(body.get("receiver"));
		message.setAction(body.get("action"));
		message.setContent(body.get("content"));
		message.setFormat(body.get("format"));
		message.setTitle(body.get("title"));
		message.setExtra(body.get("extra"));

		message.setId(System.currentTimeMillis());

		defaultMessagePusher.push(message);


		/*
		 * 将发送的消息ID 通过Replay异步发送给客户端，
		 * 可通过reqId来对应上多个消息的各自ID
		 */
		ReplyBody reply = new ReplyBody();
		reply.setKey(body.getKey());
		reply.setCode(HttpStatus.OK.value());
		reply.put("messageId",String.valueOf(message.getId()));
		reply.put("requestId",body.get("requestId"));
		reply.setTimestamp(System.currentTimeMillis());

	}
}
