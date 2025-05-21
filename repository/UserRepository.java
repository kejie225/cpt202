package com.cpt202.music_management.repository;

import com.cpt202.music_management.model.User;
import com.cpt202.music_management.model.User.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    
    // 分页和搜索方法
    Page<User> findByUsernameContaining(String username, Pageable pageable);
    
    Page<User> findByStatus(Status status, Pageable pageable);
    
    Page<User> findByUsernameContainingAndStatus(String username, Status status, Pageable pageable);
}