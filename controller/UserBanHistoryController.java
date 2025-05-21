package com.cpt202.music_management.controller;

import com.cpt202.music_management.dto.UserBanDTO;
import com.cpt202.music_management.model.UserBanHistory;
import com.cpt202.music_management.service.UserBanHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ban-history")
public class UserBanHistoryController {

    private final UserBanHistoryService service;

    public UserBanHistoryController(UserBanHistoryService service) {
        this.service = service;
    }

    // GET 所有封禁记录
    @GetMapping
    public List<UserBanHistory> getAllBanRecords() {
        return service.getAllBanRecords();
    }
    
    // 添加返回带有用户名的封禁记录的方法
    @GetMapping("/with-username")
    public List<UserBanDTO> getAllBanRecordsWithUsername() {
        return service.getAllBanRecordsWithUsername();
    }

    // GET 某个用户的封禁记录
    @GetMapping("/user/{userId}")
    public List<UserBanHistory> getRecordsByUser(@PathVariable Long userId) {
        return service.getBanRecordsByUserId(userId);
    }

    // POST 新增封禁记录
    @PostMapping
    public UserBanHistory createBanRecord(@RequestBody UserBanHistory record) {
        return service.createBanRecord(record);
    }
}
