package com.aurora.config;

import com.aurora.service.SystemConfigProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Autowired
    private SystemConfigProviderService configProvider;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String imagePath = configProvider.getConfig("upload.local.imagePath", "/www/aurora-images/");
        
        // 确保路径以/结尾
        if (!imagePath.endsWith("/")) {
            imagePath += "/";
        }
        
        // 添加本地文件映射
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + imagePath);
    }
}
