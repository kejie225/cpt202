package com.cpt202.music_management.controller;

import com.cpt202.music_management.model.User;
import com.cpt202.music_management.service.UserService;
import com.cpt202.music_management.security.JwtUtil;
import com.cpt202.music_management.dto.LoginRequest;
import com.cpt202.music_management.dto.LoginResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // 用户认证相关接口
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            if (user == null) {
                return ResponseEntity.badRequest().body("用户数据不能为空");
            }
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("注册过程中发生错误");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(new LoginResponse(null, null, null, "用户名和密码不能为空"));
            }

            try {
                User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
                if (user != null) {
                    String token = jwtUtil.generateToken(user);
                    return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getId(), "登录成功"));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new LoginResponse(null, null, null, "用户名或密码错误"));
                }
            } catch (RuntimeException e) {
                // 捕获业务逻辑异常（如用户名或密码错误）并返回401
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, null, null, e.getMessage()));
            }
        } catch (Exception e) {
            // 捕获其他异常（如数据库连接错误等）并返回500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(null, null, null, "登录过程中发生错误"));
        }
    }

    // Google OAuth2 登录
    @GetMapping("/auth/google")
    public ResponseEntity<?> googleAuth() {
        String googleAuthUrl = "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=YOUR_CLIENT_ID&" +
                "redirect_uri=http://localhost:8080/api/users/auth/google/callback&" +
                "response_type=code&" +
                "scope=email%20profile";
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", googleAuthUrl)
                .build();
    }

    @GetMapping("/auth/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam String code) {
        try {
            User user = userService.handleGoogleCallback(code);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 用户管理接口
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("用户删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/total")
    public ResponseEntity<Long> countUsers() {
        return ResponseEntity.ok(userService.countUsers());
    }

    // 用户封禁管理
    /*
    @PostMapping("/{id}/ban")
    public ResponseEntity<String> banUser(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam String duration) {
        try {
            userService.banUser(id, reason, duration);
            return ResponseEntity.ok("封禁成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("封禁失败: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/unban")
    public ResponseEntity<String> unbanUser(@PathVariable Long id) {
        try {
            userService.unbanUser(id);
            return ResponseEntity.ok("解封成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("解封失败: " + e.getMessage());
        }
    }
    */

    // 添加用户个人资料接口
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
            }
            
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取用户资料失败: " + e.getMessage());
        }
    }

    // 用户搜索
    @GetMapping("/find")
    public ResponseEntity<?> searchUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(userService.searchUsers(page, size, keyword, status));
    }
    
    // 根据用户名获取用户信息
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "用户不存在"));
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "获取用户信息失败: " + e.getMessage()));
        }
    }
}