package com.example.backjeon_BE.dto.response;

import com.example.backjeon_BE.entity.Match;

public class MatchStatusResponseDto {
    private Long matchId;
    private String status;
    private String roomId;
    private OpponentDto opponent;

    public Long getMatchId() {
        return matchId;
    }

    public String getStatus() {
        return status;
    }

    public String getRoomId() {
        return roomId;
    }

    public OpponentDto getOpponent() {
        return opponent;
    }

    public MatchStatusResponseDto() {}

    public MatchStatusResponseDto(Long matchId, String status) {
        this.matchId = matchId;
        this.status = status;
    }

    // ✅ 추가: 정적 팩토리 메서드
    public static MatchStatusResponseDto from(Match match) {
        return new MatchStatusResponseDto(
                match.getId(),
                "Match details for room ID: " + match.getRoomId()
        );
    }

    public static class OpponentDto {
        private String id;
        private String username;
        private int rating;

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public int getRating() {
            return rating;
        }
    }
}