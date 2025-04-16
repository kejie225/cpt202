package com.cpt202.music_management.service.impl;

import com.cpt202.music_management.dto.CreatePlaylistDTO;
import com.cpt202.music_management.dto.PlaylistDTO;
import com.cpt202.music_management.dto.UpdatePlaylistDTO;
import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.model.Playlist;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.repository.MusicRepository;
import com.cpt202.music_management.repository.PlaylistRepository;
import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicRepository musicRepository;

    @Override
    public List<PlaylistDTO> getAllPlaylists() {
        return playlistRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlaylistDTO getPlaylistById(Long id) {
        return playlistRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public List<PlaylistDTO> getPlaylistsByCreator(String username) {
        return playlistRepository.findByCreatorUsername(username).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistDTO> searchPlaylists(String keyword) {
        return playlistRepository.findByTitleContaining(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistDTO> getPlaylistsByStyle(String style) {
        return playlistRepository.findByStyle(style).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlaylistDTO> getPlaylistsBySong(Long songId) {
        return playlistRepository.findBySongId(songId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlaylistDTO createPlaylist(CreatePlaylistDTO createPlaylistDTO) {
        User creator = userRepository.findByUsername(createPlaylistDTO.getCreatorUsername());
        if (creator == null) {
            throw new RuntimeException("User not found");
        }

        Playlist playlist = new Playlist();
        playlist.setTitle(createPlaylistDTO.getTitle());
        playlist.setDescription(createPlaylistDTO.getDescription());
        playlist.setStyle(createPlaylistDTO.getStyle());
        playlist.setCoverUrl(createPlaylistDTO.getCoverUrl());
        playlist.setCreator(creator);

        if (createPlaylistDTO.getSongIds() != null) {
            List<Music> songs = musicRepository.findAllById(createPlaylistDTO.getSongIds());
            playlist.setSongs(songs);
        }

        return convertToDTO(playlistRepository.save(playlist));
    }

    @Override
    @Transactional
    public PlaylistDTO updatePlaylist(Long id, UpdatePlaylistDTO updatePlaylistDTO) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        if (updatePlaylistDTO.getTitle() != null) {
            playlist.setTitle(updatePlaylistDTO.getTitle());
        }
        if (updatePlaylistDTO.getDescription() != null) {
            playlist.setDescription(updatePlaylistDTO.getDescription());
        }
        if (updatePlaylistDTO.getStyle() != null) {
            playlist.setStyle(updatePlaylistDTO.getStyle());
        }
        if (updatePlaylistDTO.getCoverUrl() != null) {
            playlist.setCoverUrl(updatePlaylistDTO.getCoverUrl());
        }
        if (updatePlaylistDTO.getSongIds() != null) {
            List<Music> songs = musicRepository.findAllById(updatePlaylistDTO.getSongIds());
            playlist.setSongs(songs);
        }

        return convertToDTO(playlistRepository.save(playlist));
    }

    @Override
    @Transactional
    public void deletePlaylist(Long id) {
        playlistRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        Music song = musicRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (!playlist.getSongs().contains(song)) {
            playlist.getSongs().add(song);
            playlistRepository.save(playlist);
        }
    }

    @Override
    @Transactional
    public void removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        Music song = musicRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        playlist.getSongs().remove(song);
        playlistRepository.save(playlist);
    }

    private PlaylistDTO convertToDTO(Playlist playlist) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setTitle(playlist.getTitle());
        dto.setDescription(playlist.getDescription());
        dto.setStyle(playlist.getStyle());
        dto.setCoverUrl(playlist.getCoverUrl());
        dto.setCreatorUsername(playlist.getCreator().getUsername());
        dto.setSongIds(playlist.getSongs().stream()
                .map(Music::getId)
                .collect(Collectors.toList()));
        return dto;
    }
} 