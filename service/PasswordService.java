package com.cpt202.music_management.service;

public interface PasswordService {
    String checkPasswordStrength(String password);
    boolean validateEmail(String email);
} 