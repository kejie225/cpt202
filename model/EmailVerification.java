package com.cpt202.music_management.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "email_verifications")
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Date expireTime;

    @Column(nullable = false)
    private boolean used;

    // 验证码有效时间（分钟）
    public static final int VERIFICATION_CODE_EXPIRE_MINUTES = 5;
} 