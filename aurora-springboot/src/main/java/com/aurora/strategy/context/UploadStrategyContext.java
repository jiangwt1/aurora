package com.aurora.strategy.context;

import com.aurora.service.SystemConfigProviderService;
import com.aurora.strategy.UploadStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

import static com.aurora.enums.UploadModeEnum.getStrategy;

@Service
public class UploadStrategyContext {

    @Autowired
    private SystemConfigProviderService configProvider;

    @Autowired
    private Map<String, UploadStrategy> uploadStrategyMap;

    public String executeUploadStrategy(MultipartFile file, String path) {
        String uploadMode = configProvider.getConfig("upload.mode", "local");
        return uploadStrategyMap.get(getStrategy(uploadMode)).uploadFile(file, path);
    }

    public String executeUploadStrategy(String fileName, InputStream inputStream, String path) {
        String uploadMode = configProvider.getConfig("upload.mode", "local");
        return uploadStrategyMap.get(getStrategy(uploadMode)).uploadFile(fileName, inputStream, path);
    }

}
