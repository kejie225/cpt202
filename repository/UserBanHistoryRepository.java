package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.UserBanHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBanHistoryRepository extends JpaRepository<UserBanHistory, Long> {

    // 根据用户 ID 查询其封禁历史
    List<UserBanHistory> findByUserId(Long userId);
}
