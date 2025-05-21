package com.cpt202.music_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageForwardController {

    @GetMapping("/")
    public String forwardToIndex() {
        return "forward:/index.html"; // 确保 index.html 存在于静态资源目录
    }
}