package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.model.Playlist;
import com.cpt202.music_management.model.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {
    List<PlaylistSong> findByPlaylist(Playlist playlist);
    void deleteByPlaylistAndSong(Playlist playlist, Music song);
} 