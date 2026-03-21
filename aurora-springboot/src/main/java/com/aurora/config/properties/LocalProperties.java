package com.aurora.config.properties;

import com.aurora.service.SystemConfigProviderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Data
@Component
public class LocalProperties {

    @Autowired
    private SystemConfigProviderService configProvider;

    public String getBaseUrl() {
        return configProvider.getConfig("upload.local.baseUrl", "");
    }
}
