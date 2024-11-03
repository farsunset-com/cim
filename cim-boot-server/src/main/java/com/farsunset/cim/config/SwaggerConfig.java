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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@EnableOpenApi
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket messageApiDocket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .title("CIM Push Service APIs.")
                        .description("消息发送相关接口")
                        .version("3.0")
                        .build())
                .groupName("1、消息相关接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.farsunset.cim.mvc.controller.message"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean
    public Docket webrtcApiDocket() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .title("CIM Push Service APIs.")
                        .description("可用于webrtc通话信令接口")
                        .version("3.0")
                        .build())
                .groupName("2、Webrtc相关接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.farsunset.cim.mvc.controller.webrtc"))
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }



    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> schemeList = new ArrayList<>();
        schemeList.add(new ApiKey("access-token", "access-token", "header"));
        return schemeList;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContextList = new ArrayList<>();
        List<SecurityReference> securityReferenceList = new ArrayList<>();
        securityReferenceList.add(new SecurityReference("access-token", new AuthorizationScope[]{new AuthorizationScope("global", "accessAnything")}));
        securityContextList.add(SecurityContext
                .builder()
                .securityReferences(securityReferenceList)
                .operationSelector(operationContext -> true)
                .build()
        );
        return securityContextList;
    }

}
