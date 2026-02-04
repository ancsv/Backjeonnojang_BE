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
    private final GameRoomService gameRoomService;  // 추가!

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        String email = principal.getName();

        // 게임방 참가자 검증!
        if (!gameRoomService.isParticipant(chatMessage.getRoomId(), email)) {
            throw new RuntimeException("해당 게임방의 참가자가 아닙니다");
        }

        chatMessage.setSender(email);
        chatMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/game/" + chatMessage.getRoomId(),
                chatMessage
        );
    }

    @MessageMapping("/chat.join")
    public void joinRoom(@Payload ChatMessage chatMessage, Principal principal) {
        String email = principal.getName();

        if (!gameRoomService.isParticipant(chatMessage.getRoomId(), email)) {
            throw new RuntimeException("해당 게임방의 참가자가 아닙니다");
        }

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
        String email = principal.getName();

        if (!gameRoomService.isParticipant(chatMessage.getRoomId(), email)) {
            throw new RuntimeException("해당 게임방의 참가자가 아닙니다");
        }

        chatMessage.setSender(email);
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setTimestamp(LocalDateTime.now());

        messagingTemplate.convertAndSend(
                "/topic/game/" + chatMessage.getRoomId(),
                chatMessage
        );
    }
}