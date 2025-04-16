// 在你的项目结构中创建一个新的配置包，例如 com.cpt202.music_management.config
// 然后在该包下创建这个类

package com.cpt202.music_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean//将返回的对象注册为Spring容器中的Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") // 允许 /api/ 下的所有路径
                        .allowedOrigins("http://localhost:3000") // 允许来自这个源的请求 (修改为你前端的地址)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的方法
                        .allowedHeaders("*") // 允许所有请求头
                        .allowCredentials(true); // 是否允许发送 Cookie
            }
        };
    }
}
