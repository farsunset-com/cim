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

import com.farsunset.cim.acceptor.AppSocketAcceptor;
import com.farsunset.cim.acceptor.WebsocketAcceptor;
import com.farsunset.cim.acceptor.config.SocketConfig;
import com.farsunset.cim.acceptor.config.WebsocketConfig;
import com.farsunset.cim.component.handler.annotation.CIMHandler;
import com.farsunset.cim.component.logger.CIMEventLogger;
import com.farsunset.cim.component.predicate.AuthPredicate;
import com.farsunset.cim.config.properties.APNsProperties;
import com.farsunset.cim.config.properties.CIMAppSocketProperties;
import com.farsunset.cim.config.properties.CIMWebsocketProperties;
import com.farsunset.cim.group.SessionGroup;
import com.farsunset.cim.group.TagSessionGroup;
import com.farsunset.cim.handler.CIMRequestHandler;
import com.farsunset.cim.model.SentBody;
import com.farsunset.cim.service.SessionService;
import io.netty.channel.Channel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@EnableConfigurationProperties({
		APNsProperties.class,
		CIMWebsocketProperties.class,
		CIMAppSocketProperties.class})
@Configuration
public class CIMConfig implements CIMRequestHandler, ApplicationListener<ApplicationStartedEvent> {

	@Resource
	private ApplicationContext applicationContext;

	@Resource
	private SessionService sessionService;

	private final HashMap<String,CIMRequestHandler> handlerMap = new HashMap<>();

	@Bean
	public SessionGroup sessionGroup() {
		return new SessionGroup();
	}

	@Bean
	public TagSessionGroup tagSessionGroup() {
		return new TagSessionGroup();
	}


	@Bean(destroyMethod = "destroy",initMethod = "bind")
	@ConditionalOnProperty(name = {"cim.websocket.enable"},matchIfMissing = true)
	public WebsocketAcceptor websocketAcceptor(CIMWebsocketProperties properties, AuthPredicate authPredicate, CIMEventLogger cimEventLogger) {
		WebsocketConfig config = new WebsocketConfig();
		config.setAuthPredicate(authPredicate);
		config.setPath(properties.getPath());
		config.setPort(properties.getPort());
		config.setProtocol(properties.getProtocol());
		config.setOuterRequestHandler(this);
		config.setEnable(properties.isEnable());

		config.setWriteIdle(properties.getWriteIdle());
		config.setReadIdle(properties.getReadIdle());
		config.setMaxPongTimeout(properties.getMaxPongTimeout());
		config.setLoggingHandler(cimEventLogger);
		return new WebsocketAcceptor(config);
	}

	@Bean(destroyMethod = "destroy",initMethod = "bind")
	@ConditionalOnProperty(name = {"cim.app.enable"},matchIfMissing = true)
	public AppSocketAcceptor appSocketAcceptor(CIMAppSocketProperties properties, CIMEventLogger cimEventLogger) {

		SocketConfig config = new SocketConfig();
		config.setPort(properties.getPort());
		config.setOuterRequestHandler(this);
		config.setEnable(properties.isEnable());

		config.setWriteIdle(properties.getWriteIdle());
		config.setReadIdle(properties.getReadIdle());
		config.setMaxPongTimeout(properties.getMaxPongTimeout());
		config.setLoggingHandler(cimEventLogger);

		return new AppSocketAcceptor(config);
	}

	@Override
	public void process(Channel channel, SentBody body) {
		
        CIMRequestHandler handler = handlerMap.get(body.getKey());
		
		if(handler == null) {return ;}
		
		handler.process(channel, body);
		
	}
	/*
	 * springboot启动完成之后再启动cim服务的，避免服务正在重启时，客户端会立即开始连接导致意外异常发生.
	 */
	@Override
	public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {

		Map<String, CIMRequestHandler> beans =  applicationContext.getBeansOfType(CIMRequestHandler.class);

		for (Map.Entry<String, CIMRequestHandler> entry : beans.entrySet()) {

			CIMRequestHandler handler = entry.getValue();

			CIMHandler annotation = handler.getClass().getAnnotation(CIMHandler.class);

			if (annotation != null){
				handlerMap.put(annotation.key(),handler);
			}
		}

		sessionService.deleteLocalhost();
	}
}