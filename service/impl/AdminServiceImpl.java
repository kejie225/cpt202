package com.cpt202.music_management.service.impl;

import com.cpt202.music_management.model.Admin;
import com.cpt202.music_management.repository.AdminRepository;
import com.cpt202.music_management.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password); // 实际项目应加密

        return adminRepository.save(admin);
    }

    @Override
    public Admin login(String username, String password) {
        return adminRepository.findByUsername(username)
                .filter(admin -> admin.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
    }
}
