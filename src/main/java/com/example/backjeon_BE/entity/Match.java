package com.example.backjeon_BE.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "game_match")
public class Match extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(nullable = false)
    private String mode;

    @Column(nullable = false)
    private String status;

    public Long getId() {
        return matchId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getMode() {
        return mode;
    }

    public String getStatus() {
        return status;
    }

    public static Match createMatch(Long userId, String mode) {
        Match match = new Match();
        match.userId = userId;
        match.mode = mode;
        match.status = "WAITING";
        return match;
    }
}