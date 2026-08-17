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

import com.farsunset.cim.service.SessionService;
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
@RequestMapping("/apns")
@Tag(name = "APNs推送相关")
public class APNsController {

	@Operation(summary = "开启apns")
	@PostMapping(value = "/open")
	public ResponseEntity<Void> open(@Parameter(description = "用户ID", example = "0") @RequestParam String uid,
								 @Parameter(description = "APNs的deviceToken") @RequestParam String deviceToken) {

		sessionService.openApns(uid,deviceToken);

		return ResponseEntity.ok().build();
	}

	@Resource
	private SessionService sessionService;

	@Operation(summary = "关闭apns")
	@PostMapping(value = "/close")
	public ResponseEntity<Void> close(@Parameter(description = "用户ID", example = "0") @RequestParam String uid) {

		sessionService.closeApns(uid);

		return ResponseEntity.ok().build();
	}
}
