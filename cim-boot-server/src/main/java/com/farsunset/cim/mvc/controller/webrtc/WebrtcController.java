/*
 * Copyright 2013-2019 Xia Jun(3979434@qq.com).
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
package com.farsunset.cim.mvc.controller.webrtc;
 
import com.farsunset.cim.annotation.UID;
import com.farsunset.cim.component.push.DefaultMessagePusher;
import com.farsunset.cim.constants.MessageAction;
import com.farsunset.cim.model.Message;
import com.farsunset.cim.mvc.request.WebrtcRequest;
import com.farsunset.cim.mvc.response.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/webrtc")
@Tag(name = "单人通话信令推送接口")
public class WebrtcController {

	@Resource
	private DefaultMessagePusher defaultMessagePusher;

	@Operation(summary = "发起单人语音通话")
	@PostMapping(value = {"/voice"})
	public ResponseEntity<Void> voice(@Parameter(hidden = true) @UID String uid,@Parameter(description = "对方用户ID") @RequestParam String targetId) {
 

		Message message = new Message();
		message.setAction(MessageAction.ACTION_900);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}


	@Operation(summary = "发起单人视频通话")
	@PostMapping(value =  {"/video"})
	public ResponseEntity<Void> video(@Parameter(hidden = true) @UID String uid,@Parameter(description = "对方用户ID") @RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_901);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@Operation(summary = "接受通话")
	@PostMapping(value =  {"/accept"})
	public ResponseEntity<Void> accept(@Parameter(hidden = true) @UID String uid,@Parameter(description = "对方用户ID") @RequestParam String targetId) {
		Message message = new Message();
		message.setAction(MessageAction.ACTION_902);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@Operation(summary = "拒绝通话")
	@PostMapping(value =  {"/reject"})
	public ResponseEntity<Void> reject(@Parameter(hidden = true) @UID String uid,@Parameter(description = "对方用户ID") @RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_903);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@Operation(summary = "反馈正忙")
	@PostMapping(value =  {"/busy"})
	public ResponseEntity<Void> busy(@Parameter(hidden = true) @UID String uid, @Parameter(description = "对方用户ID") @RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_904);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@Operation(summary = "挂断通话")
	@PostMapping(value =  {"/hangup"})
	public ResponseEntity<Void> hangup(@Parameter(hidden = true) @UID String uid,@Parameter(description = "对方用户ID") @RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_905);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@Operation(summary = "取消呼叫")
	@PostMapping(value =  {"/cancel"})
	public ResponseEntity<Void> cancel(@Parameter(hidden = true) @UID String uid, @Parameter(description = "对方用户ID") @RequestParam String targetId) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_906);
		message.setSender(uid);
		message.setReceiver(targetId);
		defaultMessagePusher.push(message);

		return ResponseEntity.make();
	}

	@Operation(summary = "同步IceCandidate")
	@PostMapping(value = {"/transmit/ice"})
	public ResponseEntity<Void> ice(@Parameter(hidden = true) @UID String uid,
									@RequestBody WebrtcRequest request
	) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_907);
		message.setSender(uid);
		message.setContent(request.getContent());
		message.setReceiver(request.getUid());
		defaultMessagePusher.push(message);
		return ResponseEntity.make();
	}

	@Operation(summary = "同步offer")
	@PostMapping(value =  {"/transmit/offer"})
	public ResponseEntity<Void> offer(@Parameter(hidden = true) @UID String uid,
									  @RequestBody WebrtcRequest request
	) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_908);
		message.setSender(uid);
		message.setContent(request.getContent());
		message.setReceiver(request.getUid());
		defaultMessagePusher.push(message);
		return ResponseEntity.make();
	}

	@Operation(summary = "同步answer")
	@PostMapping(value =  {"/transmit/answer"})
	public ResponseEntity<Void> answer(@Parameter(hidden = true) @UID String uid,
									   @RequestBody WebrtcRequest request
	) {

		Message message = new Message();
		message.setAction(MessageAction.ACTION_909);
		message.setSender(uid);
		message.setContent(request.getContent());
		message.setReceiver(request.getUid());
		defaultMessagePusher.push(message);
		return ResponseEntity.make();
	}
}
