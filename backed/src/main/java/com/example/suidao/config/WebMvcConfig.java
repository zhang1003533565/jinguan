package com.example.suidao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 配置接口前缀
        configurer.addPathPrefix("/mini", c -> c.getPackage().getName().contains(".mini"))
                 .addPathPrefix("/admin", c -> c.getPackage().getName().contains(".admin"));
    }
} 