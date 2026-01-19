package com.example.backjeon_BE.dto.response;

import com.example.backjeon_BE.entity.GameRoom;

public class GameRoomResponseDto {
    private Long id;
    private String roomName;
    private int maxPlayers;

    public GameRoomResponseDto(Long id, String roomName, int maxPlayers) {
        this.id = id;
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
    }

    public Long getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public static GameRoomResponseDto from(GameRoom gameRoom) {
        return new GameRoomResponseDto(
                gameRoom.getId(),
                gameRoom.getRoomName(),
                gameRoom.getMaxPlayers()
        );
    }
}