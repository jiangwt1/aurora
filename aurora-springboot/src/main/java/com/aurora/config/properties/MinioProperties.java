package com.aurora.config.properties;

import com.aurora.service.SystemConfigProviderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class MinioProperties {

    @Autowired
    private SystemConfigProviderService configProvider;

    public String getUrl() {
        return configProvider.getConfig("upload.minio.url", "");
    }

    public String getEndpoint() {
        return configProvider.getConfig("upload.minio.endpoint", "");
    }

    public String getAccessKey() {
        return configProvider.getConfig("upload.minio.accesskey", "");
    }

    public String getSecretKey() {
        return configProvider.getConfig("upload.minio.secretKey", "");
    }

    public String getBucketName() {
        return configProvider.getConfig("upload.minio.bucketName", "");
    }
}
