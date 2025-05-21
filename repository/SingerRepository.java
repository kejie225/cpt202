package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.Singer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SingerRepository extends JpaRepository<Singer, Long> {
    List<Singer> findByName(String name);

    List<Singer> findByRegion(String region);

    // 从 CPT MUSIC-421 添加
    List<Singer> findByGender(Singer.Gender gender);
}