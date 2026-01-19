package com.example.backjeon_BE.repository;

import com.example.backjeon_BE.entity.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRoomRepository extends JpaRepository<GameRoom, Long> {
}
