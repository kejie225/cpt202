package com.cpt202.music_management.service;

import com.cpt202.music_management.model.UserBanHistory;
import com.cpt202.music_management.dto.UserBanDTO;

import java.util.List;

public interface UserBanHistoryService {

    // 添加封禁记录
    UserBanHistory createBanRecord(UserBanHistory record);

    // 查询所有封禁记录
    List<UserBanHistory> getAllBanRecords();

    // 根据用户 ID 查询
    List<UserBanHistory> getBanRecordsByUserId(Long userId);

    // ✅ 封装包含用户名的封禁记录（用于前端展示）
    List<UserBanDTO> getAllBanRecordsWithUsername();
}
