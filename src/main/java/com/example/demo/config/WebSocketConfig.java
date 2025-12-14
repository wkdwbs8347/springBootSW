package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("http://localhost:*")
				.addInterceptors(new HttpSessionHandshakeInterceptor()).withSockJS();
	}

	// WebSocket 세션 검사 인터셉터 등록
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new MyChannelInterceptor());
	}

	// WebSocket 세션 검사 인터셉터 구현
	public static class MyChannelInterceptor implements ChannelInterceptor {

		@Override
		public Message<?> preSend(Message<?> message, MessageChannel channel) {

			// STOMP 헤더 접근
			var accessor = org.springframework.messaging.simp.stomp.StompHeaderAccessor.wrap(message);

			// CONNECT 시점에만 검사
			if (org.springframework.messaging.simp.stomp.StompCommand.CONNECT.equals(accessor.getCommand())) {

				// 세션에서 userId를 가져오기 (HttpSession으로부터 가져오기)
				Object userIdObj = accessor.getSessionAttributes().get("userId");
				if (userIdObj == null) {
					throw new IllegalStateException("WebSocket Unauthorized (no session or userId)");
				}
				String userId = String.valueOf(userIdObj); // Integer → String 안전하게 변환
			}

			return message;
		}
	}
}