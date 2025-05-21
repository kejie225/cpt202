package com.cpt202.music_management.service;

import com.cpt202.music_management.model.User;
import com.cpt202.music_management.dto.LoginRequest;
import com.cpt202.music_management.dto.LoginResponse;
import com.cpt202.music_management.dto.RegisterRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    // 用户管理基础操作
    List<User> getAllUsers();              
    User getUserById(Long id);             
    User findByUsername(String username);  // 添加按用户名查找用户的方法
    User createUser(User user);            
    void deleteUser(Long id);              
    long countUsers();                     

    // 用户认证
    User registerUser(User user);
    User registerUserWithVerification(RegisterRequest registerRequest) throws Exception;
    LoginResponse login(LoginRequest request);
    User login(String username, String password);
    User handleGoogleCallback(String code);

    // 用户管理
    void banUser(Long userId, String reason, String duration);  
    void unbanUser(Long userId);                               

    // 搜索功能
    Page<User> searchUsers(int page, int size, String keyword, String status);
}