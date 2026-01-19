package com.example.backjeon_BE.repository;

import com.example.backjeon_BE.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Optional<Match> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
}