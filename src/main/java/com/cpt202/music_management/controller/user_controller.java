package com.cpt202.music_management.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.service.UserService;
import com.cpt202.music_management.dto.LoginRequest;
import com.cpt202.music_management.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api")
public class user_controller {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            if (user == null) {
                return ResponseEntity.badRequest().body("User data is required");
            }
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during registration");
        }
    }

    @GetMapping("/auth/google")
    public ResponseEntity<?> googleAuth() {
        // This endpoint will redirect to Google OAuth
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=YOUR_CLIENT_ID&" +
                "redirect_uri=http://localhost:8080/api/auth/google/callback&" +
                "response_type=code&" +
                "scope=email%20profile";
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", googleAuthUrl)
                .build();
    }

    @GetMapping("/auth/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam String code) {
        try {
            // Handle Google OAuth callback
            User user = userService.handleGoogleCallback(code);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body(new LoginResponse(null, null, "Username and password are required"));
            }

            User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            if (user != null) {
                // TODO: Generate JWT token here
                String token = "dummy-token"; // Replace with actual JWT token
                return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), "Login successful"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, null, "Invalid username or password"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(null, null, "An error occurred during login"));
        }
    }
}
