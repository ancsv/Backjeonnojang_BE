package com.example.backjeon_BE.config;

import com.example.backjeon_BE.entity.User;
import com.example.backjeon_BE.security.JwtProvider;
import com.example.backjeon_BE.service.GameRoomService;
import com.example.backjeon_BE.repository.UserRepository;
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
    private final UserRepository userRepository;

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
                        // 1. 방 번호 추출
                        String roomId = destination.substring("/topic/game/".length()).replaceAll("[^0-9]", "");

                        // 2. 이메일 추출
                        String email = (String) accessor.getSessionAttributes().get("userEmail");

                        // 3. 권한 검증 (DB 조회)
                        if (email == null || !gameRoomService.isParticipant(roomId, email)) {
                            System.out.println("🚨 [차단] 권한 없음: " + email);
                            throw new RuntimeException("권한이 없습니다.");
                        }

                        System.out.println("✅ [승인]: " + email);
                    }
                }
                return message;
            }
        });
    }
}