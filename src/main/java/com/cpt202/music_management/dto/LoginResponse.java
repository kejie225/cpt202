package com.cpt202.music_management.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String message;

    public LoginResponse(String token, String username, String message) {
        this.token = token;
        this.username = username;
        this.message = message;
    }
} 