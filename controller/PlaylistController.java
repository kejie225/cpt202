package com.cpt202.music_management.controller;

import com.cpt202.music_management.model.Playlist;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.repository.PlaylistRepository;
import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.repository.MusicRepository;
import com.cpt202.music_management.repository.PlaylistSongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/playlist")
public class PlaylistController {
    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    private final String UPLOAD_DIR = "uploads/playlist_covers/";
    private final String PLAYLIST_COVER_DIR = "uploads/playlist_covers/";

    @GetMapping
    public ResponseEntity<List<Playlist>> getAllPlaylists() {
        return ResponseEntity.ok(playlistRepository.findAll());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Playlist>> getMyPlaylists(@RequestParam String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return ResponseEntity.ok(playlistRepository.findByCreator(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable Long id) {
        Optional<Playlist> playlist = playlistRepository.findById(id);
        return playlist.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<?> createPlaylist(
            @RequestParam("title") String title,
            @RequestParam("style") String style,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("isPublic") boolean isPublic,
            @RequestParam(value = "cover", required = false) MultipartFile cover,
            @RequestParam("creator") String username) {

        try {
            // 添加参数诊断日志
            System.out.println("创建歌单请求参数：");
            System.out.println("- 标题: " + title);
            System.out.println("- 风格: " + style);
            System.out.println("- 描述: " + (description != null ? description : "null"));
            System.out.println("- 是否公开: " + isPublic + " (类型: " + isPublic + ")");
            System.out.println("- 创建者: " + username);
            System.out.println("- 有封面: " + (cover != null && !cover.isEmpty()));

            // 参数验证
            if (title == null || title.trim().isEmpty()) {
                System.out.println("错误：歌单标题为空");
                return ResponseEntity.badRequest().body("标题不能为空");
            }

            if (style == null || style.trim().isEmpty()) {
                System.out.println("错误：歌单风格为空");
                return ResponseEntity.badRequest().body("风格不能为空");
            }

            if (username == null || username.trim().isEmpty()) {
                System.out.println("错误：创建者用户名为空");
                return ResponseEntity.badRequest().body("创建者不能为空");
            }

            User creator = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        System.out.println("错误：未找到用户名为 " + username + " 的用户");
                        return new RuntimeException("未找到用户名为 " + username + " 的用户");
                    });

            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlist.setStyle(style);
            playlist.setDescription(description);
            playlist.setPublic(isPublic);  // 这里会将布尔值设置到实体中
            playlist.setCreator(creator);
            playlist.setCreatedAt(new Date());

            // Handle cover image upload
            if (cover != null && !cover.isEmpty()) {
                String fileName = saveCoverImage(cover);
                if (fileName != null) {
                    playlist.setCoverUrl("/" + PLAYLIST_COVER_DIR + fileName);
                } else {
                    System.out.println("警告：封面图片保存失败");
                }
            }

            Playlist savedPlaylist = playlistRepository.save(playlist);
            System.out.println("成功创建歌单ID: " + savedPlaylist.getId());
            return ResponseEntity.ok(savedPlaylist);
        } catch (Exception e) {
            System.out.println("创建歌单时发生错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String saveCoverImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            System.out.println("准备保存封面图片: " + file.getOriginalFilename() +
                    ", 类型: " + file.getContentType() +
                    ", 大小: " + (file.getSize() / 1024) + "KB");

            // 验证文件是图片
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                System.out.println("上传的文件不是图片: " + contentType);
                return null;
            }

            // 检查文件大小 (限制为10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                System.out.println("图片大小超过限制: " + (file.getSize() / 1024 / 1024) + "MB");
                return null;
            }

            // 确保上传目录存在
            File uploadDir = new File(PLAYLIST_COVER_DIR);
            if (!uploadDir.exists()) {
                if (!uploadDir.mkdirs()) {
                    System.out.println("创建上传目录失败: " + PLAYLIST_COVER_DIR);
                    return null;
                }
                System.out.println("创建上传目录: " + PLAYLIST_COVER_DIR);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                // 根据ContentType设置默认扩展名
                if ("image/jpeg".equals(contentType)) {
                    extension = ".jpg";
                } else if ("image/png".equals(contentType)) {
                    extension = ".png";
                } else if ("image/gif".equals(contentType)) {
                    extension = ".gif";
                } else {
                    extension = ".jpg";  // 默认扩展名
                }
            }

            String filename = UUID.randomUUID().toString() + extension;
            String uploadPath = PLAYLIST_COVER_DIR + filename;

            // 保存文件
            Path destinationPath = Path.of(uploadPath);
            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("封面图片保存成功: " + uploadPath);
            return filename;
        } catch (IOException e) {
            System.out.println("保存封面图片失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable Long id, @RequestBody Playlist playlist) {
        if (!playlistRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        playlist.setId(id);
        return ResponseEntity.ok(playlistRepository.save(playlist));
    }

    @PutMapping(value = "/{id}/with-cover", consumes = "multipart/form-data")
    public ResponseEntity<?> updatePlaylistWithCover(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("style") String style,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "cover", required = false) MultipartFile cover) {

        try {
            System.out.println("收到更新歌单请求，ID: " + id);
            System.out.println("标题: " + title);
            System.out.println("风格: " + style);
            System.out.println("描述: " + (description != null ? description : ""));
            System.out.println("封面: " + (cover != null ? (cover.isEmpty() ? "空文件" : cover.getOriginalFilename()) : "无文件"));

            Optional<Playlist> playlistOpt = playlistRepository.findById(id);
            if (playlistOpt.isEmpty()) {
                System.out.println("未找到ID为 " + id + " 的歌单");
                return ResponseEntity.notFound().build();
            }

            Playlist playlist = playlistOpt.get();
            playlist.setTitle(title);
            playlist.setStyle(style);
            playlist.setDescription(description);

            // 处理封面图片上传
            if (cover != null && !cover.isEmpty()) {
                System.out.println("接收到新封面图片: " + cover.getOriginalFilename() + ", 大小: " + cover.getSize() + ", 类型: " + cover.getContentType());
                String fileName = saveCoverImage(cover);
                playlist.setCoverUrl("/" + PLAYLIST_COVER_DIR + fileName);
                System.out.println("保存封面图片成功，新URL: " + playlist.getCoverUrl());
            } else {
                System.out.println("没有收到新封面图片或图片为空");
            }

            Playlist savedPlaylist = playlistRepository.save(playlist);
            System.out.println("歌单更新成功，ID: " + savedPlaylist.getId());
            return ResponseEntity.ok(savedPlaylist);
        } catch (Exception e) {
            System.err.println("更新歌单封面失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("更新歌单失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        if (!playlistRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        playlistRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{playlistId}/add-song/{songId}")
    public ResponseEntity<?> addSongToPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        Optional<Playlist> playlistOpt = playlistRepository.findById(playlistId);
        Optional<Music> songOpt = musicRepository.findById(songId);

        if (playlistOpt.isEmpty() || songOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Playlist playlist = playlistOpt.get();
        Music song = songOpt.get();

        if (playlist.getSongs().contains(song)) {
            return ResponseEntity.badRequest().body("Song already exists in playlist");
        }

        playlist.getSongs().add(song);
        playlistRepository.save(playlist);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{playlistId}/remove-song/{songId}")
    public ResponseEntity<?> removeSongFromPlaylist(@PathVariable Long playlistId, @PathVariable Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found with ID: " + playlistId));
        Music song = musicRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found with ID: " + songId));

        if (!playlist.getSongs().contains(song)) {
            return ResponseEntity.badRequest().body("Song not found in playlist");
        }

        playlist.getSongs().remove(song);
        playlistRepository.save(playlist);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<List<Music>> getPlaylistSongs(@PathVariable Long id) {
        Optional<Playlist> playlist = playlistRepository.findById(id);
        if (playlist.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(playlist.get().getSongs());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Playlist>> searchPlaylists(@RequestParam String keyword) {
        return ResponseEntity.ok(playlistRepository.searchByTitle(keyword));
    }

    @GetMapping("/style/{style}")
    public ResponseEntity<List<Playlist>> getPlaylistsByStyle(@PathVariable String style) {
        return ResponseEntity.ok(playlistRepository.findByStyle(style));
    }

    @GetMapping("/user/{username}/title/{title}")
    public ResponseEntity<List<Playlist>> getPlaylistByUserAndTitle(
            @PathVariable String username,
            @PathVariable String title) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return ResponseEntity.ok(playlistRepository.findByCreatorAndTitle(user, title));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countPlaylists() {
        return ResponseEntity.ok(playlistRepository.count());
    }
}