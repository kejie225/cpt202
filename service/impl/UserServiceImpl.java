package com.cpt202.music_management.service.impl;

import com.cpt202.music_management.model.User;
import com.cpt202.music_management.model.UserBanHistory;
import com.cpt202.music_management.repository.UserBanHistoryRepository;
import com.cpt202.music_management.repository.UserRepository;
import com.cpt202.music_management.service.UserService;
import com.cpt202.music_management.service.EmailService;
import com.cpt202.music_management.dto.LoginRequest;
import com.cpt202.music_management.dto.LoginResponse;
import com.cpt202.music_management.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.text.SimpleDateFormat;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserBanHistoryRepository userBanHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserServiceImpl(
            UserRepository userRepository,
            UserBanHistoryRepository userBanHistoryRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.userRepository = userRepository;
        this.userBanHistoryRepository = userBanHistoryRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    public void banUser(Long userId, String reason, String duration) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(User.Status.banned);
            userRepository.save(user);

            UserBanHistory ban = new UserBanHistory();
            ban.setUserId(userId);
            ban.setReason(reason);
            ban.setBanTime(LocalDateTime.now());
            ban.setDuration(duration);
            userBanHistoryRepository.save(ban);
        } else {
            throw new RuntimeException("用户不存在");
        }
    }

    @Override
    public void unbanUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setStatus(User.Status.active);
            userRepository.save(user);
        } else {
            throw new RuntimeException("找不到该用户");
        }
    }

    // ✅ 实现分页搜索与筛选
    @Override
    public Page<User> searchUsers(int page, int size, String keyword, String status) {
        Specification<User> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (keyword != null && !keyword.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("username"), "%" + keyword + "%"));
            }
            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("All")) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }

            return predicate;
        };

        return userRepository.findAll(spec, PageRequest.of(page, size));
    }

    // 原始的注册方法
    @Override
    public User registerUser(User user) {
        logger.info("尝试注册用户: {}", user.getUsername());

        // 验证输入
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }

        // 检查用户名和邮箱是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(User.Status.active);
        }
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(new Date());
        }

        return userRepository.save(user);
    }

    // 带验证码的用户注册方法
    @Override
    public User registerUserWithVerification(RegisterRequest registerRequest) throws Exception {
        logger.info("尝试带验证码注册用户: {}", registerRequest.getUsername());

        // 验证输入
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new Exception("用户名不能为空");
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new Exception("邮箱不能为空");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            throw new Exception("密码不能为空");
        }
        if (registerRequest.getVerificationCode() == null || registerRequest.getVerificationCode().trim().isEmpty()) {
            throw new Exception("验证码不能为空");
        }

        // 检查用户名和邮箱是否已存在
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new Exception("用户名已存在");
        }
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new Exception("邮箱已存在");
        }

        // 验证邮箱验证码
        boolean isCodeValid = emailService.verifyCode(registerRequest.getEmail(), registerRequest.getVerificationCode());
        if (!isCodeValid) {
            logger.error("验证码无效或已过期: email={}, code={}", registerRequest.getEmail(), registerRequest.getVerificationCode());
            throw new Exception("验证码无效或已过期");
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setGender(registerRequest.getGender());
        user.setRegion(registerRequest.getRegion());
        user.setSignature(registerRequest.getSignature());
        
        // 处理生日日期
        if (registerRequest.getBirthday() != null && !registerRequest.getBirthday().isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date birthday = dateFormat.parse(registerRequest.getBirthday());
                user.setBirthday(birthday);
            } catch (Exception e) {
                logger.error("生日日期格式错误", e);
            }
        }
        
        // 设置默认值
        user.setStatus(User.Status.active);
        user.setRole(User.Role.USER);
        user.setCreatedAt(new Date());

        return userRepository.save(user);
    }

    // 新增：用户登录方法
    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("尝试登录用户: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        return new LoginResponse("dummy-token", user.getUsername(), user.getId(), "登录成功");
    }

    // 新增：Google OAuth2登录
    @Override
    public User handleGoogleCallback(String code) {
        logger.info("处理Google OAuth回调，code: {}", code);

        // 从Google API获取用户信息（示例）
        String email = "example@gmail.com";
        String username = "GoogleUser";

        // 检查用户是否已存在
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email);
                    newUser.setPassword(passwordEncoder.encode("default-password"));
                    newUser.setStatus(User.Status.active);
                    return userRepository.save(newUser);
                });
    }

    @Override
    public User login(String username, String password) {
        logger.info("尝试登录用户: {}", username);

        // 首先查找用户，如果找不到则抛出异常
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("密码不匹配，用户: {}", username);
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证用户状态
        if (user.getStatus() == User.Status.banned) {
            logger.warn("账户已被禁用，用户: {}", username);
            throw new RuntimeException("账户已被禁用");
        }

        // 更新最后登录时间
        user.setLastLogin(new Date());
        return userRepository.save(user);
    }
}
