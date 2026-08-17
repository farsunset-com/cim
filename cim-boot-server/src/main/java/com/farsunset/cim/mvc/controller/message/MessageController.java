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
package com.farsunset.cim.mvc.controller.message;

import com.farsunset.cim.component.push.DefaultMessagePusher;
import com.farsunset.cim.model.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;


@RestController
@RequestMapping("/api/message")
@Tag(name = "消息相关接口")
public class MessageController  {

	@Resource
	private DefaultMessagePusher defaultMessagePusher;

	@Operation(summary = "发送消息")
	@PostMapping(value = "/send")
	public ResponseEntity<Long> send(@Parameter(description = "发送者UID") @RequestParam String sender ,
									 @Parameter(description = "接收者UID") @RequestParam String receiver ,
									 @Parameter(description = "消息动作") @RequestParam String action ,
									 @Parameter(description = "消息标题") @RequestParam(required = false) String title ,
									 @Parameter(description = "消息内容") @RequestParam(required = false) String content ,
									 @Parameter(description = "消息格式") @RequestParam(required = false) String format ,
									 @Parameter(description = "扩展字段") @RequestParam(required = false) String extra)  {

		Message message = new Message();
		message.setSender(sender);
		message.setReceiver(receiver);
		message.setAction(action);
		message.setContent(content);
		message.setFormat(format);
		message.setTitle(title);
		message.setExtra(extra);

		message.setId(System.currentTimeMillis());

		defaultMessagePusher.push(message);

		return ResponseEntity.ok(message.getId());
	}


}
