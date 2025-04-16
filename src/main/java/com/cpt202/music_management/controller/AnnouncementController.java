package com.cpt202.music_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    @GetMapping
    public List<Map<String, Object>> getAnnouncements() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> a1 = new HashMap<>();
        a1.put("title", "系统维护通知");
        a1.put("date", new java.util.Date());
        a1.put("content", "本周五晚将进行系统维护，届时部分功能不可用。");
        list.add(a1);
        Map<String, Object> a2 = new HashMap<>();
        a2.put("title", "新功能上线");
        a2.put("date", new java.util.Date());
        a2.put("content", "歌单分享、音乐推荐等新功能已上线，欢迎体验！");
        list.add(a2);
        return list;
    }
} 