package com.aurora.strategy.impl;

import com.aurora.exception.BizException;
import com.aurora.service.SystemConfigProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service("localUploadStrategyImpl")
public class LocalUploadStrategyImpl extends AbstractUploadStrategyImpl {

    @Autowired
    private SystemConfigProviderService configProvider;

    private String imagePath;

    @PostConstruct
    public void init() {
        // 使用项目根目录下的images文件夹
        String projectPath = System.getProperty("user.dir");
        imagePath = projectPath + File.separator + "images";
        
        // 确保images目录存在
        File imageDir = new File(imagePath);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
    }

    @Override
    public Boolean exists(String filePath) {
        return new File(imagePath + File.separator + filePath).exists();
    }

    @Override
    public void upload(String path, String fileName, InputStream inputStream) throws IOException {
        // 确保子目录存在
        File directory = new File(imagePath + File.separator + path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 写入文件
        File file = new File(directory, fileName);
        try (InputStream in = inputStream;
             FileOutputStream out = new FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        // 从数据库读取配置的baseUrl
        String baseUrl = configProvider.getConfig("upload.local.baseUrl", "");
        
        // 如果没有配置baseUrl，抛出异常
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new BizException("本地上传模式未配置基础URL，请在【系统管理-网站管理-系统设置】中配置基础URL");
        }
        
        // 确保baseUrl以/结尾
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        
        return baseUrl + filePath;
    }
}