package com.cpt202.music_management.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private Date birthday;
    private String region;
    private String signature;
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

    private String onlineTime; // 在线时长（如 2小时/天）

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public enum Gender {
        男, 女
    }

    public enum Status {
        active, banned
    }
    
    public enum Role {
        USER, ADMIN
    }
}