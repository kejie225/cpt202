package com.cpt202.music_management.service;

public interface EmailService {
    
    /**
     * 发送验证码邮件
     * @param to 收件人邮箱
     * @return 生成的验证码
     */
    String sendVerificationCode(String to);
    
    /**
     * 验证邮箱验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 验证是否成功
     */
    boolean verifyCode(String email, String code);
} 