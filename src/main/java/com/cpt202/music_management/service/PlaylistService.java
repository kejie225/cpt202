package com.cpt202.music_management.service;

import com.cpt202.music_management.dto.CreatePlaylistDTO;
import com.cpt202.music_management.dto.PlaylistDTO;
import com.cpt202.music_management.dto.UpdatePlaylistDTO;
import com.cpt202.music_management.model.Playlist;

import java.util.List;

public interface PlaylistService {
    List<PlaylistDTO> getAllPlaylists();
    
    PlaylistDTO getPlaylistById(Long id);
    
    List<PlaylistDTO> getPlaylistsByCreator(String username);
    
    List<PlaylistDTO> searchPlaylists(String keyword);
    
    List<PlaylistDTO> getPlaylistsByStyle(String style);
    
    List<PlaylistDTO> getPlaylistsBySong(Long songId);
    
    PlaylistDTO createPlaylist(CreatePlaylistDTO createPlaylistDTO);
    
    PlaylistDTO updatePlaylist(Long id, UpdatePlaylistDTO updatePlaylistDTO);
    
    void deletePlaylist(Long id);
    
    void addSongToPlaylist(Long playlistId, Long songId);
    
    void removeSongFromPlaylist(Long playlistId, Long songId);
} 