package com.example.backjeon_BE.dto.response;

import com.example.backjeon_BE.entity.GameHistory;
import java.util.List;

public class GameHistoryResponseDto {
    private List<HistoryDto> history;
    private Long gameId;
    private Long userId;
    private int score;

    public GameHistoryResponseDto(Long gameId, Long userId, int score) {
        this.gameId = gameId;
        this.userId = userId;
        this.score = score;
    }

    public List<HistoryDto> getHistory() {
        return history;
    }

    public Long getGameId() {
        return gameId;
    }

    public Long getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }
    //팩토리 메서드 예시
    public static GameHistoryResponseDto from(GameHistory gameHistory) {
        return new GameHistoryResponseDto(
                gameHistory.getGameId(),
                gameHistory.getUserId(),
                gameHistory.getScore()
        );
    }

    public static class HistoryDto {
        private String gameId;
        private String result;
        private String opponent;
        private String date;

        public String getGameId() {
            return gameId;
        }

        public String getResult() {
            return result;
        }

        public String getOpponent() {
            return opponent;
        }

        public String getDate() {
            return date;
        }
    }
}