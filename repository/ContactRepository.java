package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    // 根据已读状态查询留言
    Page<Contact> findByRead(Boolean read, Pageable pageable);
    
    // 统计未读留言数量
    long countByRead(boolean read);
    
    // 自定义查询以适应新的字段名
    @Query("SELECT c FROM Contact c WHERE c.read = ?1")
    Page<Contact> findByReadStatus(boolean isRead, Pageable pageable);
    
    // 自定义保存方法，避免可能的字段映射问题
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO contacts (name, email, subject, message, created_at, is_read) VALUES (:name, :email, :subject, :message, NOW(), false)", nativeQuery = true)
    void saveContact(@Param("name") String name, @Param("email") String email, @Param("subject") String subject, @Param("message") String message);
} 