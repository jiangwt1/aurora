package com.aurora.config.properties;

import com.aurora.service.SystemConfigProviderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class OssConfigProperties {

    @Autowired
    private SystemConfigProviderService configProvider;

    public String getUrl() {
        return configProvider.getConfig("upload.oss.url", "");
    }

    public String getEndpoint() {
        return configProvider.getConfig("upload.oss.endpoint", "");
    }

    public String getAccessKeyId() {
        return configProvider.getConfig("upload.oss.accessKeyId", "");
    }

    public String getAccessKeySecret() {
        return configProvider.getConfig("upload.oss.accessKeySecret", "");
    }

    public String getBucketName() {
        return configProvider.getConfig("upload.oss.bucketName", "");
    }

}
