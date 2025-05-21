package com.cpt202.music_management.service;

import com.cpt202.music_management.model.Singer;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SingerService {
    List<Singer> getAllSingers();

    Singer getSingerById(Long id);

    Singer createSinger(Singer singer);

    Singer updateSinger(Singer singer);

    void deleteSinger(Long id);

    long countSingers();

    Page<Singer> searchSingers(int page, int size, String keyword, String gender);
}