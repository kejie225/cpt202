package com.cpt202.music_management.dto;

import lombok.Data;
import java.util.List;

@Data
public class UpdatePlaylistDTO {
    private String title;
    private String description;
    private String style;
    private String coverUrl;
    private List<Long> songIds;
} 