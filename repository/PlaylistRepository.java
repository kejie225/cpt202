package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.Playlist;
import com.cpt202.music_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByCreator(User creator);

    @Query("SELECT p FROM Playlist p WHERE p.title LIKE %:keyword%")
    List<Playlist> searchByTitle(@Param("keyword") String keyword);

    @Query("SELECT p FROM Playlist p WHERE p.style = :style")
    List<Playlist> findByStyle(@Param("style") String style);

    @Query("SELECT p FROM Playlist p WHERE p.creator = :creator AND p.title = :title")
    List<Playlist> findByCreatorAndTitle(@Param("creator") User creator, @Param("title") String title);

    // 从 CPT MUSIC-421 添加
    List<Playlist> findByCreatorId(Long creatorId);
}