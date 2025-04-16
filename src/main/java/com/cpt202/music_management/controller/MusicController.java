package com.cpt202.music_management.controller;

import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.repository.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/music")
public class MusicController {
    @Autowired
    private MusicRepository musicRepository;

    @GetMapping
    public ResponseEntity<List<Music>> getAllMusic() {
        return ResponseEntity.ok(musicRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Music> getMusicById(@PathVariable Long id) {
        Optional<Music> music = musicRepository.findById(id);
        return music.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Music>> getMusicByTitle(@PathVariable String title) {
        return ResponseEntity.ok(musicRepository.findByTitle(title));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Music>> getMusicByType(@PathVariable String type) {
        return ResponseEntity.ok(musicRepository.findByType(type));
    }

    @GetMapping("/singer/{singerId}")
    public ResponseEntity<List<Music>> getMusicBySinger(@PathVariable Long singerId) {
        return ResponseEntity.ok(musicRepository.findBySingerId(singerId));
    }

    @PostMapping
    public ResponseEntity<Music> createMusic(@RequestBody Music music) {
        return ResponseEntity.ok(musicRepository.save(music));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Music> updateMusic(@PathVariable Long id, @RequestBody Music musicDetails) {
        if (!musicRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        Music music = musicRepository.findById(id).get();
        music.setTitle(musicDetails.getTitle());
        music.setAlbum(musicDetails.getAlbum());
        music.setLyrics(musicDetails.getLyrics());
        music.setType(musicDetails.getType());
        music.setResourceUrl(musicDetails.getResourceUrl());
        music.setCoverUrl(musicDetails.getCoverUrl());
        music.setDuration(musicDetails.getDuration());
        music.setSinger(musicDetails.getSinger());
        return ResponseEntity.ok(musicRepository.save(music));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMusic(@PathVariable Long id) {
        if (!musicRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        musicRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 