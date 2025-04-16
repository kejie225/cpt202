package com.cpt202.music_management.service;

import com.cpt202.music_management.dto.PasswordUpdateDTO;
import com.cpt202.music_management.dto.ProfileUpdateDTO;
import com.cpt202.music_management.model.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    User getCurrentUserProfile();
    User updateProfile(ProfileUpdateDTO profileUpdate);
    void updatePassword(PasswordUpdateDTO passwordUpdate);
    String updateAvatar(MultipartFile file);
    void deleteAccount();
} 