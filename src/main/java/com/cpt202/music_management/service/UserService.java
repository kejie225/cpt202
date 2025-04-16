package com.cpt202.music_management.service;

import com.cpt202.music_management.model.User;
import com.cpt202.music_management.dto.LoginRequest;
import com.cpt202.music_management.dto.LoginResponse;

public interface UserService {
    User registerUser(User user);
    User login(String username, String password);
    User handleGoogleCallback(String code);
    LoginResponse login(LoginRequest request);
} 