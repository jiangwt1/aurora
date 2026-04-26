package com.aurora.strategy.impl;

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

    private String defaultImagePath;

    @PostConstruct
    public void init() {
        String projectPath = System.getProperty("user.dir");
        defaultImagePath = projectPath + File.separator + "images";

        File imageDir = new File(defaultImagePath);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
    }

    private String getStoragePath() {
        String customPath = configProvider.getConfig("upload.local.path", "");
        if (customPath == null || customPath.trim().isEmpty()) {
            return defaultImagePath;
        }
        return customPath;
    }

    @Override
    public Boolean exists(String filePath) {
        return new File(getStoragePath() + File.separator + filePath).exists();
    }

    @Override
    public void upload(String path, String fileName, InputStream inputStream) throws IOException {
        String storagePath = getStoragePath();
        File directory = new File(storagePath + File.separator + path);
        if (!directory.exists()) {
            directory.mkdirs();
        }

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
        return "/api/images/" + filePath;
    }
}