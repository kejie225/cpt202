package com.cpt202.music_management.controller;

import com.cpt202.music_management.dto.MusicsResponse;
import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.model.Playlist;
import com.cpt202.music_management.model.Singer; // 确保导入 Singer
import com.cpt202.music_management.repository.MusicRepository;
import com.cpt202.music_management.repository.PlaylistRepository;
import com.cpt202.music_management.repository.SingerRepository; // 确保导入 SingerRepository
import com.cpt202.music_management.service.MusicService;
import com.cpt202.music_management.specifications.MusicSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
// Import necessary classes for GET by ID
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date; // 导入 Date
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    private static final Logger log = LoggerFactory.getLogger(MusicController.class);

    @Autowired
    private MusicRepository musicRepository;
    
    @Autowired
    private MusicService musicService;

    // 确保 SingerRepository 已注入
    @Autowired
    private SingerRepository singerRepository;

    // 定义音乐文件上传目录
    private final String MUSIC_UPLOAD_DIR = "uploads/music/";

    // --- 其他GetMapping, PutMapping, DeleteMapping 方法保持不变 ---
    // ... (保留 getAllMusic, getMusicByTitle, etc.) ...

    // GET endpoint to retrieve a single music item by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Music> getMusicById(@PathVariable Long id) {
        Optional<Music> musicOptional = musicRepository.findById(id);
        if (musicOptional.isPresent()) {
            return ResponseEntity.ok(musicOptional.get());
        } else {
            System.out.println("未找到 ID 为 " + id + " 的音乐");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or return ResponseEntity.notFound().build();
        }
    }

    @GetMapping({"", "/"})
    public ResponseEntity<Page<Music>> getAllMusic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        log.info("GET request for all music with page: {}, size: {}, sortBy: {}, sortDir: {}, keyword: {}, author: {}, type: {}, status: {}", 
                page, size, sortBy, sortDir, keyword, author, type, status);

        Sort sort = Sort.by(sortBy);
        if (sortDir.equals("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Music> musicPage = musicRepository.findAll(
                Specification
                        .where(MusicSpec.hasName(keyword))
                        .and(MusicSpec.hasAuthor(author))
                        .and(MusicSpec.hasType(type))
                        .and(MusicSpec.hasStatus(status)),
                pageable);

        log.info("Total music entries: {}", musicPage.getTotalElements());
        return ResponseEntity.ok(musicPage);
    }
    
    // 修复后的 createMusic 方法
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createMusic(
            @RequestParam("songTitle") String title,
            @RequestParam(value = "songArtist", required = false) String artistName,
            @RequestParam("songDuration") String duration,
            @RequestParam(value = "songFile", required = true) MultipartFile songFile,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "lyrics", required = false) String lyrics) {

        if (songFile.isEmpty()) {
            return ResponseEntity.badRequest().body("必须上传音乐文件 (Music file is required.)");
        }

        try {
            // --- 步骤 1: 处理艺术家信息 ---
            Singer singer = null;
            if (artistName != null && !artistName.trim().isEmpty()) {
                // 尝试查找现有的歌手
                List<Singer> singers = singerRepository.findByName(artistName);
                if (!singers.isEmpty()) {
                    singer = singers.get(0);
                    System.out.println("找到存在的 Singer: " + singer.getName() + " (ID: " + singer.getId() + ")");
                } else {
                    // 如果没有找到匹配的歌手，创建一个新的歌手记录
                    singer = new Singer();
                    singer.setName(artistName);
                    singer.setGender(Singer.Gender.男); // 默认性别设置
                    singer.setRegion("未知");
                    singer.setDescription("用户添加的歌手");
                    singer.setCreatedAt(new Date());
                    singer = singerRepository.save(singer);
                    System.out.println("创建了新的 Singer: " + singer.getName() + " (ID: " + singer.getId() + ")");
                }
            } else {
                // 如果没有提供艺术家名称，使用"未知艺术家"
                List<Singer> singers = singerRepository.findByName("未知艺术家");
                if (!singers.isEmpty()) {
                    singer = singers.get(0);
                } else {
                    // 如果"未知艺术家"不存在，创建它
                    singer = new Singer();
                    singer.setName("未知艺术家");
                    singer.setGender(Singer.Gender.男);
                    singer.setRegion("未知");
                    singer.setDescription("未知艺术家");
                    singer.setCreatedAt(new Date());
                    singer = singerRepository.save(singer);
                }
            }

            // --- 步骤 2: 保存上传的音乐文件 ---
            String resourceUrl = saveMusicFile(songFile);
            System.out.println("音乐文件已保存到: " + resourceUrl);

            // --- 步骤 3: 处理封面图片上传 ---
            String coverUrl = null;
            if (coverImage != null && !coverImage.isEmpty()) {
                coverUrl = saveCoverImage(coverImage);
                System.out.println("封面图片已保存到: " + coverUrl);
            }
            
            // --- 步骤 3.5: 处理歌词长度 ---
            if (lyrics != null && !lyrics.isEmpty()) {
                int lyricsLength = lyrics.length();
                // 设置一个安全的最大长度，根据数据库TEXT类型的限制
                final int MAX_LYRICS_LENGTH = 65000;  // MySQL TEXT类型最大长度约为65KB
                
                if (lyricsLength > MAX_LYRICS_LENGTH) {
                    System.out.println("警告: 歌词内容过长 (" + lyricsLength + " 字符)，将截断至 " + MAX_LYRICS_LENGTH + " 字符");
                    lyrics = lyrics.substring(0, MAX_LYRICS_LENGTH);
                }
            }

            // --- 步骤 4: 创建并填充 Music 对象 ---
            Music music = new Music();
            music.setTitle(title);
            music.setDuration(duration);
            music.setResourceUrl(resourceUrl);
            music.setCoverUrl(coverUrl);
            music.setSinger(singer);  // 可以为 null
            music.setLyrics(lyrics);  // 设置歌词
            music.setCreatedAt(new Date());
            music.setStatus("Pending"); // 设置初始状态为待审核

            // --- 步骤 5: 保存 Music 对象到数据库 ---
            Music savedMusic = musicRepository.save(music);
            System.out.println("已创建新的 Music 记录, ID: " + savedMusic.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(savedMusic);

        } catch (IOException e) {
            System.err.println("错误: 保存音乐文件失败 - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("服务器内部错误: 保存音乐文件失败。请联系管理员。");
        } catch (Exception e) {
            System.err.println("错误: 创建音乐记录失败 - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("服务器内部错误: 创建音乐记录失败 - " + e.getMessage());
        }
    }

    // --- 确保 saveMusicFile 辅助方法存在 ---
    public String saveMusicFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(MUSIC_UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
                System.out.println("已创建音乐上传目录: " + uploadPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("无法创建音乐上传目录: " + uploadPath.toAbsolutePath());
                throw e; // 重新抛出异常
            }
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            // 如果需要，可以生成一个默认文件名或抛出更具体的错误
           originalFilename = "unknown_music_file";
           System.err.println("警告: 上传的文件名为空，将使用默认名称。");
           // throw new IOException("上传的文件名为空 (Uploaded file has no original filename.)");
        }

        String extension = "";
        int lastDot = originalFilename.lastIndexOf(".");
        if (lastDot >= 0) {
            extension = originalFilename.substring(lastDot);
        }
        // 清理可能的路径字符，虽然UUID基本避免了冲突，但以防万一
        String safeOriginalNameBase = originalFilename.substring(0, lastDot >= 0 ? lastDot : originalFilename.length())
                                                     .replaceAll("[^a-zA-Z0-9.\\-]", "_");

        String fileName = UUID.randomUUID().toString() + "_" + safeOriginalNameBase + extension;

        Path filePath = uploadPath.resolve(fileName);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            System.err.println("无法将文件复制到目标路径: " + filePath.toAbsolutePath());
            throw e; // 重新抛出异常
        }
        // 返回可以在前端访问的相对 URL 路径
        return "/" + MUSIC_UPLOAD_DIR.replace("\\", "/") + fileName; // 确保使用 / 分隔符
    }

    // 添加保存封面图片的辅助方法
    private String saveCoverImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 验证文件是图片
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("上传的文件不是图片: " + contentType);
        }

        // 检查文件大小 (限制为10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("图片大小超过限制: " + (file.getSize() / 1024 / 1024) + "MB");
        }

        // 确保上传目录存在
        Path uploadPath = Paths.get("uploads/covers/");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("已创建封面图片上传目录: " + uploadPath.toAbsolutePath());
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
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/covers/" + filename;
    }

    // --- 保留你的 updateMusic 和 deleteMusic 方法 ---
    // ... (你的 updateMusic 和 deleteMusic 方法代码) ...
     @PutMapping("/{id}")
     public ResponseEntity<Music> updateMusic(@PathVariable Long id, @RequestBody Music musicDetails) {
         log.info("Updating music with id: {}, details: {}", id, musicDetails);
         
         if (!musicRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
         }
         Music music = musicRepository.findById(id).orElse(null);
         if (music == null) return ResponseEntity.notFound().build();

         // 仅当提供了相应字段时才更新
         if (musicDetails.getTitle() != null) {
             music.setTitle(musicDetails.getTitle());
         }
         if (musicDetails.getAlbum() != null) {
             music.setAlbum(musicDetails.getAlbum());
         }
         if (musicDetails.getLyrics() != null) {
             music.setLyrics(musicDetails.getLyrics());
         }
         if (musicDetails.getType() != null) {
             music.setType(musicDetails.getType());
         }
         if (musicDetails.getDuration() != null) {
             music.setDuration(musicDetails.getDuration());
         }
         
         // 处理歌手信息
         if (musicDetails.getSinger() != null && musicDetails.getSinger().getName() != null) {
             String artistName = musicDetails.getSinger().getName();
             if (!artistName.trim().isEmpty()) {
                 // 尝试查找已存在的歌手
                 List<Singer> singers = singerRepository.findByName(artistName);
                 Singer singer;
                 
                 if (!singers.isEmpty()) {
                     // 使用现有歌手
                     singer = singers.get(0);
                     log.info("Found existing singer: {} (ID: {})", singer.getName(), singer.getId());
                 } else {
                     // 创建新歌手
                     singer = new Singer();
                     singer.setName(artistName);
                     singer.setGender(Singer.Gender.男); // 默认性别
                     singer.setRegion("未知");
                     singer.setDescription("用户添加的歌手");
                     singer.setCreatedAt(new Date());
                     singer = singerRepository.save(singer);
                     log.info("Created new singer: {} (ID: {})", singer.getName(), singer.getId());
                 }
                 
                 // 设置歌手
                 music.setSinger(singer);
             }
         }
         
         // 添加对status和reason字段的处理
         if (musicDetails.getStatus() != null) {
             music.setStatus(musicDetails.getStatus());
             log.info("Updated music status to: {}", musicDetails.getStatus());
         }
         if (musicDetails.getReason() != null) {
             music.setReason(musicDetails.getReason());
         }
         
         // 注意：通常更新时不处理文件上传和 resourceUrl/coverUrl
         
         Music updatedMusic = musicRepository.save(music);
         log.info("Music updated successfully: {}", updatedMusic);
         return ResponseEntity.ok(updatedMusic);
     }

     @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteMusic(@PathVariable Long id) {
         // ... 实现 ...
          if (!musicRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // TODO: 添加删除关联文件的逻辑
        musicRepository.deleteById(id);
        return ResponseEntity.ok().build();
     }

    // 新增：获取推荐音乐的端点
    @GetMapping("/recommend")
    public ResponseEntity<List<Music>> getRecommendedMusic(@RequestParam(defaultValue = "6") int count) {
        log.info("获取推荐音乐，请求数量: {}", count);
        
        // 获取所有已审核通过的音乐
        List<Music> approvedMusic = musicRepository.findAll(MusicSpec.hasStatus("Approved"));
        log.info("找到已审核通过的音乐: {} 首", approvedMusic.size());
        
        // 如果音乐数量不足，直接返回所有已审核通过的音乐
        if (approvedMusic.size() <= count) {
            return ResponseEntity.ok(approvedMusic);
        }
        
        // 随机选择指定数量的音乐
        java.util.Random rand = new java.util.Random();
        java.util.List<Music> recommendedMusic = new java.util.ArrayList<>();
        java.util.Set<Integer> selectedIndices = new java.util.HashSet<>();
        
        while (recommendedMusic.size() < count) {
            int index = rand.nextInt(approvedMusic.size());
            if (selectedIndices.add(index)) {  // 如果这个索引之前没被选中过
                recommendedMusic.add(approvedMusic.get(index));
            }
        }
        
        log.info("返回 {} 首推荐音乐", recommendedMusic.size());
        return ResponseEntity.ok(recommendedMusic);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countMusics() {
        return ResponseEntity.ok(musicRepository.count());
    }

    /**
     * 获取最近活动记录
     * 目前是模拟数据，实际项目中可以从数据库查询
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity() {
        log.info("获取最近活动记录");
        
        // 创建模拟的活动数据
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // 创建几个示例活动
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("title", "New Song Added");
        activity1.put("description", "A new song 'TestSong1' was added to the collection");
        activity1.put("timestamp", System.currentTimeMillis() - 3600000); // 1小时前
        
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("title", "Playlist Created");
        activity2.put("description", "You created a new playlist 'My Favorites'");
        activity2.put("timestamp", System.currentTimeMillis() - 7200000); // 2小时前
        
        Map<String, Object> activity3 = new HashMap<>();
        activity3.put("title", "Song Played");
        activity3.put("description", "You listened to 'TestSong1'");
        activity3.put("timestamp", System.currentTimeMillis() - 86400000); // 1天前
        
        activities.add(activity1);
        activities.add(activity2);
        activities.add(activity3);
        
        log.info("返回 {} 条活动记录", activities.size());
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/check-uploads-directory")
    public ResponseEntity<?> checkUploadsDirectory() {
        Path uploadPath = Paths.get(MUSIC_UPLOAD_DIR);
        boolean exists = Files.exists(uploadPath);
        return ResponseEntity.ok(Map.of("exists", exists, "path", uploadPath.toString()));
    }

    @PostMapping("/create-uploads-directory")
    public ResponseEntity<?> createUploadsDirectory() {
        try {
            Path uploadPath = Paths.get(MUSIC_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                return ResponseEntity.ok(Map.of("success", true, "message", "Directory created successfully"));
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Directory already exists"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // 添加新的POST端点，接受JSON格式的请求
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMusicJson(@RequestBody Music music) {
        log.info("Creating music with JSON request: {}", music);
        try {
            // 设置初始状态为Pending
            if (music.getStatus() == null) {
                music.setStatus("Pending");
            }
            
            // 设置创建时间
            if (music.getCreatedAt() == null) {
                music.setCreatedAt(new Date());
            }
            
            // 处理歌手信息
            if (music.getSinger() != null && music.getSinger().getName() != null) {
                String artistName = music.getSinger().getName();
                if (!artistName.trim().isEmpty()) {
                    // 尝试查找已存在的歌手
                    List<Singer> singers = singerRepository.findByName(artistName);
                    Singer singer;
                    
                    if (!singers.isEmpty()) {
                        // 使用现有歌手
                        singer = singers.get(0);
                        log.info("Found existing singer: {} (ID: {})", singer.getName(), singer.getId());
                    } else {
                        // 创建新歌手
                        singer = new Singer();
                        singer.setName(artistName);
                        singer.setGender(Singer.Gender.男); // 默认性别
                        singer.setRegion("未知");
                        singer.setDescription("用户添加的歌手");
                        singer.setCreatedAt(new Date());
                        singer = singerRepository.save(singer);
                        log.info("Created new singer: {} (ID: {})", singer.getName(), singer.getId());
                    }
                    
                    // 设置歌手
                    music.setSinger(singer);
                }
            } else {
                // 使用未知艺术家
                List<Singer> singers = singerRepository.findByName("未知艺术家");
                Singer unknownSinger;
                
                if (!singers.isEmpty()) {
                    unknownSinger = singers.get(0);
                } else {
                    // 创建未知艺术家
                    unknownSinger = new Singer();
                    unknownSinger.setName("未知艺术家");
                    unknownSinger.setGender(Singer.Gender.男);
                    unknownSinger.setRegion("未知");
                    unknownSinger.setDescription("未知艺术家");
                    unknownSinger.setCreatedAt(new Date());
                    unknownSinger = singerRepository.save(unknownSinger);
                }
                
                music.setSinger(unknownSinger);
            }
            
            // 保存到数据库
            Music savedMusic = musicRepository.save(music);
            log.info("Music created successfully with ID: {}", savedMusic.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMusic);
        } catch (Exception e) {
            log.error("Failed to create music: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Error creating music: " + e.getMessage());
        }
    }

    // 添加音乐审批端点
    @PostMapping("/{id}/approve")
    public ResponseEntity<Music> approveMusic(@PathVariable Long id) {
        log.info("Approving music with id: {} (type: {})", id, id != null ? id.getClass().getName() : "null");
        Optional<Music> musicOpt = musicRepository.findById(id);
        
        if (!musicOpt.isPresent()) {
            log.warn("Music with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        
        Music music = musicOpt.get();
        music.setStatus("Approved");
        music.setReason(null); // 清除任何拒绝原因
        
        Music approvedMusic = musicRepository.save(music);
        log.info("Music approved successfully: {}", approvedMusic);
        
        return ResponseEntity.ok(approvedMusic);
    }
    
    // 添加音乐拒绝端点
    @PostMapping("/{id}/reject")
    public ResponseEntity<Music> rejectMusic(
            @PathVariable Long id,
            @RequestParam String reason) {
        log.info("Rejecting music with id: {}, reason: {}", id, reason);
        Optional<Music> musicOpt = musicRepository.findById(id);
        
        if (!musicOpt.isPresent()) {
            log.warn("Music with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        
        Music music = musicOpt.get();
        music.setStatus("Rejected");
        music.setReason(reason);
        
        Music rejectedMusic = musicRepository.save(music);
        log.info("Music rejected successfully: {}", rejectedMusic);
        
        return ResponseEntity.ok(rejectedMusic);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Music>> searchMusic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type) {
        
        log.info("搜索音乐，参数: page={}, size={}, sortBy={}, sortDir={}, keyword={}, type={}", 
                page, size, sortBy, sortDir, keyword, type);
        
        // 创建排序
        Sort sort = Sort.by(sortBy);
        if (sortDir.equals("desc")) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        
        // 创建分页请求
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 创建查询规范
        Specification<Music> spec = Specification.where(null);
        
        // 添加关键词过滤条件
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and(MusicSpec.hasName(keyword));
        }
        
        // 添加类型过滤条件
        if (type != null && !type.trim().isEmpty()) {
            spec = spec.and(MusicSpec.hasType(type));
        }
        
        // 执行查询
        Page<Music> results = musicRepository.findAll(spec, pageable);
        log.info("查询结果: 总计 {} 条记录", results.getTotalElements());
        
        return ResponseEntity.ok(results);
    }

}