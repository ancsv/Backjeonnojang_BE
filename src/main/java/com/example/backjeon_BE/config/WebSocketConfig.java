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
                        throw new RuntimeException("유효하지 않은 토큰입니다");
                    }
                }
                // 구독 시점 보안 (도청 방어 핵심 로직)
                else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination(); // 예: /topic/game/room123
                    String email = accessor.getUser().getName();    // 위에서 인증된 이메일

                    // 주소에서 roomId 추출 (방 주소 형식이 /topic/game/{roomId} 인 경우)
                    if (destination != null && destination.startsWith("/topic/game/")) {
                        String roomId = destination.replace("/topic/game/", "");

//                        // [인가 체크] 이 사용자가 진짜 이 방의 참가자인가?
//                        if (!gameRoomService.isParticipant(roomId, email)) {
//                            throw new RuntimeException("해당 게임방의 구독 권한이 없습니다. (도청 차단)");
//                        }
                    }
                }
                return message;
            }
        });
    }
}