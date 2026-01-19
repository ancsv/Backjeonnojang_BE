package com.example.backjeon_BE.controller;

import com.example.backjeon_BE.dto.request.MatchRequestDto;
import com.example.backjeon_BE.dto.response.MatchStatusResponseDto;
import com.example.backjeon_BE.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/match")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    // 매칭 요청
    @PostMapping
    public ResponseEntity<Map<String, String>> requestMatch(@RequestHeader("Authorization") String token,
                                                            @RequestBody MatchRequestDto matchRequestDto) {
        String status = matchService.requestMatch(token, matchRequestDto.getMode());
        return ResponseEntity.ok(Map.of("status", status, "message", "Looking for an opponent..."));
    }

    // 매칭 상태 확인
    @GetMapping("/status")
    public ResponseEntity<MatchStatusResponseDto> getMatchStatus(@RequestHeader("Authorization") String token) {
        MatchStatusResponseDto matchStatus = matchService.getMatchStatus(token);
        return ResponseEntity.ok(matchStatus);
    }
}
