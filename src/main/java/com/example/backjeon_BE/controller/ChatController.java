package com.example.backjeon_BE.controller;

import com.example.backjeon_BE.dto.ChatMessage;
import com.example.backjeon_BE.service.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameRoomService gameRoomService;

    private static final int MAX_MESSAGE_LENGTH = 500;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        String email = principal != null ? principal.getName() : "익명";  // ← null 체크 추가!

//    // 게임방 참가자 검증
//    if (!gameRoomService.isParticipant(chatMessage.getRoomId(), email)) {
//        throw new RuntimeException("해당 게임방의 참가자가 아닙니다");
//    }

        String validatedMessage = validateAndSanitizeMessage(chatMessage.getMessage());
        chatMessage.setMessage(validatedMessage);
        chatMessage.setSender(email);
        chatMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/game/" + chatMessage.getRoomId(),
                chatMessage
        );
    }

    @MessageMapping("/chat.join")
    public void joinRoom(@Payload ChatMessage chatMessage, Principal principal) {
        String email = principal != null ? principal.getName() : "익명";  // ← null 체크 추가!

//    if (!gameRoomService.isParticipant(chatMessage.getRoomId(), email)) {  // ← 주석 처리!
//        throw new RuntimeException("해당 게임방의 참가자가 아닙니다");
//    }

        chatMessage.setSender(email);
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/game/" + chatMessage.getRoomId(),
                chatMessage
        );
    }

    @MessageMapping("/chat.leave")
    public void leaveRoom(@Payload ChatMessage chatMessage, Principal principal) {
        String email = principal != null ? principal.getName() : "익명";  // ← null 체크 추가!

//    if (!gameRoomService.isParticipant(chatMessage.getRoomId(), email)) {  // ← 주석 처리!
//        throw new RuntimeException("해당 게임방의 참가자가 아닙니다");
//    }

        chatMessage.setSender(email);
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/game/" + chatMessage.getRoomId(),
                chatMessage
        );
    }

    private String validateAndSanitizeMessage(String message) {
        if (message == null) {
            throw new RuntimeException("메시지는 필수입니다");
        }

        String trimmedMessage = message.trim();
        if (trimmedMessage.isEmpty()) {
            throw new RuntimeException("빈 메시지는 전송할 수 없습니다");
        }

        if (trimmedMessage.length() > MAX_MESSAGE_LENGTH) {
            throw new RuntimeException("메시지는 " + MAX_MESSAGE_LENGTH + "자 이하로 입력해주세요");
        }

        String sanitized = sanitizeHtml(trimmedMessage);
        return sanitized;
    }

    private String sanitizeHtml(String message) {
        if (message == null) {
            return null;
        }

        return message
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("&", "&amp;")
                .replaceAll("/", "&#x2F;");
    }
}