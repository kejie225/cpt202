package com.cpt202.music_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactDTO {

    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 100, message = "姓名长度必须在2到100个字符之间")
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请提供有效的邮箱地址")
    private String email;

    @NotBlank(message = "主题不能为空")
    @Size(min = 2, max = 200, message = "主题长度必须在2到200个字符之间")
    private String subject;

    @NotBlank(message = "消息内容不能为空")
    @Size(min = 5, max = 2000, message = "消息内容长度必须在5到2000个字符之间")
    private String message;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "ContactDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", subject='" + subject + '\'' +
                ", message='" + (message != null ? message.substring(0, Math.min(30, message.length())) + "..." : "null") + '\'' +
                '}';
    }
} 