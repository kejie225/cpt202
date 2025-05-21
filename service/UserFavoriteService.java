package com.cpt202.music_management.service;

import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.model.UserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserFavoriteService {
    List<UserFavorite> getUserFavorites(Long userId);
    Page<UserFavorite> getUserFavoritesPaged(Long userId, Pageable pageable);
    UserFavorite addFavorite(Long userId, Long musicId);
    void removeFavorite(Long userId, Long musicId);
    boolean isFavorite(Long userId, Long musicId);
    long countUserFavorites(Long userId);
} 