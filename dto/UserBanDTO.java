package com.cpt202.music_management.dto;

import lombok.Data;

@Data
public class UserBanDTO {
    private Long id;
    private String username;
    private String reason;
    private String duration;
    private String date; // 前端用字符串显示时间
}