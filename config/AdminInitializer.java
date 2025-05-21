package com.cpt202.music_management.config;

import com.cpt202.music_management.model.Admin;
import com.cpt202.music_management.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);
    
    private final AdminRepository adminRepository;
    
    @Autowired
    public AdminInitializer(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在管理员
        if (adminRepository.count() > 0) {
            logger.info("数据库中已存在管理员账户，跳过初始化");
            return;
        }
        
        logger.info("正在创建默认管理员账户...");
        
        try {
            // 创建默认管理员
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin");
            
            adminRepository.save(admin);
            
            logger.info("默认管理员账户创建成功，用户名: admin, 密码: admin");
        } catch (Exception e) {
            logger.error("创建默认管理员账户失败: " + e.getMessage(), e);
        }
    }
} 