package com.example.backjeon_BE.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private int rating;

    public Long getUserId() {
        return userId;
    }

    public Long getId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getRating() {
        return rating;
    }

    public static User createUser(String userName, String email, String password) {
        User user = new User();
        user.userName = userName;
        user.email = email;
        user.password = password;
        user.role = "USER";  // 기본값
        user.rating = 1000;
        return user;
    }
}