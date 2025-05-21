package com.cpt202.music_management.controller;

import com.cpt202.music_management.dto.UserBanDTO;
import com.cpt202.music_management.service.UserBanHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bans")
public class BanController {

    private final UserBanHistoryService service;

    public BanController(UserBanHistoryService service) {
        this.service = service;
    }

    // GET 所有封禁记录
    @GetMapping
    public List<UserBanDTO> getAllBanRecords() {
        // 直接返回包含用户名的DTO对象
        return service.getAllBanRecordsWithUsername();
    }
} 