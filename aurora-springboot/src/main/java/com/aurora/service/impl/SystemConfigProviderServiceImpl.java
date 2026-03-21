package com.aurora.service.impl;

import com.aurora.entity.SystemConfig;
import com.aurora.mapper.SystemConfigMapper;
import com.aurora.service.RedisService;
import com.aurora.service.SystemConfigProviderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aurora.constant.RedisConstant.SYSTEM_CONFIG;

@Service
public class SystemConfigProviderServiceImpl implements SystemConfigProviderService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public String getConfig(String key) {
        return getConfig(key, null);
    }

    @Override
    public String getConfig(String key, String defaultValue) {
        Map<String, String> configMap = getConfigMap();
        String value = configMap.get(key);
        return value != null ? value : defaultValue;
    }

    private Map<String, String> getConfigMap() {
        @SuppressWarnings("unchecked")
        Map<String, String> configMap = (Map<String, String>) redisService.get(SYSTEM_CONFIG);
        
        if (configMap == null) {
            configMap = new HashMap<>();
            List<SystemConfig> configList = systemConfigMapper.selectList(null);
            for (SystemConfig config : configList) {
                configMap.put(config.getConfigKey(), config.getConfigValue());
            }
            redisService.set(SYSTEM_CONFIG, configMap);
        }
        
        return configMap;
    }

}
