package com.cpt202.music_management.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "singers")
public class Singer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 歌手名称

    @Enumerated(EnumType.STRING)
    private Gender gender; // 性别：男 / 女

    private Date birthDate; // 出生日期
    private String region; // 地区
    private String description; // 简介
    private String avatarUrl; // 头像地址

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt; // 创建时间

    // 枚举类型：性别
    public enum Gender {
        男, 女
    }
}
