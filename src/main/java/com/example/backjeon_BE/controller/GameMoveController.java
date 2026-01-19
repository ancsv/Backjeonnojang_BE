package com.example.backjeon_BE.controller;

import com.example.backjeon_BE.dto.request.GameMoveRequestDto;
import com.example.backjeon_BE.dto.response.GameMoveResponseDto;
import com.example.backjeon_BE.service.GameMoveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/game/move")
public class GameMoveController {

    private final GameMoveService gameMoveService;

    public GameMoveController(GameMoveService gameMoveService) {
        this.gameMoveService = gameMoveService;
    }

    // 말 이동
    @PostMapping("/")
    public ResponseEntity<GameMoveResponseDto> makeMove(@RequestHeader("Authorization") String token,
                                                    @RequestBody GameMoveRequestDto gameMoveRequestDto) {
        GameMoveResponseDto response = gameMoveService.makeMove(token, gameMoveRequestDto);
        return ResponseEntity.ok(response);
    }
}
