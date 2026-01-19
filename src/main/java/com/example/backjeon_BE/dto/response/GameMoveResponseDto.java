package com.example.backjeon_BE.dto.response;

public class GameMoveResponseDto {
    private String status;
    private String message;
    private String boardState; // JSON 형태의 장기판 상태

    public GameMoveResponseDto() {}
    // Getters and Setters

    public GameMoveResponseDto(String status, String message, String boardState) {
        this.status = status;
        this.message = message;
        this.boardState = boardState;
    }
    public GameMoveResponseDto(Long moveId, String status) {
        this.status = status;
        this.message = "Move ID: " + moveId;
        this.boardState = "{}";  // 기본값 설정
    }
    // Getters and Setters 추가
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }
}
