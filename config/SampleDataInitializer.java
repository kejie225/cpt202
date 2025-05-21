package com.cpt202.music_management.config;

import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.model.Singer;
import com.cpt202.music_management.repository.MusicRepository;
import com.cpt202.music_management.repository.SingerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Configuration
@Profile("unused")
public class SampleDataInitializer {

    @Autowired
    private MusicRepository musicRepository;
    
    @Autowired
    private SingerRepository singerRepository;

    @Bean
    public CommandLineRunner initSampleMusic() {
        return args -> {
            try {
                // 检查数据库中是否已有样本音乐记录
                List<Music> existingSampleMusic = musicRepository.findAll()
                    .stream()
                    .filter(music -> music.getResourceUrl() != null && music.getResourceUrl().contains("/sample/"))
                    .toList();
                
                if (!existingSampleMusic.isEmpty()) {
                    System.out.println("数据库中已存在样本音乐记录，共 " + existingSampleMusic.size() + " 首，跳过初始化");
                    return;
                }
                
                System.out.println("开始初始化样本音乐...");
                
                // 创建或获取未知歌手
                Singer unknownSinger = getOrCreateUnknownSinger();
                
                // 获取sample目录下的所有mp3文件
                Resource[] resources = new PathMatchingResourcePatternResolver()
                        .getResources("classpath:static/sample/*.mp3");
                
                if (resources.length == 0) {
                    // 尝试使用绝对路径查找
                    String absolutePath = System.getProperty("user.dir") + "/src/main/resources/static/sample/*.mp3";
                    System.out.println("未找到classpath下的样本音乐，尝试使用绝对路径：" + absolutePath);
                    
                    try {
                        resources = new PathMatchingResourcePatternResolver()
                                .getResources("file:" + absolutePath);
                    } catch (Exception e) {
                        System.err.println("使用绝对路径查找样本音乐失败：" + e.getMessage());
                    }
                    
                    if (resources.length == 0) {
                        // 尝试硬编码的方式添加样本
                        System.out.println("未找到任何样本音乐文件，使用硬编码方式添加样本");
                        addHardcodedSampleMusic(unknownSinger);
                        return;
                    }
                }

                System.out.println("找到 " + resources.length + " 个样本音乐文件");
                
                for (Resource resource : resources) {
                    String filename = resource.getFilename();
                    if (filename == null) continue;
                    
                    System.out.println("正在处理文件：" + filename);

                    // 从文件名中提取一个简单的标题（移除UUID部分）
                    String title = filename;
                    if (filename.contains("_")) {
                        title = filename.substring(filename.indexOf("_") + 1);
                    }
                    // 移除扩展名
                    if (title.endsWith(".mp3")) {
                        title = title.substring(0, title.length() - 4);
                    }

                    // 创建音乐记录
                    Music music = new Music();
                    music.setTitle(title);
                    music.setSinger(unknownSinger); // 设置未知歌手对象
                    music.setAlbum("样本音乐");
                    music.setType("Pop");
                    music.setDuration("3:30"); // 默认时长
                    music.setLyrics("样本音乐，无歌词");
                    music.setStatus("Approved"); // 直接设置为已审核
                    music.setResourceUrl("/sample/" + filename); // 设置为static/sample目录下的相对路径
                    music.setCoverUrl("/images/default-cover.jpg"); // 使用默认封面
                    music.setCreatedAt(new Date());

                    musicRepository.save(music);
                    System.out.println("已添加样本音乐：" + title);
                }

                System.out.println("样本音乐初始化完成");
            } catch (IOException e) {
                System.err.println("初始化样本音乐时发生错误：" + e.getMessage());
                e.printStackTrace();
            }
        };
    }
    
    /**
     * 添加硬编码的样本音乐
     */
    private void addHardcodedSampleMusic(Singer singer) {
        // 已知的样本文件
        String[] sampleFiles = {
            "dee8a3aa-60b8-4928-a2f9-48f5f9db672c_M800000LMPer1deeec.mp3",
            "de87bc9e-e73e-480d-8d6b-bd8831fdd5f0_2278945565.mp3",
            "8055bc99-b5f8-4f96-831f-fd14949e75d8_M800001ziKgJ3o5Ipp.mp3"
        };
        
        for (String filename : sampleFiles) {
            // 从文件名中提取一个简单的标题
            String title = filename;
            if (filename.contains("_")) {
                title = filename.substring(filename.indexOf("_") + 1);
            }
            // 移除扩展名
            if (title.endsWith(".mp3")) {
                title = title.substring(0, title.length() - 4);
            }
            
            // 创建音乐记录
            Music music = new Music();
            music.setTitle(title);
            music.setSinger(singer);
            music.setAlbum("样本音乐");
            music.setType("Pop");
            music.setDuration("3:30");
            music.setLyrics("样本音乐，无歌词");
            music.setStatus("Approved");
            music.setResourceUrl("/sample/" + filename);
            music.setCoverUrl("/images/default-cover.jpg");
            music.setCreatedAt(new Date());
            
            musicRepository.save(music);
            System.out.println("已添加硬编码样本音乐：" + title);
        }
    }
    
    /**
     * 获取或创建未知歌手
     */
    private Singer getOrCreateUnknownSinger() {
        List<Singer> singers = singerRepository.findByName("未知艺术家");
        if (!singers.isEmpty()) {
            return singers.get(0);
        }
        
        // 创建一个新的未知歌手
        Singer singer = new Singer();
        singer.setName("未知艺术家");
        singer.setGender(Singer.Gender.男); // 默认设置性别
        singer.setRegion("未知");
        singer.setDescription("样本音乐的默认艺术家");
        singer.setCreatedAt(new Date());
        
        return singerRepository.save(singer);
    }
} 