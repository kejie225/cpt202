package com.cpt202.music_management.controller;

import com.cpt202.music_management.dto.CreatePlaylistDTO;
import com.cpt202.music_management.dto.PlaylistDTO;
import com.cpt202.music_management.dto.UpdatePlaylistDTO;
import com.cpt202.music_management.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlist")
public class PlaylistController {
    @Autowired
    private PlaylistService playlistService;

    @GetMapping
    public ResponseEntity<List<PlaylistDTO>> getAllPlaylists() {
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Long id) {
        PlaylistDTO playlist = playlistService.getPlaylistById(id);
        return playlist != null ? ResponseEntity.ok(playlist) : ResponseEntity.notFound().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<PlaylistDTO>> getMyPlaylists(@RequestParam String username) {
        return ResponseEntity.ok(playlistService.getPlaylistsByCreator(username));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlaylistDTO>> searchPlaylists(@RequestParam String keyword) {
        return ResponseEntity.ok(playlistService.searchPlaylists(keyword));
    }

    @GetMapping("/style/{style}")
    public ResponseEntity<List<PlaylistDTO>> getPlaylistsByStyle(@PathVariable String style) {
        return ResponseEntity.ok(playlistService.getPlaylistsByStyle(style));
    }

    @GetMapping("/song/{songId}")
    public ResponseEntity<List<PlaylistDTO>> getPlaylistsBySong(@PathVariable Long songId) {
        return ResponseEntity.ok(playlistService.getPlaylistsBySong(songId));
    }

    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody CreatePlaylistDTO createPlaylistDTO) {
        return ResponseEntity.ok(playlistService.createPlaylist(createPlaylistDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable Long id, @RequestBody UpdatePlaylistDTO updatePlaylistDTO) {
        return ResponseEntity.ok(playlistService.updatePlaylist(id, updatePlaylistDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{playlistId}/song/{songId}")
    public ResponseEntity<Void> addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.addSongToPlaylist(playlistId, songId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{playlistId}/song/{songId}")
    public ResponseEntity<Void> removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
        return ResponseEntity.ok().build();
    }
} 