package com.cpt202.music_management.service.impl;

import com.cpt202.music_management.dto.UserBanDTO;
import com.cpt202.music_management.model.User;
import com.cpt202.music_management.model.UserBanHistory;
import com.cpt202.music_management.repository.UserBanHistoryRepository;
import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.service.UserBanHistoryService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserBanHistoryServiceImpl implements UserBanHistoryService {

    private final UserBanHistoryRepository repository;
    private final UserRepository userRepository;

    public UserBanHistoryServiceImpl(UserBanHistoryRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public UserBanHistory createBanRecord(UserBanHistory record) {
        return repository.save(record);
    }

    @Override
    public List<UserBanHistory> getAllBanRecords() {
        return repository.findAll();
    }

    @Override
    public List<UserBanHistory> getBanRecordsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    // ✅ 封装包含用户名的封禁记录（用于前端展示）
    @Override
    public List<UserBanDTO> getAllBanRecordsWithUsername() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return repository.findAll().stream().map(ban -> {
            UserBanDTO dto = new UserBanDTO();
            dto.setId(ban.getId());
            dto.setReason(ban.getReason());
            dto.setDuration(ban.getDuration());
            dto.setDate(ban.getBanTime().format(formatter));

            // 查找用户名
            Optional<User> userOpt = userRepository.findById(ban.getUserId());
            dto.setUsername(userOpt.map(User::getUsername).orElse("Unknown"));

            return dto;
        }).collect(Collectors.toList());
    }
}
