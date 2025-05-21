package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.model.Singer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long>, JpaSpecificationExecutor<Music> {
    List<Music> findByTitle(String title);

    List<Music> findByType(String type);

    List<Music> findBySingerId(Long singerId);
    
    List<Music> findBySinger(Singer singer);

    // 从 CPT MUSIC-421 添加
    List<Music> findByAlbum(String album);
}