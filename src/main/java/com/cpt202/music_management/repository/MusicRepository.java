package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {
    List<Music> findByTitle(String title);
    List<Music> findByType(String type);
    List<Music> findBySingerId(Long singerId);
} 