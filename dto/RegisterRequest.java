package com.cpt202.music_management.dto;

import com.cpt202.music_management.model.User;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private User.Gender gender;
    private String region;
    private String signature;
    private String birthday;
    private String verificationCode;  // 验证码字段
} 