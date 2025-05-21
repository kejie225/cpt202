package com.cpt202.music_management.service;

import com.cpt202.music_management.model.Playlist;

import java.util.List;

public interface PlaylistService {
    List<Playlist> getAllPlaylists();

    List<Playlist> getPlaylistsByUserId(Long userId);

    Playlist getPlaylistById(Long id);

    Playlist createPlaylist(Playlist playlist);

    Playlist updatePlaylist(Playlist playlist);

    void deletePlaylist(Long id);

    long countPlaylists();

    void addSongToPlaylist(Long playlistId, Long songId);
}