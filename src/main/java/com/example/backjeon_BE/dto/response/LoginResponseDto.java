package com.example.backjeon_BE.dto.response;

import com.example.backjeon_BE.entity.User;

public class LoginResponseDto {
    private String token;
    private String email;
    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static LoginResponseDto from(User user, String token) {
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setUsername(user.getUserName());
        return response;
    }
}