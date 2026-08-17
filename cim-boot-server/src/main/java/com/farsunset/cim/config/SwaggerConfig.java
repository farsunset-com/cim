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
package com.farsunset.cim.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String ACCESS_TOKEN = "access-token";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CIM Push Service APIs.")
                        .description("CIM接口文档")
                        .version("3.0"))
                .components(new Components().addSecuritySchemes(ACCESS_TOKEN,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .name(ACCESS_TOKEN)
                                .in(SecurityScheme.In.HEADER)))
                .addSecurityItem(new SecurityRequirement().addList(ACCESS_TOKEN));
    }

    @Bean
    public GroupedOpenApi messageApiDocket() {
        return GroupedOpenApi.builder()
                .group("1、消息相关接口")
                .packagesToScan("com.farsunset.cim.mvc.controller.message")
                .build();
    }

    @Bean
    public GroupedOpenApi webrtcApiDocket() {
        return GroupedOpenApi.builder()
                .group("2、Webrtc相关接口")
                .packagesToScan("com.farsunset.cim.mvc.controller.webrtc")
                .build();
    }
}
