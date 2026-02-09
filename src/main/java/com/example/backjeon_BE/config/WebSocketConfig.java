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
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        try {
                            String email = jwtProvider.getEmailFromToken(token);
                            // 💡 세션 속성에 이메일을 명시적으로 저장합니다. (나중에 꺼내기 위해)
                            accessor.getSessionAttributes().put("userEmail", email);

                            accessor.setUser(new UsernamePasswordAuthenticationToken(email, null, null));
                        } catch (Exception e) {
                            System.out.println(" [보안로그] 토큰 파싱 에러");
                        }
                    }
                }
                // 구독 시점 보안 (도청 방어 핵심 로직)
                else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    if (destination != null && destination.startsWith("/topic/game/")) {
                        String roomId = destination.substring("/topic/game/".length()).trim();

                        // 위에서 저장한 세션 속성에서 이메일을 가져옵니다.
                        String email = (String) accessor.getSessionAttributes().get("userEmail");

                        System.out.println("🧐 [검증 시작] 방: " + roomId + " | 유저: " + email);

                        if (email == null) {
                            throw new RuntimeException("인증 정보가 없습니다.");
                        }

                        // 4. 인가 체크 (실제 서비스 버전)
                        boolean isMember = gameRoomService.isParticipant(roomId, email);

                        if (isMember) {
                            // DB에 이 방과 이 유저의 매칭 정보가 있을 때만 통과
                            System.out.println("✅ [승인] 정상 사용자 접속: " + email);
                            return message;
                        } else {
                            // 그 외의 모든 경우(공격자, 다른 방 유저 등)는 가차 없이 차단
                            System.out.println("🚨 [차단] 권한 없음! 유저: " + email + " | 방: " + roomId);
                            throw new RuntimeException("해당 방에 대한 구독 권한이 없습니다.");
                        }
                    }
                }
                return message;
            }
        });
    }
}