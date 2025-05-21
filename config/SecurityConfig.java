package com.cpt202.music_management.config;

import com.cpt202.music_management.security.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }
    /////生成了SecurityConfig类，并配置了JWT过滤器

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }//密码设置了BCrypt

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable()) // 禁用CSRF保护，因为这是API项目
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 允许未认证访问的公开接口
                .requestMatchers("/api/users/register", "/api/users/login", "/api/singers/**", "/api/musics/**").permitAll()
                // 允许验证码接口匿名访问
                .requestMatchers("/api/verification/**", "/api/site/register").permitAll()
                // 允许管理员注册和登录接口匿名访问
                .requestMatchers("/api/admin/register", "/api/admin/register/json", "/api/admin/login").permitAll()
                // 需要认证的接口
                .requestMatchers("/api/users/profile", "/api/users/favorites/**").authenticated()
                // 管理员接口
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                // 其他请求默认允许
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
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