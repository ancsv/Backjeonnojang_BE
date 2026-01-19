package com.example.backjeon_BE.controller;

import com.example.backjeon_BE.dto.response.GameDetailResponseDto;
import com.example.backjeon_BE.dto.response.GameHistoryResponseDto;
import com.example.backjeon_BE.service.GameHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/history")
public class GameHistoryController {

    private final GameHistoryService gameHistoryService;

    public GameHistoryController(GameHistoryService gameHistoryService) {
        this.gameHistoryService = gameHistoryService;
    }

    // 게임 기록 조회
    @GetMapping
    public ResponseEntity<List<GameHistoryResponseDto>> getGameHistory(@RequestHeader("Authorization") String token) {
        List<GameHistoryResponseDto> history = gameHistoryService.getGameHistory(token);
        return ResponseEntity.ok(history);
    }

    // 게임 상세 보기
    @GetMapping("/{gameId}")
    public ResponseEntity<GameDetailResponseDto> getGameDetails(@RequestHeader("Authorization") String token,
                                                                @PathVariable String gameId) {
        GameDetailResponseDto gameDetails = gameHistoryService.getGameDetails(token, gameId);
        return ResponseEntity.ok(gameDetails);
    }
}
