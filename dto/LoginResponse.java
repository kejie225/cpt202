package com.cpt202.music_management.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private Long userId;
    private String message;

    public LoginResponse(String token, String username, Long userId, String message) {
        this.token = token;
        this.username = username;
        this.userId = userId;
        this.message = message;
    }
} 