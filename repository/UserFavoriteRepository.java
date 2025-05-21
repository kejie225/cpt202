package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.UserFavorite;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.model.Music;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    List<UserFavorite> findByUser(User user);
    Page<UserFavorite> findByUser(User user, Pageable pageable);
    List<UserFavorite> findByMusic(Music music);
    Optional<UserFavorite> findByUserAndMusic(User user, Music music);
    boolean existsByUserAndMusic(User user, Music music);
    void deleteByUserAndMusic(User user, Music music);
} 