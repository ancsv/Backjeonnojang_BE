package com.example.backjeon_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String roomId;      // 게임방 ID
    private String sender;      // 보내는 사람 (username)
    private String message;     // 메시지 내용
    private LocalDateTime timestamp;  // 전송 시간
    private MessageType type;   // 메시지 타입

    public enum MessageType {
        CHAT,    // 일반 채팅
        JOIN,    // 입장 알림
        LEAVE    // 퇴장 알림
    }
}