package com.cpt202.music_management.controller;

import com.cpt202.music_management.model.Admin;
import com.cpt202.music_management.repository.AdminRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminRegisterController {

    private final AdminRepository adminRepository;

    public AdminRegisterController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        try {
            // Input validation
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username cannot be empty");
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password cannot be empty");
            }

            // Check if username exists
            if (adminRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            // Create new admin
            Admin admin = new Admin();
            admin.setUsername(username.trim());
            admin.setPassword(password);

            // Save admin
            adminRepository.save(admin);
            return ResponseEntity.ok("Registration successful");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }
    
    // 添加JSON格式请求处理
    @PostMapping(value = "/register/json", consumes = "application/json")
    public ResponseEntity<?> registerAdminJson(@RequestBody Map<String, String> adminData) {
        String username = adminData.get("username");
        String password = adminData.get("password");
        
        try {
            // Input validation
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username cannot be empty"));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password cannot be empty"));
            }

            // Check if username exists
            if (adminRepository.findByUsername(username).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
            }

            // Create new admin
            Admin admin = new Admin();
            admin.setUsername(username.trim());
            admin.setPassword(password);

            // Save admin
            adminRepository.save(admin);
            return ResponseEntity.ok(Map.of("message", "Registration successful"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }
}
