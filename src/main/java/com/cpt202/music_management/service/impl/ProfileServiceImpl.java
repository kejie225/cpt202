package com.cpt202.music_management.service.impl;

import com.cpt202.music_management.dto.PasswordUpdateDTO;
import com.cpt202.music_management.dto.ProfileUpdateDTO;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String uploadDir = "uploads/avatars/";

    @Override
    public User getCurrentUserProfile() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username);
    }

    @Override
    public User updateProfile(ProfileUpdateDTO profileUpdate) {
        User user = getCurrentUserProfile();
        
        if (profileUpdate.getUsername() != null) {
            user.setUsername(profileUpdate.getUsername());
        }
        if (profileUpdate.getEmail() != null) {
            user.setEmail(profileUpdate.getEmail());
        }
        if (profileUpdate.getPhone() != null) {
            user.setPhone(profileUpdate.getPhone());
        }
        if (profileUpdate.getGender() != null) {
            user.setGender(User.Gender.valueOf(profileUpdate.getGender()));
        }
        if (profileUpdate.getBirthday() != null) {
            user.setBirthday(profileUpdate.getBirthday());
        }
        if (profileUpdate.getRegion() != null) {
            user.setRegion(profileUpdate.getRegion());
        }
        if (profileUpdate.getSignature() != null) {
            user.setSignature(profileUpdate.getSignature());
        }
        if (profileUpdate.getAvatarUrl() != null) {
            user.setAvatarUrl(profileUpdate.getAvatarUrl());
        }

        return userRepository.save(user);
    }

    @Override
    public void updatePassword(PasswordUpdateDTO passwordUpdate) {
        User user = getCurrentUserProfile();

        if (!passwordEncoder.matches(passwordUpdate.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (!passwordUpdate.getNewPassword().equals(passwordUpdate.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public String updateAvatar(MultipartFile file) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Update user's avatar URL
            User user = getCurrentUserProfile();
            String avatarUrl = "/uploads/avatars/" + filename;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar", e);
        }
    }

    @Override
    public void deleteAccount() {
        User user = getCurrentUserProfile();
        userRepository.delete(user);
    }
} 