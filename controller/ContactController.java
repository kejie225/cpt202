package com.cpt202.music_management.controller;

import com.cpt202.music_management.dto.ContactDTO;
import com.cpt202.music_management.model.Contact;
import com.cpt202.music_management.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactRepository contactRepository;
    
    // 测试API是否可用的简单端点
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testContactAPI() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Contact API is working");
        response.put("timestamp", System.currentTimeMillis());
        logger.info("测试API端点被调用");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> submitContactForm(@Valid @RequestBody ContactDTO contactDTO) {
        try {
            logger.info("接收到联系表单提交: {}", contactDTO);
            
            // 验证数据
            if (contactDTO.getName() == null || contactDTO.getName().trim().isEmpty()) {
                logger.warn("提交的姓名为空");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "姓名不能为空"
                ));
            }
            
            if (contactDTO.getEmail() == null || !contactDTO.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                logger.warn("提交的邮箱格式无效: {}", contactDTO.getEmail());
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "请提供有效的邮箱地址"
                ));
            }
            
            if (contactDTO.getSubject() == null || contactDTO.getSubject().trim().isEmpty()) {
                logger.warn("提交的主题为空");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "主题不能为空"
                ));
            }
            
            if (contactDTO.getMessage() == null || contactDTO.getMessage().trim().isEmpty()) {
                logger.warn("提交的消息内容为空");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "消息内容不能为空"
                ));
            }
            
            try {
                // 使用自定义方法直接保存到数据库
                contactRepository.saveContact(
                    contactDTO.getName(),
                    contactDTO.getEmail(),
                    contactDTO.getSubject(),
                    contactDTO.getMessage()
                );
                logger.info("成功保存联系信息: name={}, email={}", contactDTO.getName(), contactDTO.getEmail());
            } catch (Exception e) {
                logger.error("保存联系信息到数据库时发生错误", e);
                // 尝试使用普通方法
                try {
                    // 创建新的Contact实体
                    Contact contact = new Contact(
                            contactDTO.getName(),
                            contactDTO.getEmail(),
                            contactDTO.getSubject(),
                            contactDTO.getMessage()
                    );
                    // 保存到数据库
                    Contact savedContact = contactRepository.save(contact);
                    logger.info("使用普通方法成功保存联系信息: {}", savedContact);
                } catch (Exception ex) {
                    logger.error("使用普通方法保存联系信息失败", ex);
                    throw ex; // 再次抛出异常以便上层捕获
                }
            }

            // 返回成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Your message has been sent successfully! We will get back to you soon.");

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("提交联系表单时发生错误", e);
            
            // 返回错误响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An error occurred while sending your message. Please try again later.");
            response.put("error", e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 获取所有留言（管理员功能）
    @GetMapping("/admin/messages")
    public ResponseEntity<?> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Boolean read
    ) {
        try {
            logger.info("获取留言列表，页码: {}, 大小: {}, 排序字段: {}, 排序方向: {}, 读取状态: {}", 
                page, size, sortBy, sortDir, read);
                
            Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Contact> contactPage;
            
            // 如果read参数存在，按照已读/未读状态筛选
            if (read != null) {
                contactPage = contactRepository.findByReadStatus(read, pageable);
            } else {
                contactPage = contactRepository.findAll(pageable);
            }
            
            List<Contact> contacts = contactPage.getContent();
            logger.info("找到 {} 条留言记录", contacts.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", contacts);
            response.put("currentPage", contactPage.getNumber());
            response.put("totalItems", contactPage.getTotalElements());
            response.put("totalPages", contactPage.getTotalPages());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取联系信息时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error retrieving messages",
                            "error", e.getMessage()
                    ));
        }
    }
    
    // 标记留言为已读（管理员功能）
    @PutMapping("/admin/messages/{id}/read")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long id) {
        try {
            logger.info("标记留言为已读，ID: {}", id);
            
            Optional<Contact> contactOptional = contactRepository.findById(id);
            
            if (!contactOptional.isPresent()) {
                logger.warn("找不到ID为 {} 的留言", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "Message not found with id: " + id
                        ));
            }
            
            Contact contact = contactOptional.get();
            contact.setRead(true);
            contactRepository.save(contact);
            logger.info("留言已标记为已读，ID: {}", id);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Message marked as read successfully"
            ));
            
        } catch (Exception e) {
            logger.error("标记消息已读时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error marking message as read",
                            "error", e.getMessage()
                    ));
        }
    }
    
    // 获取未读留言数量（管理员功能）
    @GetMapping("/admin/messages/unread/count")
    public ResponseEntity<?> getUnreadMessagesCount() {
        try {
            long count = contactRepository.countByRead(false);
            logger.info("未读留言数量: {}", count);
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", count
            ));
            
        } catch (Exception e) {
            logger.error("获取未读消息数量时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Error counting unread messages",
                            "error", e.getMessage()
                    ));
        }
    }
} 