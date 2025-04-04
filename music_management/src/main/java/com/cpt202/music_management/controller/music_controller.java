package com.cpt202.music_management.controller;
import java.util.List;     // 导入List接口
import java.util.ArrayList; // 导入ArrayList实现类

import com.cpt202.music_management.repository.MusicRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.cpt202.music_management.model.Music;

@RestController//Springboot annotation
@RequestMapping("/api")
public class music_controller {
    @Autowired//用法:
    private MusicRepo musicRepo;
    @GetMapping("/hello")
    public List<Music> SayHallo(){
        List <Music> musics = new ArrayList<>();
        Music music1 = new Music("kejie",12,"kejie");
        musics.add(music1);
        Music music2 = new Music("changbo",12,"kejie");
        musics.add(music2);
        return musics;
    }
    @PostMapping("/api/music")
    public void saveMusic(@RequestBody Music music){
        musicRepo.save(music);

    }


}
