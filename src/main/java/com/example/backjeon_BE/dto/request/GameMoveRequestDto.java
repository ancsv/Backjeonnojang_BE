package com.example.backjeon_BE.dto.request;

public class GameMoveRequestDto {
    private String roomId;
    private String playerId;
    private MoveDto move;

    // Getters and Setters

    public static class MoveDto {
        private String from; // 예: "e2"
        private String to;   // 예: "e4"

        // Getters and Setters
    }
}
