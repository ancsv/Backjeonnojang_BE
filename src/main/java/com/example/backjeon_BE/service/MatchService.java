package com.example.backjeon_BE.service;

import com.example.backjeon_BE.dto.response.MatchStatusResponseDto;
import com.example.backjeon_BE.entity.Match;
import com.example.backjeon_BE.repository.MatchRepository;
import com.example.backjeon_BE.repository.UserRepository;
import com.example.backjeon_BE.security.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public MatchService(MatchRepository matchRepository, UserRepository userRepository, JwtProvider jwtProvider) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional(readOnly = true)  // ✅ 규칙 9
    public List<MatchStatusResponseDto> getAllMatches() {
        return matchRepository.findAll()
                .stream()
                .map(MatchStatusResponseDto::from)  // ✅ 규칙 11
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)  // ✅ 규칙 9
    public MatchStatusResponseDto getMatchById(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found with ID: " + matchId));
        return MatchStatusResponseDto.from(match);  // ✅ 규칙 11
    }

    @Transactional(readOnly = true)  // ✅ 규칙 9
    public MatchStatusResponseDto getMatchStatus(String token) {
        Long userId = getUserIdFromToken(token);

        // ✅ 규칙 3: User 조회 불필요 (userId만 사용)
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        Match match = matchRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)  // ✅ 규칙 3
                .orElseThrow(() -> new RuntimeException("Match not found for user"));

        return MatchStatusResponseDto.from(match);  // ✅ 규칙 11
    }

    @Transactional  // ✅ 규칙 9
    public String requestMatch(String token, String mode) {
        Long userId = getUserIdFromToken(token);

        // ✅ 규칙 3: User 조회 불필요
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        Match match = Match.createMatch(userId, mode);  // ✅ 규칙 3, 10

        matchRepository.save(match);

        return "Match requested successfully!";
    }

    private Long getUserIdFromToken(String token) {
        return jwtProvider.getUserIdFromToken(token);
    }
}