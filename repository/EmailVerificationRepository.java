package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    
    // 根据邮箱和验证码查找有效的验证码记录
    @Query("SELECT e FROM EmailVerification e WHERE e.email = ?1 AND e.code = ?2 AND e.expireTime > ?3 AND e.used = false")
    Optional<EmailVerification> findValidVerificationCode(String email, String code, Date now);
    
    // 查找某邮箱最近的一条验证码记录
    Optional<EmailVerification> findTopByEmailOrderByExpireTimeDesc(String email);
} 