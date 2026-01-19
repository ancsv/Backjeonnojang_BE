package com.example.backjeon_BE.controller;

import com.example.backjeon_BE.dto.request.GameRoomRequestDto;
import com.example.backjeon_BE.dto.response.GameRoomResponseDto;
import com.example.backjeon_BE.service.GameRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/game/room")
public class GameRoomController {

    private final GameRoomService gameRoomService;

    public GameRoomController(GameRoomService gameRoomService) {
        this.gameRoomService = gameRoomService;
    }

    // 방 생성
    @PostMapping("/create")
    public ResponseEntity<GameRoomResponseDto> createGameRoom(@RequestHeader("Authorization") String token,
                                                              @RequestBody GameRoomRequestDto gameRoomRequestDto) {
        GameRoomResponseDto response = gameRoomService.createGameRoom(token, gameRoomRequestDto);
        return ResponseEntity.ok(response);
    }

    // 모든 방 조회 (옵션)
    @GetMapping("/all")
    public ResponseEntity<List<GameRoomResponseDto>> getAllGameRooms() {
        List<GameRoomResponseDto> response = gameRoomService.getAllGameRooms();
        return ResponseEntity.ok(response);
    }
}
