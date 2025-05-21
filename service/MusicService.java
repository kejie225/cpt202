package com.cpt202.music_management.service;

import com.cpt202.music_management.model.Music;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MusicService {
    List<Music> getAllSongs();

    List<Music> getSongsBySingerId(Long id);

    Music getSongById(Long id);

    Music createSong(Music song);

    Music updateSong(Music song);

    void deleteSong(Long id);

    long countSongs();

    void approveSong(Long id);

    void rejectSong(Long id, String reason);

    Page<Music> searchSongs(int page, int size, String keyword, String status);

    List<Music> getAllMusic();

    Music getMusicById(Long id);

    Music createMusic(Music music);

    Music updateMusic(Long id, Music music);

    void deleteMusic(Long id);

    Page<Music> searchMusic(int page, int size, String keyword, String status);

    Music approveMusic(Long id);

    Music rejectMusic(Long id, String reason);
}