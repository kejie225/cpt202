package com.cpt202.music_management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.service.UserService;
import com.cpt202.music_management.dto.LoginRequest;
import com.cpt202.music_management.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {
        logger.info("Attempting to register user: {}", user.getUsername());

        // Validate username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            logger.error("Username is empty");
            throw new RuntimeException("Username cannot be empty");
        }

        // Validate email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            logger.error("Email is empty");
            throw new RuntimeException("Email cannot be empty");
        }

        // Validate password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            logger.error("Password is empty");
            throw new RuntimeException("Password cannot be empty");
        }

        // Check if username exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.error("Username already exists: {}", user.getUsername());
            throw new RuntimeException("Username already exists");
        }

        // Check if email exists
        if (userRepository.findByEmail(user.getEmail()) != null) {
            logger.error("Email already exists: {}", user.getEmail());
            throw new RuntimeException("Email already exists");
        }

        // 密码加密处理 - 关键修改部分
        String rawPassword = user.getPassword(); // 获取原始密码
        String encodedPassword = passwordEncoder.encode(rawPassword); // 使用BCrypt加密
        user.setPassword(encodedPassword); // 设置加密后的密码

        // Save user
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());

        return savedUser;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("Attempting to login user: {}", request.getUsername());
        
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            logger.error("User not found: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.error("Invalid password for user: {}", request.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
        
        // Create login response
        LoginResponse response = new LoginResponse("dummy-token", user.getUsername(), "Login successful");
        
        logger.info("User logged in successfully: {}", user.getUsername());
        return response;
    }

    @Override
    public User handleGoogleCallback(String code) {
        // TODO: Implement Google OAuth callback handling
        // This would involve:
        // 1. Exchange code for tokens
        // 2. Get user info from Google
        // 3. Create or update user in database
        throw new UnsupportedOperationException("Google OAuth not implemented yet");
    }

    @Override
    public User login(String username, String password) {
        logger.info("Attempting to login user: {}", username);
        
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is empty");
            throw new RuntimeException("Username cannot be empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            logger.error("Password is empty");
            throw new RuntimeException("Password cannot be empty");
        }
        
        // Find user by username
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.error("User not found: {}", username);
            throw new RuntimeException("User not found");
        }
        
        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("Invalid password for user: {}", username);
            throw new RuntimeException("Invalid password");
        }
        
        logger.info("User logged in successfully: {}", username);
        return user;
    }
} 