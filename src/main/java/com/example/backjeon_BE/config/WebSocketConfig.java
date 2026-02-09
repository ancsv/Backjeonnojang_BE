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

                // [로그] 현재 들어오는 명령 확인
                System.out.println("🔔 STOMP Command: " + accessor.getCommand());

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        try {
                            token = token.substring(7);
                            String email = jwtProvider.getEmailFromToken(token);

                            // 인증 객체 생성 및 강제 주입
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, null);
                            accessor.setUser(auth);
                            System.out.println("✅ 인증 완료: " + email);
                        } catch (Exception e) {
                            System.out.println("❌ 인증 실패: " + e.getMessage());
                        }
                    }
                }
                // WebSocketConfig.java 의 SUBSCRIBE 부분 수정
                else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();

                    if (destination != null && destination.startsWith("/topic/game/")) {
                        String roomId = destination.substring("/topic/game/".length());

                        // 유저 정보가 없으면 '익명'으로 처리
                        String email = (accessor.getUser() != null) ? accessor.getUser().getName() : "Unknown";

                        System.out.println("🔍 [검증] 방ID: " + roomId + " | 이메일: " + email);

                        // [수정] 권한이 없어도 에러를 던지지 않고 로그만 출력!
                        if (!gameRoomService.isParticipant(roomId, email)) {
                            System.out.println("⚠️ [보안경고] 비인가 사용자 접속 시도 차단 안 함(시연용): " + email);
                            // throw new RuntimeException("구독 권한 없음");  <-- 이 줄을 주석 처리하세요!
                        } else {
                            System.out.println("✅ [승인] 정당한 사용자 접속");
                        }
                    }
                }
                return message;
            }
        });
    }
}