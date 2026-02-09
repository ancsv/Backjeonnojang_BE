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
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");

                    // 토큰 없으면 연결 차단
                    if (token == null || !token.startsWith("Bearer ")) {
                        throw new RuntimeException("인증 토큰이 필요합니다");
                    }

                    token = token.substring(7);
                    try {
                        String email = jwtProvider.getEmailFromToken(token);
                        Long userId = jwtProvider.getUserIdFromToken(token);

                        accessor.setUser(new UsernamePasswordAuthenticationToken(
                                email, null, null));
                    } catch (Exception e) {
//                        throw new RuntimeException("유효하지 않은 토큰입니다");
                        System.out.println(" [보안로그] 비인가 접근 감지");
                    }
                }
                // 구독 시점 보안 (도청 방어 핵심 로직)
                else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();

                    if (destination != null && destination.startsWith("/topic/game/")) {
                        // 1. roomId 추출 및 공백 제거
                        String roomId = destination.substring("/topic/game/".length()).trim();

                        // 2. 이메일 추출 (세션 속성 우선 확인)
                        String email = (accessor.getSessionAttributes().get("userEmail") != null)
                                ? ((String) accessor.getSessionAttributes().get("userEmail")).trim()
                                : (accessor.getUser() != null ? accessor.getUser().getName().trim() : null);

                        System.out.println("🧐 [최종대조] 방ID: [" + roomId + "] | 유저: [" + (email != null ? email : "null") + "]");

                        // 3. 인증 체크
                        if (email == null) {
                            System.out.println("❌ [차단] 인증 정보가 아예 없음");
                            throw new RuntimeException("인증 정보가 없습니다.");
                        }

                        // 4. 인가 체크 (테스트 계정 통과 + DB 검증 조합)
                        boolean isMember = gameRoomService.isParticipant(roomId, email);

                        if (email.equals("test2@test.com") || isMember) {
                            System.out.println("✅ [승인] 정상 사용자 접속: " + email);
                        } else {
                            // 공격자나 명단에 없는 유저는 여기서 확실히 차단
                            System.out.println("🚨 [차단] 비인가 접근 시도! 유저: " + email + " | 방: " + roomId);
                            throw new RuntimeException("구독 권한이 없습니다.");
                        }
                    }
                }
                return message;
            }
        });
    }
}