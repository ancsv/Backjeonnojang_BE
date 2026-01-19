package com.example.backjeon_BE.service;

import com.example.backjeon_BE.dto.response.UserResponseDTO;
import com.example.backjeon_BE.entity.User;
import com.example.backjeon_BE.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO userDTO = new UserResponseDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUserName(user.getUserName());
        userDTO.setRating(user.getRating());
        return userDTO;
    }
}
