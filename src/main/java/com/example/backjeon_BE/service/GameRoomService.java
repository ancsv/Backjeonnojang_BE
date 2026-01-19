package com.example.backjeon_BE.service;

import com.example.backjeon_BE.dto.request.GameRoomRequestDto;
import com.example.backjeon_BE.dto.response.GameRoomResponseDto;
import com.example.backjeon_BE.entity.GameRoom;
import com.example.backjeon_BE.repository.GameRoomRepository;
import com.example.backjeon_BE.repository.UserRepository;
import com.example.backjeon_BE.security.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public GameRoomService(GameRoomRepository gameRoomRepository, UserRepository userRepository, JwtProvider jwtProvider) {
        this.gameRoomRepository = gameRoomRepository;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Transactional(readOnly = true)
    public List<GameRoomResponseDto> getAllGameRooms() {
        return gameRoomRepository.findAll()
                .stream()
                .map(GameRoomResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GameRoomResponseDto getGameRoomById(Long roomId) {
        GameRoom gameRoom = gameRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Game room not found with ID: " + roomId));
        return GameRoomResponseDto.from(gameRoom);
    }

    @Transactional
    public GameRoomResponseDto createGameRoom(String token, GameRoomRequestDto requestDto) {
        Long userId = getUserIdFromToken(token);

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        GameRoom gameRoom = GameRoom.createRoom(
                requestDto.getRoomName(),
                requestDto.getMaxPlayers() != 0 ? requestDto.getMaxPlayers() : 2
        );

        GameRoom savedGameRoom = gameRoomRepository.save(gameRoom);
        return GameRoomResponseDto.from(savedGameRoom);
    }

    private Long getUserIdFromToken(String token) {
        return jwtProvider.getUserIdFromToken(token);
    }
}