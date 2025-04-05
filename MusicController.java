package com.cpt202.music_management.controller;
import java.util.List;     // 导入List接口
import java.util.ArrayList; // 导入ArrayList实现类

import com.cpt202.music_management.repository.MusicRepo;
import com.cpt202.music_management.specification.MusicSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import com.cpt202.music_management.model.Music;

@RestController//Springboot annotation
@RequestMapping("/api")
public class MusicController {
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
    @PostMapping("/music") // /api/api/music
    public void saveMusic(@RequestBody Music music){
        musicRepo.save(music);
    }
    @GetMapping("/music")
    public Page<Music> getAllMusic(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String author
    ) {
        Sort sort = Sort.by(Sort.Direction.DESC, "view");
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Music> spec = Specification.where(MusicSpec.hasAuthor(keyword))
                .and(MusicSpec.hasName(keyword));

        return musicRepo.findAll(spec, pageable);
    }


}
