package com.cpt202.music_management.dto;

import lombok.Data;
import java.util.List;
import com.cpt202.music_management.model.Music;

@Data
public class MusicsResponse {
    private List<Music> music;
    private int totalPages;
    private long totalElements;

    public MusicsResponse(List<Music> music, int totalPages, long totalElements) {
        this.music = music;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}