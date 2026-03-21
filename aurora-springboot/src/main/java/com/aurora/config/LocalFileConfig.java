package com.aurora.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class LocalFileConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取项目根目录
        String projectPath = System.getProperty("user.dir");
        String imagePath = projectPath + File.separator + "images" + File.separator;
        
        // 将 /images/** 映射到项目根目录的images文件夹
        // 注意：nginx转发 /api/images/ 时会去掉 /api/，所以这里配置 /images/**
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath)
                .setCachePeriod(2592000); // 缓存30天
    }
}
