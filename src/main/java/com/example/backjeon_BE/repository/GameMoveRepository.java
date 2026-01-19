package com.example.backjeon_BE.repository;

import com.example.backjeon_BE.entity.GameMove;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameMoveRepository extends JpaRepository<GameMove, Long> {
}
