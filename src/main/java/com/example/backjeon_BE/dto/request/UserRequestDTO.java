// Request용
package com.example.backjeon_BE.dto.request;

public class UserRequestDTO {
    private String email;
    private int rating;

    // Getters and Setters
    public String getUserName() {
        return email;
    }

    public void setUserName(String userName) {
        this.email = userName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}