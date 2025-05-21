package com.cpt202.music_management.service.impl;

import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.repository.MusicRepository;
import com.cpt202.music_management.service.MusicService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MusicServiceImpl implements MusicService {

    private final MusicRepository musicRepository;

    public MusicServiceImpl(MusicRepository musicRepository) {
        this.musicRepository = musicRepository;
    }

    @Override
    public List<Music> getAllSongs() {
        return musicRepository.findAll();
    }

    @Override
    public List<Music> getSongsBySingerId(Long id) {
        return musicRepository.findBySingerId(id);
    }

    @Override
    public Music getSongById(Long id) {
        return musicRepository.findById(id).orElse(null);
    }

    @Override
    public Music createSong(Music song) {
        // Set initial status to Pending for new songs
        if (song.getStatus() == null) {
            song.setStatus("Pending");
        }
        return musicRepository.save(song);
    }

    @Override
    public Music updateSong(Music song) {
        // Make sure the song exists before updating
        Optional<Music> existingSong = musicRepository.findById(song.getId());
        if (existingSong.isPresent()) {
            // You may want to reset status to Pending after edits
            if (!existingSong.get().getStatus().equals(song.getStatus())) {
                song.setStatus("Pending");
            }
            return musicRepository.save(song);
        }
        return null;
    }

    @Override
    public void deleteSong(Long id) {
        musicRepository.deleteById(id);
    }

    @Override
    public long countSongs() {
        return musicRepository.count();
    }

    @Override
    public void approveSong(Long id) {
        Optional<Music> song = musicRepository.findById(id);
        if (song.isPresent()) {
            Music updatedSong = song.get();
            updatedSong.setStatus("Approved");
            updatedSong.setReason(null); // Clear any rejection reason
            musicRepository.save(updatedSong);
        }
    }

    @Override
    public void rejectSong(Long id, String reason) {
        Optional<Music> song = musicRepository.findById(id);
        if (song.isPresent()) {
            Music updatedSong = song.get();
            updatedSong.setStatus("Rejected");
            updatedSong.setReason(reason);
            musicRepository.save(updatedSong);
        }
    }

    @Override
    public Page<Music> searchSongs(int page, int size, String keyword, String status) {
        Specification<Music> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("album")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("type")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.join("singer").get("name")), "%" + keyword.toLowerCase() + "%")
                ));
            }

            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All")) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return musicRepository.findAll(spec, PageRequest.of(page, size));
    }

    // Additional helper methods for the test
    @Override
    public List<Music> getAllMusic() {
        return musicRepository.findAll();
    }

    @Override
    public Music getMusicById(Long id) {
        return musicRepository.findById(id).orElse(null);
    }

    @Override
    public Music createMusic(Music music) {
        return musicRepository.save(music);
    }

    @Override
    public Music updateMusic(Long id, Music music) {
        // 修改此方法以与测试预期一致
        Optional<Music> existingMusic = musicRepository.findById(id);
        if (existingMusic.isPresent()) {
            return musicRepository.save(music);
        }
        return null;
    }

    @Override
    public void deleteMusic(Long id) {
        musicRepository.deleteById(id);
    }

    @Override
    public Page<Music> searchMusic(int page, int size, String keyword, String status) {
        return searchSongs(page, size, keyword, status);
    }

    @Override
    public Music approveMusic(Long id) {
        Optional<Music> music = musicRepository.findById(id);
        if (music.isPresent()) {
            Music updatedMusic = music.get();
            updatedMusic.setStatus("Approved");
            updatedMusic.setReason(null); // Clear any rejection reason
            return musicRepository.save(updatedMusic);
        }
        return null;
    }

    @Override
    public Music rejectMusic(Long id, String reason) {
        Optional<Music> music = musicRepository.findById(id);
        if (music.isPresent()) {
            Music updatedMusic = music.get();
            updatedMusic.setStatus("Rejected");
            updatedMusic.setReason(reason);
            return musicRepository.save(updatedMusic);
        }
        return null;
    }
} 