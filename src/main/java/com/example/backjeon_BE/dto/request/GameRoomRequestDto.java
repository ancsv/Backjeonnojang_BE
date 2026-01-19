package com.example.backjeon_BE.dto.request;

import java.util.List;

public class GameRoomRequestDto {
    private String roomId;
    private String roomName;
    private int maxPlayers;

    private List<PlayerDto> players;
    // Getters and Setters

    public static class PlayerDto {
        private String id;
        private String username;

        // Getters and Setters
    }

    public GameRoomRequestDto() {}

    public String getRoomName() {
        return roomName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }


}
