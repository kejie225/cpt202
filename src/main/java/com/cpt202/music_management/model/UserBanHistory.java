package com.cpt202.music_management.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_ban_history") // 对应数据库中的表名
public class UserBanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // 被封禁的用户 ID

    private String reason; // 封禁原因

    private LocalDateTime banTime; // 封禁时间

    private String duration; // 封禁时长（如 1h、1d、永久）
}
