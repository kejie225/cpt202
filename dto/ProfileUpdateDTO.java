package com.cpt202.music_management.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ProfileUpdateDTO {
    private String username;
    private String email;
    private String phone;
    private String gender;
    private Date birthday;
    private String region;
    private String signature;
    private String avatarUrl;
} 