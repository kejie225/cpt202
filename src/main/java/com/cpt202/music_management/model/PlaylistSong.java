package com.cpt202.music_management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "playlist_songs")
@Getter
@Setter
public class PlaylistSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 正确命名为 playlist（而不是 playlist_id 或 playlistId 混用）
    @ManyToOne
    @JoinColumn(name = "playlist_id")  // 物理列名是 playlist_id
    private Playlist playlist;

    // 正确命名为 song
    @ManyToOne
    @JoinColumn(name = "song_id")
    private Music song;
}
