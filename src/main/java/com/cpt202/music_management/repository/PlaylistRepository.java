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
    
    List<Playlist> findByTitleContaining(String title);
    
    List<Playlist> findByStyle(String style);
    
    @Query("SELECT p FROM Playlist p WHERE p.creator.username = :username")
    List<Playlist> findByCreatorUsername(@Param("username") String username);
    
    @Query("SELECT p FROM Playlist p JOIN p.songs s WHERE s.id = :songId")
    List<Playlist> findBySongId(@Param("songId") Long songId);
} 