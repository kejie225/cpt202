package com.cpt202.music_management.controller;

import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api/site")
public class SiteLoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(SiteLoginController.class);

    public SiteLoginController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        
        logger.info("Login attempt with email/username: {}", email);
        logger.debug("Received password length: {}", password != null ? password.length() : 0);
        
        Map<String, Object> response = new HashMap<>();
        
        if (email == null || password == null) {
            logger.warn("Login attempt failed: email or password is null");
            response.put("message", "邮箱和密码不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        // 先尝试通过邮箱查找用户
        Optional<User> userByEmail = userRepository.findByEmail(email);
        logger.debug("User found by email: {}", userByEmail.isPresent());
        
        // 如果通过邮箱没找到，尝试通过用户名查找
        Optional<User> userByUsername = userRepository.findByUsername(email);
        logger.debug("User found by username: {}", userByUsername.isPresent());
        
        // 使用找到的第一个用户（邮箱或用户名）
        Optional<User> userOpt = userByEmail.isPresent() ? userByEmail : userByUsername;

        if (!userOpt.isPresent()) {
            logger.warn("User not found with email/username: {}", email);
            response.put("message", "用户不存在，请先注册");
            return ResponseEntity.status(404).body(response);
        }

        User user = userOpt.get();
        logger.debug("Found user: {}, stored password hash: {}", user.getUsername(), user.getPassword());
        logger.debug("Attempting to verify password: input length={}, stored hash length={}", 
                    password.length(), user.getPassword().length());
        
        // 验证密码
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());
        logger.debug("Password verification result: {}, input password: {}, stored hash: {}", 
                    passwordMatches, password, user.getPassword());
        
        if (!passwordMatches) {
            logger.warn("Invalid password for user: {}", user.getUsername());
            response.put("message", "密码错误，请重试");
            response.put("status", "error");
            response.put("code", 401);
            return ResponseEntity.status(401).body(response);
        }

        // 更新最后登录时间
        user.setLastLogin(new Date());
        userRepository.save(user);
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user);
        logger.debug("Generated JWT token for user: {}", user.getUsername());
        
        // 返回登录成功信息
        response.put("message", "登录成功");
        response.put("status", "success");
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("userId", user.getId());
        response.put("redirect", "/home.html");
        
        logger.info("Login successful for user: {}", user.getUsername());
        
        return ResponseEntity.ok(response);
    }
}