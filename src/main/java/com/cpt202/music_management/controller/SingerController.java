package com.cpt202.music_management.controller;

import com.cpt202.music_management.model.Singer;
import com.cpt202.music_management.repository.SingerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/singer")
public class SingerController {
    @Autowired
    private SingerRepository singerRepository;

    @GetMapping
    public ResponseEntity<List<Singer>> getAllSingers() {
        return ResponseEntity.ok(singerRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Singer> getSingerById(@PathVariable Long id) {
        Optional<Singer> singer = singerRepository.findById(id);
        return singer.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Singer>> getSingerByName(@PathVariable String name) {
        return ResponseEntity.ok(singerRepository.findByName(name));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<List<Singer>> getSingerByRegion(@PathVariable String region) {
        return ResponseEntity.ok(singerRepository.findByRegion(region));
    }

    @PostMapping
    public ResponseEntity<Singer> createSinger(@RequestBody Singer singer) {
        return ResponseEntity.ok(singerRepository.save(singer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Singer> updateSinger(@PathVariable Long id, @RequestBody Singer singer) {
        if (!singerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        singer.setId(id);
        return ResponseEntity.ok(singerRepository.save(singer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSinger(@PathVariable Long id) {
        if (!singerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        singerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
} 