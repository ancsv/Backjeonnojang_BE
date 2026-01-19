package com.example.backjeon_BE.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "game_room")
public class GameRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private String status;

    public Long getId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getStatus() {
        return status;
    }

    public static GameRoom createRoom(String roomName, int maxPlayers) {
        GameRoom room = new GameRoom();
        room.roomName = roomName;
        room.maxPlayers = maxPlayers;
        room.status = "WAITING";
        return room;
    }

    public void startGame() {
        this.status = "PLAYING";
    }

    public void endGame() {
        this.status = "FINISHED";
    }
}