package com.cpt202.music_management.controller;

import com.cpt202.music_management.dto.EmailVerificationRequest;
import com.cpt202.music_management.dto.EmailVerificationResponse;
import com.cpt202.music_management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verification")
public class EmailVerificationController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-code")
    public ResponseEntity<EmailVerificationResponse> sendVerificationCode(@RequestBody EmailVerificationRequest request) {
        try {
            String email = request.getEmail();
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new EmailVerificationResponse(false, "邮箱地址不能为空"));
            }
            
            // 发送验证码
            String code = emailService.sendVerificationCode(email);
            
            if (code == null) {
                return ResponseEntity.ok(new EmailVerificationResponse(false, "请求过于频繁，请1分钟后再试"));
            }
            
            return ResponseEntity.ok(new EmailVerificationResponse(true, "验证码已发送到您的邮箱"));
        } catch (Exception e) {
            return ResponseEntity.ok(new EmailVerificationResponse(false, "验证码发送失败：" + e.getMessage()));
        }
    }
} 