package com.cpt202.music_management.controller;

import com.cpt202.music_management.model.UserFavorite;
import com.cpt202.music_management.service.UserFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserFavorite>> getUserFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(userFavoriteService.getUserFavorites(userId));
    }

    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<Page<UserFavorite>> getUserFavoritesPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        return ResponseEntity.ok(userFavoriteService.getUserFavoritesPaged(userId, pageable));
    }

    @PostMapping("/user/{userId}/music/{musicId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long userId, @PathVariable Long musicId) {
        try {
            UserFavorite favorite = userFavoriteService.addFavorite(userId, musicId);
            return ResponseEntity.ok(favorite);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/user/{userId}/music/{musicId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long userId, @PathVariable Long musicId) {
        try {
            userFavoriteService.removeFavorite(userId, musicId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/music/{musicId}/check")
    public ResponseEntity<Boolean> checkFavorite(@PathVariable Long userId, @PathVariable Long musicId) {
        return ResponseEntity.ok(userFavoriteService.isFavorite(userId, musicId));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getFavoriteCount(@PathVariable Long userId) {
        return ResponseEntity.ok(userFavoriteService.countUserFavorites(userId));
    }
} 