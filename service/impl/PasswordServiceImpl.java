package com.cpt202.music_management.service.impl;

import org.springframework.stereotype.Service;
import com.cpt202.music_management.service.PasswordService;
import java.util.regex.Pattern;

@Service
public class PasswordServiceImpl implements PasswordService {

    @Override
    public String checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "Empty";
        }
        if (password.length() < 6) {
            return "Weak";
        }
        if (password.length() < 10) {
            return "Medium";
        }
        if (!Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find() || 
            !Pattern.compile("[A-Z]").matcher(password).find()) {
            return "Strong";
        }
        return "Very Strong";
    }

    @Override
    public boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }
} 