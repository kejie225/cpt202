package com.cpt202.music_management.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreatePlaylistDTO {
    private String title;
    private String description;
    private String style;
    private String coverUrl;
    private String creatorUsername;
    private List<Long> songIds;
} 