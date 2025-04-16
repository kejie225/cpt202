package com.cpt202.music_management.dto;

import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
} 