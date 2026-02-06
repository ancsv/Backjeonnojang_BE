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

    private JwtProvider jwtProvider;
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

                    System.out.println("=== 구독 시도 Destination: " + destination);

                    if (destination != null && destination.startsWith("/topic/game/")) {
                        // roomId 추출 시 뒤에 붙은 쿼리 파라미터 등이 있을 수 있으므로 정교하게 추출
                        String roomId = destination.substring("/topic/game/".length());

                        // 유저 정보 확인
                        if (accessor.getUser() == null) {
                            throw new RuntimeException("인증 정보가 없습니다.");
                        }
                        String email = accessor.getUser().getName();

                        // 인가 체크
                        if (!gameRoomService.isParticipant(roomId, email)) {
                            System.out.println("도청 차단 발생 - 유저: " + email + ", 방: " + roomId);
//                            throw new RuntimeException("구독 권한 없음");
                        }
                    }
                }
                return message;
            }
        });
    }
}