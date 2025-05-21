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
        a1.put("title", "System Maintenance Notice");
        a1.put("date", new java.util.Date());
        a1.put("content", "System maintenance will take place this Friday evening, during which some features will be unavailable.");
        list.add(a1);
        Map<String, Object> a2 = new HashMap<>();
        a2.put("title", "New Features Released");
        a2.put("date", new java.util.Date());
        a2.put("content", "New features such as playlist sharing and music recommendations are now available. Enjoy exploring!");
        list.add(a2);
        return list;
    }
} 