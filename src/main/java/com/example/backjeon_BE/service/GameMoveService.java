package com.example.backjeon_BE.service;

import com.example.backjeon_BE.dto.request.GameMoveRequestDto;
import com.example.backjeon_BE.dto.response.GameMoveResponseDto;
import com.example.backjeon_BE.entity.GameMove;
import com.example.backjeon_BE.repository.GameMoveRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameMoveService {

    private final GameMoveRepository gameMoveRepository;

    public GameMoveService(GameMoveRepository gameMoveRepository) {
        this.gameMoveRepository = gameMoveRepository;
    }

    // 모든 움직임 조회
    public List<GameMoveResponseDto> getAllMoves() {
        return gameMoveRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 특정 움직임 조회
    public GameMoveResponseDto getMoveById(Long moveId) {
        GameMove move = gameMoveRepository.findById(moveId)
                .orElseThrow(() -> new RuntimeException("Move not found with ID: " + moveId));
        return convertToDTO(move);
    }

    // DTO 변환
    private GameMoveResponseDto convertToDTO(GameMove gameMove) {
        return new GameMoveResponseDto(
                gameMove.getId(),
                "Details for move ID: " + gameMove.getId()
        );
    }

    public GameMoveResponseDto makeMove(String token, GameMoveRequestDto requestDto) {
        // 게임 이동 로직 추가
        return new GameMoveResponseDto("success", "Move applied", "{}");

    }

}
