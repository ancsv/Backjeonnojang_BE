package com.example.backjeon_BE.dto.response;

import com.example.backjeon_BE.entity.GameHistory;
import java.util.List;

public class GameDetailResponseDto {
    private Long gameId;
    private List<PlayerDto> players;
    private List<MoveDto> moves;
    private String result;
    private String date;
    private Long userId;
    private int score;

    public GameDetailResponseDto(Long gameId, Long userId, int score) {
        this.gameId = gameId;
        this.userId = userId;
        this.score = score;
    }

    public Long getGameId() {
        return gameId;
    }

    public List<PlayerDto> getPlayers() {
        return players;
    }

    public List<MoveDto> getMoves() {
        return moves;
    }

    public String getResult() {
        return result;
    }

    public String getDate() {
        return date;
    }

    public Long getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }

    public static GameDetailResponseDto from(GameHistory gameHistory) {
        return new GameDetailResponseDto(
                gameHistory.getGameId(),
                gameHistory.getUserId(),
                gameHistory.getScore()
        );
    }

    public static class PlayerDto {
        private String id;
        private String username;

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }
    }

    public static class MoveDto {
        private String from;
        private String to;

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }
}