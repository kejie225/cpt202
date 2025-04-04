package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicRepo extends JpaRepository<Music,Integer> {
    //Spring Data JPA的核心设计是**"约定优于配置"**，它通过以下方式工作：
    //运行时自动生成实现：Spring会在应用启动时为此接口创建动态代理实例
    //方法名解析：根据方法名自动生成查询实现

}
