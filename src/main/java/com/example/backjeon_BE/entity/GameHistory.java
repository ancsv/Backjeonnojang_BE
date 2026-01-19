package com.example.backjeon_BE.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "game_history")
public class GameHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private int score;

    public Long getGameId() {
        return gameId;
    }

    public Long getUserId() {
        return userId;
    }

    public int getScore() {
        return score;
    }

    public static GameHistory createGameHistory(Long userId, int score) {
        GameHistory gameHistory = new GameHistory();
        gameHistory.userId = userId;
        gameHistory.score = score;
        return gameHistory;
    }
}