package com.example.backjeon_BE.repository;

import com.example.backjeon_BE.entity.GameHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GameHistoryRepository extends JpaRepository<GameHistory, Long> {
    List<GameHistory> findByUserId(Long userId);
}