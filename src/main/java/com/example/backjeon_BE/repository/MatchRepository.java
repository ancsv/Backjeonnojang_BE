package com.example.backjeon_BE.repository;

import com.example.backjeon_BE.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findFirstByUserIdOrderByCreatedAtDesc(Long userId);
    List<Match> findByRoomId(Long roomId);
    boolean existsByRoomIdAndUserId(Long roomId, Long userId);
}
