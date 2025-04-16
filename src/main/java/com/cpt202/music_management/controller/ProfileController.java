package com.cpt202.music_management.controller;

import com.cpt202.music_management.dto.PasswordUpdateDTO;
import com.cpt202.music_management.dto.ProfileUpdateDTO;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            User profile = profileService.getCurrentUserProfile();
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateDTO profileUpdate) {
        try {
            User updatedProfile = profileService.updateProfile(profileUpdate);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDTO passwordUpdate) {
        try {
            profileService.updatePassword(passwordUpdate);
            return ResponseEntity.ok("Password updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("avatar") MultipartFile file) {
        try {
            String avatarUrl = profileService.updateAvatar(file);
            return ResponseEntity.ok(avatarUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount() {
        try {
            profileService.deleteAccount();
            return ResponseEntity.ok("Account deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 