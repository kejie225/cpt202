package com.cpt202.music_management.controller;

import com.cpt202.music_management.model.Admin;
import com.cpt202.music_management.repository.AdminRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminLoginController {

    private final AdminRepository adminRepository;

    public AdminLoginController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        // 空值校验
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username cannot be empty"));
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password cannot be empty"));
        }

        // 查询用户
        return adminRepository.findByUsername(username.trim())
                .map(admin -> {
                    if (admin.getPassword() != null && admin.getPassword().equals(password)) {
                        return ResponseEntity.ok(Map.of(
                                "message", "Login success",
                                "username", admin.getUsername(),
                                "role", "ADMIN"
                        ));
                    } else {
                        return ResponseEntity.status(401).body(Map.of("message", "Invalid password"));
                    }
                })
                .orElse(ResponseEntity.status(404).body(Map.of("message", "User not found")));
    }
}
