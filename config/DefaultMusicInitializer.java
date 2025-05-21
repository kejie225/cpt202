package com.cpt202.music_management.config;

import com.cpt202.music_management.model.Music;
import com.cpt202.music_management.model.Singer;
import com.cpt202.music_management.repository.MusicRepository;
import com.cpt202.music_management.repository.SingerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Component
@Profile("unused")
public class DefaultMusicInitializer {

    private static final Logger log = LoggerFactory.getLogger(DefaultMusicInitializer.class);

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private SingerRepository singerRepository;

    @PostConstruct
    public void initDefaultMusic() {
        // 检查是否需要初始化
        if (musicRepository.count() > 0) {
            log.info("数据库中已存在音乐数据，跳过默认音乐初始化");
            return;
        }

        log.info("开始初始化默认音乐数据...");

        try {
            // 创建几个默认歌手
            Singer singer1 = createSingerIfNotExists("周杰伦", "中国台湾");
            Singer singer2 = createSingerIfNotExists("Taylor Swift", "美国");
            Singer singer3 = createSingerIfNotExists("RADWIMPS", "日本");
            Singer singer4 = createSingerIfNotExists("IU", "韩国");
            Singer singer5 = createSingerIfNotExists("Adele", "英国");

            // 创建默认音乐
            createDefaultMusic("七里香", "流行", "4:25", "/samples/qilixiang.mp3", singer1);
            createDefaultMusic("Lover", "流行", "3:41", "/samples/lover.mp3", singer2);
            createDefaultMusic("前前前世", "摇滚", "4:12", "/samples/zenzenzense.mp3", singer3);
            createDefaultMusic("八月十五日", "韩流", "3:58", "/samples/august15.mp3", singer4);
            createDefaultMusic("Hello", "流行", "4:55", "/samples/hello.mp3", singer5);
            createDefaultMusic("夜曲", "流行", "3:49", "/samples/yequ.mp3", singer1);
            createDefaultMusic("Love Story", "乡村", "3:56", "/samples/lovestory.mp3", singer2);
            createDefaultMusic("なんでもないや", "摇滚", "4:37", "/samples/nandemonaiya.mp3", singer3);

            log.info("默认音乐数据初始化完成");
        } catch (Exception e) {
            log.error("初始化默认音乐数据时出错: " + e.getMessage(), e);
        }
    }

    private Singer createSingerIfNotExists(String name, String region) {
        List<Singer> existingSingers = singerRepository.findByName(name);
        if (!existingSingers.isEmpty()) {
            return existingSingers.get(0);
        }

        Singer singer = new Singer();
        singer.setName(name);
        singer.setRegion(region);
        return singerRepository.save(singer);
    }

    private void createDefaultMusic(String title, String type, String duration, String resourcePath, Singer singer) {
        Music music = new Music();
        music.setTitle(title);
        music.setType(type);
        music.setDuration(duration);
        music.setResourceUrl(resourcePath);
        music.setSinger(singer);
        music.setStatus("Approved");  // 设置为已审核状态
        music.setCreatedAt(new Date());
        musicRepository.save(music);
        log.info("已创建默认音乐: {}", title);
    }
} 