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
                        // 1. 방 정보 추출
                        String extractedRoomId = destination.substring("/topic/game/".length()).trim();
                        String numericId = extractedRoomId.replaceAll("[^0-9]", ""); // 숫자만 추출 (예: "1")

                        // 2. 세션에서 이메일 가져오기
                        String email = (String) accessor.getSessionAttributes().get("userEmail");

                        if (email == null) {
                            System.out.println("🚨 [차단] 인증 정보 없음");
                            throw new RuntimeException("인증 정보가 없습니다.");
                        }

                        // 3. DB에서 실제 유저 ID 확인 (로그 확인용 핵심 로직)
                        // userRepository가 주입되어 있어야 합니다.
                        User user = userRepository.findByEmail(email).orElse(null);
                        if (user != null) {
                            System.out.println("🆔 [ID 대조] 유저: " + email + " | DB ID: " + user.getId() + " | 시도방 ID: " + numericId);
                        }

                        System.out.println("🧐 [인가 검증] 추출된방: " + extractedRoomId + " -> DB조회ID: " + numericId);

                        // 4. DB 조회 (참여 여부 확인)
                        boolean isMember = gameRoomService.isParticipant(numericId, email);

                        // 5. 최종 판정
                        if (isMember) {
                            System.out.println("✅ [승인] 접속 허용: " + email);
                        } else {
                            // 여기가 실행된다면 DB의 game_match 테이블에 위에서 찍힌 ID값이 없는 것입니다.
                            System.out.println("🚨 [차단] 권한 없음 (도청 감지): " + email + " | 방: " + extractedRoomId);
                            throw new RuntimeException("해당 방에 대한 권한이 없습니다.");
                        }
                    }
                }
                return message;
            }
        });
    }
}