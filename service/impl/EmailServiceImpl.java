package com.cpt202.music_management.service.impl;

import com.cpt202.music_management.model.EmailVerification;
import com.cpt202.music_management.repository.EmailVerificationRepository;
import com.cpt202.music_management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailVerificationRepository verificationRepository;

    @Override
    public String sendVerificationCode(String to) {
        // 检查是否在短时间内重复发送
        Optional<EmailVerification> lastVerification = verificationRepository.findTopByEmailOrderByExpireTimeDesc(to);
        if (lastVerification.isPresent()) {
            Date now = new Date();
            Date lastTime = lastVerification.get().getExpireTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastTime);
            calendar.add(Calendar.MINUTE, -EmailVerification.VERIFICATION_CODE_EXPIRE_MINUTES);
            Date sendTime = calendar.getTime();
            
            // 如果上次发送距离现在不到1分钟，拒绝发送
            if (now.getTime() - sendTime.getTime() < 60000) {
                return null;
            }
        }

        // 生成6位随机验证码
        String code = generateVerificationCode();
        
        // 调试用，打印验证码到控制台
        System.out.println("==================================================");
        System.out.println("发送到 " + to + " 的验证码: " + code);
        System.out.println("==================================================");
        
        // 设置过期时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, EmailVerification.VERIFICATION_CODE_EXPIRE_MINUTES);
        Date expireTime = calendar.getTime();
        
        // 保存验证码记录
        EmailVerification verification = new EmailVerification();
        verification.setEmail(to);
        verification.setCode(code);
        verification.setExpireTime(expireTime);
        verification.setUsed(false);
        verificationRepository.save(verification);
        
        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("18806208821@163.com");
        message.setTo(to);
        message.setSubject("Melodies音乐管理系统 - 验证码");
        message.setText("您的注册验证码是: " + code + "，有效期5分钟。\n\n如果这不是您的操作，请忽略此邮件。");
        
        try {
            mailSender.send(message);
            System.out.println("邮件发送成功: " + to);
        } catch (Exception e) {
            System.err.println("邮件发送失败: " + e.getMessage());
            e.printStackTrace();
            // 即使邮件发送失败，也返回验证码，方便调试
        }
        
        return code;
    }

    @Override
    public boolean verifyCode(String email, String code) {
        System.out.println("验证码验证: 邮箱=" + email + ", 验证码=" + code);
        Optional<EmailVerification> verification = verificationRepository.findValidVerificationCode(email, code, new Date());
        
        if (verification.isPresent()) {
            System.out.println("找到有效验证码记录: ID=" + verification.get().getId());
            // 标记验证码为已使用
            EmailVerification verificationEntity = verification.get();
            verificationEntity.setUsed(true);
            verificationRepository.save(verificationEntity);
            System.out.println("验证码已标记为已使用");
            return true;
        }
        
        System.out.println("未找到有效验证码记录或验证码已过期/已使用");
        // 输出当前所有与该邮箱相关的验证码记录，用于调试
        List<EmailVerification> allCodes = verificationRepository.findAll();
        allCodes.stream()
                .filter(v -> v.getEmail().equals(email))
                .forEach(v -> System.out.println("邮箱 " + email + " 的验证码记录: " +
                        "ID=" + v.getId() + 
                        ", 验证码=" + v.getCode() + 
                        ", 过期时间=" + v.getExpireTime() + 
                        ", 是否已使用=" + v.isUsed()));
        
        return false;
    }
    
    /**
     * 生成6位数字验证码
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
} 