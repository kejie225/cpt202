package com.cpt202.music_management.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import com.cpt202.music_management.service.PasswordService;

@RestController
@RequestMapping("/api")
public class password_controller {

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/validate-password")
    public ResponseEntity<?> validatePassword(@RequestBody String password) {
        try {
            String strength = passwordService.checkPasswordStrength(password);
            return ResponseEntity.ok(strength);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/validate-email")
    public ResponseEntity<?> validateEmail(@RequestBody String email) {
        try {
            boolean isValid = passwordService.validateEmail(email);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
