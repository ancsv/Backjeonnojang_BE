package com.example.backjeon_BE.service;

import com.example.backjeon_BE.dto.request.GameRoomRequestDto;
import com.example.backjeon_BE.dto.response.GameRoomResponseDto;
import com.example.backjeon_BE.entity.GameRoom;
import com.example.backjeon_BE.entity.Match;
import com.example.backjeon_BE.entity.User;
import com.example.backjeon_BE.repository.GameRoomRepository;
import com.example.backjeon_BE.repository.MatchRepository;
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
    private final MatchRepository matchRepository;
    private final JwtProvider jwtProvider;

    @Autowired
    public GameRoomService(GameRoomRepository gameRoomRepository,
                           UserRepository userRepository,
                           MatchRepository matchRepository,
                           JwtProvider jwtProvider) {
        this.gameRoomRepository = gameRoomRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
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

    // 게임방 참가자 검증 메서드
    @Transactional(readOnly = true)
    public boolean isParticipant(String roomId, String email) {
        try {
            Long roomIdLong = Long.parseLong(roomId);

            //게임방 존재 여부 확인
            if (!gameRoomRepository.existsById(roomIdLong)) {
                return false;
            }

            //이메일로 User 조회
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                return false;
            }

            //해당 roomId를 가진 Match들 조회
            List<Match> matches = matchRepository.findByRoomId(roomIdLong);

            //그 중에 현재 userId가 있는지 확인
            return matches.stream()
                    .anyMatch(match -> match.getUserId().equals(user.getId()));

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Long getUserIdFromToken(String token) {
        return jwtProvider.getUserIdFromToken(token);
    }
}