package com.cpt202.music_management.controller;

import com.cpt202.music_management.model.User;
import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.service.UserService;
import com.cpt202.music_management.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@RestController
@RequestMapping("/api/site")
public class SiteRegisterController {

    private static final Logger logger = LoggerFactory.getLogger(SiteRegisterController.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public SiteRegisterController(
            UserRepository userRepository, 
            PasswordEncoder passwordEncoder,
            UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    /**
     * 原始注册方法，保留向后兼容
     */
    @PostMapping("/register-old")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            logger.info("尝试使用老方法注册新用户: {} 和邮箱: {}", user.getUsername(), user.getEmail());

            // 检查用户名或邮箱是否重复
            if (userRepository.existsByUsername(user.getUsername())) {
                logger.warn("注册失败: 用户名 {} 已存在", user.getUsername());
                return ResponseEntity.badRequest().body("用户名已存在");
            }

            if (userRepository.existsByEmail(user.getEmail())) {
                logger.warn("注册失败: 邮箱 {} 已注册", user.getEmail());
                return ResponseEntity.badRequest().body("邮箱已注册");
            }

            // 加密密码
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            logger.debug("密码加密成功");

            // 设置默认状态和注册时间
            user.setStatus(User.Status.active);
            user.setCreatedAt(new Date());

            // 保存用户
            User savedUser = userRepository.save(user);
            logger.info("用户注册成功，ID: {}", savedUser.getId());

            return ResponseEntity.ok("注册成功");
        } catch (Exception e) {
            logger.error("注册失败，错误: ", e);
            return ResponseEntity.status(500).body("注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 带验证码的新注册方法
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerWithVerification(@RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("尝试带验证码注册新用户: {} 和邮箱: {}", registerRequest.getUsername(), registerRequest.getEmail());
            
            User user = userService.registerUserWithVerification(registerRequest);
            logger.info("用户注册成功，ID: {}", user.getId());
            
            return ResponseEntity.ok("注册成功");
        } catch (Exception e) {
            // 记录详细错误信息
            logger.error("注册失败，错误: {}", e.getMessage(), e);
            e.printStackTrace();
            
            // 确保返回具体的错误消息
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                errorMessage = "注册失败：未知错误";
            }
            
            // 返回 400 Bad Request 状态码和错误消息
            return ResponseEntity.badRequest().body(errorMessage);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            logger.info("尝试获取用户信息，ID: {}", userId);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("未找到ID为" + userId + "的用户"));
            logger.info("找到用户: {}", user.getUsername());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("获取用户信息出错: ", e);
            return ResponseEntity.status(500).body("获取用户信息出错: " + e.getMessage());
        }
    }
}