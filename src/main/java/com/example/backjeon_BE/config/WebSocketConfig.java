package com.example.backjeon_BE.config;

import com.example.backjeon_BE.security.JwtProvider;
import com.example.backjeon_BE.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtProvider jwtProvider;
    private final GameRoomService gameRoomService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        try {
                            token = token.substring(7);
                            String email = jwtProvider.getEmailFromToken(token);

                            // 1. 시큐리티 컨텍스트 설정
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, null);
                            accessor.setUser(auth);

                            // 2. [중요] 세션 속성에 이메일 직접 저장 (유실 방지용)
                            accessor.getSessionAttributes().put("userEmail", email);

                            System.out.println("✅ [연결 승인] 유저: " + email);
                        } catch (Exception e) {
                            System.out.println("❌ [연결 거부] 토큰 에러: " + e.getMessage());
                            throw new RuntimeException("Auth Error");
                        }
                    }
                }
                else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith("/topic/game/")) {
                        String roomId = destination.substring("/topic/game/".length());

                        // 세션 속성에서 이메일 꺼내기 (accessor.getUser()가 null일 때를 대비)
                        String email = (String) accessor.getSessionAttributes().get("userEmail");

                        if (email == null && accessor.getUser() != null) {
                            email = accessor.getUser().getName();
                        }

                        System.out.println("🧐 [인가 체크] 방: " + roomId + " | 유저: " + email);

                        // DB 체크
                        if (email == null || !gameRoomService.isParticipant(roomId, email)) {
                            System.out.println("🚨 [차단] 비인가 접근! 방: " + roomId + " | 유저: " + email);
                            throw new RuntimeException("No Permission");
                        }
                        System.out.println("⭕ [구독 완료] " + email + " 님이 " + roomId + "에 입장");
                    }
                }
                return message;
            }
        });
    }
}