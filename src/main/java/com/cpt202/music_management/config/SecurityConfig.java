package com.cpt202.music_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }//密码设置了BCrypt

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 禁用CSRF保护，因为这是API项目
            .authorizeHttpRequests(auth -> auth
                // 允许访问所有静态资源
                .requestMatchers("/**").permitAll()
                // 允许访问所有API端点
                .requestMatchers("/api/**").permitAll()
                // 允许访问所有页面
                .requestMatchers("/*.html", "/*.css", "/*.js").permitAll()
            )
            .formLogin(form -> form.disable()) // 禁用默认的登录表单
            .httpBasic(basic -> basic.disable()); // 禁用HTTP基本认证
        
        return http.build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
} 