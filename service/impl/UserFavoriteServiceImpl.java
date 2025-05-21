package com.cpt202.music_management.service.impl;

import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.model.UserFavorite;
import com.cpt202.music_management.repository.UserFavoriteRepository;
import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.repository.MusicRepository;
import com.cpt202.music_management.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserFavoriteServiceImpl implements UserFavoriteService {

    @Autowired
    private UserFavoriteRepository userFavoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicRepository musicRepository;

    @Override
    public List<UserFavorite> getUserFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userFavoriteRepository.findByUser(user);
    }

    @Override
    public Page<UserFavorite> getUserFavoritesPaged(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userFavoriteRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional
    public UserFavorite addFavorite(Long userId, Long musicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("Music not found"));

        if (userFavoriteRepository.existsByUserAndMusic(user, music)) {
            throw new RuntimeException("Music already in favorites");
        }

        UserFavorite favorite = new UserFavorite();
        favorite.setUser(user);
        favorite.setMusic(music);
        favorite.setCreatedAt(new Date());

        return userFavoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long musicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("Music not found"));

        userFavoriteRepository.deleteByUserAndMusic(user, music);
    }

    @Override
    public boolean isFavorite(Long userId, Long musicId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new RuntimeException("Music not found"));

        return userFavoriteRepository.existsByUserAndMusic(user, music);
    }

    @Override
    public long countUserFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userFavoriteRepository.findByUser(user).size();
    }
} 