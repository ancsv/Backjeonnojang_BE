package com.example.backjeon_BE.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "game_move")
public class GameMove extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long moveId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private int moveFrom;

    @Column(nullable = false)
    private int moveTo;

    public Long getId() {
        return moveId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public int getMoveFrom() {
        return moveFrom;
    }

    public int getMoveTo() {
        return moveTo;
    }

    public static GameMove createMove(Long userId, Long roomId, int moveFrom, int moveTo) {
        GameMove gameMove = new GameMove();
        gameMove.userId = userId;
        gameMove.roomId = roomId;
        gameMove.moveFrom = moveFrom;
        gameMove.moveTo = moveTo;
        return gameMove;
    }
}