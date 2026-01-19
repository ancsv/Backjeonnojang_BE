package com.example.backjeon_BE.service;

import com.example.backjeon_BE.exception.GameNotFoundException;
import com.example.backjeon_BE.exception.InvalidGameIdException;
import com.example.backjeon_BE.exception.UserNotFoundException;
import com.example.backjeon_BE.repository.UserRepository;
import com.example.backjeon_BE.dto.response.GameDetailResponseDto;
import com.example.backjeon_BE.dto.response.GameHistoryResponseDto;
import com.example.backjeon_BE.entity.GameHistory;
import com.example.backjeon_BE.repository.GameHistoryRepository;
import com.example.backjeon_BE.security.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameHistoryService {

    private final GameHistoryRepository gameHistoryRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public GameHistoryService(GameHistoryRepository gameHistoryRepository, UserRepository userRepository, JwtProvider jwtProvider) {
        this.gameHistoryRepository = gameHistoryRepository;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional(readOnly = true)
    public List<GameHistoryResponseDto> getAllGameHistories() {
        return gameHistoryRepository.findAll()
                .stream()
                .map(GameHistoryResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GameHistoryResponseDto> getGameHistoriesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId); 
        }

        return gameHistoryRepository.findByUserId(userId)
                .stream()
                .map(GameHistoryResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GameHistoryResponseDto> getGameHistory(String token) {
        Long userId = getUserIdFromToken(token);
        return getGameHistoriesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public GameDetailResponseDto getGameDetails(String token, String gameId) {
        Long userId = getUserIdFromToken(token);

        Long parsedGameId;
        try {
            parsedGameId = Long.parseLong(gameId);
        } catch (NumberFormatException e) {
            throw new InvalidGameIdException(gameId);
        }

        GameHistory gameHistory = gameHistoryRepository.findById(parsedGameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        return GameDetailResponseDto.from(gameHistory);
    }

    private Long getUserIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtProvider.getUserIdFromToken(token);
    }
}