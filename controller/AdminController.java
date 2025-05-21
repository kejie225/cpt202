package com.cpt202.music_management.controller;

import com.cpt202.music_management.model.Admin;
import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.model.Playlist;
import com.cpt202.music_management.model.Singer;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.model.User.Status;
import com.cpt202.music_management.model.UserBanHistory;
import com.cpt202.music_management.repository.AdminRepository;
import com.cpt202.music_management.repository.MusicRepository;
import com.cpt202.music_management.repository.PlaylistRepository;
import com.cpt202.music_management.repository.SingerRepository;
import com.cpt202.music_management.repository.UserBanHistoryRepository;
import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MusicRepository musicRepository;
    
    @Autowired
    private PlaylistRepository playlistRepository;
    
    @Autowired
    private SingerRepository singerRepository;
    
    @Autowired
    private UserBanHistoryRepository userBanHistoryRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private AdminService adminService;

    // 获取用户总数
    @GetMapping("/api/users/count")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userRepository.count());
    }
    
    // 获取歌手总数
    @GetMapping("/api/singers/count")
    public ResponseEntity<Long> getSingerCount() {
        return ResponseEntity.ok(singerRepository.count());
    }
    
    // 获取歌曲总数
    @GetMapping("/api/songs/count")
    public ResponseEntity<Long> getSongCount() {
        return ResponseEntity.ok(musicRepository.count());
    }
    
    // 获取播放列表总数
    @GetMapping("/api/playlists/count")
    public ResponseEntity<Long> getPlaylistCount() {
        return ResponseEntity.ok(playlistRepository.count());
    }
    
    // 获取用户列表 (带分页和搜索)
    @GetMapping("/api/users/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        
        try {
            Pageable paging = PageRequest.of(page, size);
            Page<User> userPage;
            
            // 检查是否有关键字搜索和状态筛选
            if (keyword != null && !keyword.isEmpty()) {
                if (status != null && !status.isEmpty() && !status.equals("All")) {
                    Status userStatus = Status.valueOf(status.toLowerCase());
                    userPage = userRepository.findByUsernameContainingAndStatus(keyword, userStatus, paging);
                } else {
                    userPage = userRepository.findByUsernameContaining(keyword, paging);
                }
            } else if (status != null && !status.isEmpty() && !status.equals("All")) {
                Status userStatus = Status.valueOf(status.toLowerCase());
                userPage = userRepository.findByStatus(userStatus, paging);
            } else {
                userPage = userRepository.findAll(paging);
            }
            
            List<User> users = userPage.getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("content", users);
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 禁封用户
    @PostMapping("/api/users/{userId}/ban")
    public ResponseEntity<?> banUser(
            @PathVariable Long userId,
            @RequestParam("reason") String reason,
            @RequestParam("duration") String duration) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // 设置用户为禁封状态
            user.setStatus(Status.banned);
            userRepository.save(user);
            
            // 创建禁封记录
            UserBanHistory banHistory = new UserBanHistory();
            banHistory.setUserId(userId);
            banHistory.setReason(reason);
            banHistory.setBanTime(LocalDateTime.now());
            banHistory.setDuration(duration);
            
            userBanHistoryRepository.save(banHistory);
            
            return ResponseEntity.ok(Map.of("message", "User banned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // 解封用户
    @PostMapping("/api/users/{userId}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable Long userId) {
        try {
            System.out.println("开始解封用户，ID: " + userId);
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            
            System.out.println("找到用户: " + user.getUsername() + ", 当前状态: " + user.getStatus());
            
            // 更新用户状态
            user.setStatus(Status.active);
            userRepository.save(user);
            System.out.println("已将用户状态更改为: " + user.getStatus());
            
            // 创建一个解封记录
            UserBanHistory unbanHistory = new UserBanHistory();
            unbanHistory.setUserId(userId);
            unbanHistory.setReason("管理员手动解封");
            unbanHistory.setBanTime(LocalDateTime.now());
            unbanHistory.setDuration("0");  // 表示解封
            userBanHistoryRepository.save(unbanHistory);
            System.out.println("已创建解封记录");
            
            return ResponseEntity.ok(Map.of(
                "message", "User unbanned successfully", 
                "username", user.getUsername(), 
                "status", user.getStatus()
            ));
        } catch (Exception e) {
            System.err.println("解封用户失败, ID: " + userId + ", 错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 