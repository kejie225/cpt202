package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username); // 根据用户名查找管理员
}