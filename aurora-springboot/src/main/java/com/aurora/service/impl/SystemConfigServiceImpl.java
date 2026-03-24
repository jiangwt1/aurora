package com.aurora.service.impl;

import com.aurora.entity.SystemConfig;
import com.aurora.mapper.SystemConfigMapper;
import com.aurora.model.dto.SystemConfigDTO;
import com.aurora.model.vo.SystemConfigVO;
import com.aurora.service.RedisService;
import com.aurora.service.SystemConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aurora.constant.RedisConstant.SYSTEM_CONFIG;

@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public SystemConfigDTO getSystemConfig() {
        Map<String, String> configMap = getConfigMap();
        
        return SystemConfigDTO.builder()
                .websiteUrl(configMap.get("website.url"))
                .websiteName(configMap.get("website.name"))
                .websiteMessage(configMap.get("website.message"))
                .friendLinkUrl(configMap.get("friendLink.url"))
                .friendLinkEmail(configMap.get("friendLink.email"))
                .searchMode(configMap.get("search.mode"))
                .uploadMode(configMap.get("upload.mode"))
                .uploadOssUrl(configMap.get("upload.oss.url"))
                .uploadOssEndpoint(configMap.get("upload.oss.endpoint"))
                .uploadOssAccessKeyId(configMap.get("upload.oss.accessKeyId"))
                .uploadOssAccessKeySecret(configMap.get("upload.oss.accessKeySecret"))
                .uploadOssBucketName(configMap.get("upload.oss.bucketName"))
                .uploadMinioUrl(configMap.get("upload.minio.url"))
                .uploadMinioEndpoint(configMap.get("upload.minio.endpoint"))
                .uploadMinioAccesskey(configMap.get("upload.minio.accesskey"))
                .uploadMinioSecretKey(configMap.get("upload.minio.secretKey"))
                .uploadMinioBucketName(configMap.get("upload.minio.bucketName"))
                .uploadLocalUsername(configMap.get("upload.local.username"))
                .uploadLocalPassword(configMap.get("upload.local.password"))
                .uploadLocalImagePath(configMap.get("upload.local.imagePath"))
                .uploadLocalBaseUrl(configMap.get("upload.local.baseUrl"))
                .uploadLocalBucketName(configMap.get("upload.local.bucketName"))
                .loginBackgroundImage(configMap.get("login.backgroundImage"))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSystemConfig(SystemConfigVO systemConfigVO) {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("website.url", systemConfigVO.getWebsiteUrl());
        configMap.put("website.name", systemConfigVO.getWebsiteName());
        configMap.put("website.message", systemConfigVO.getWebsiteMessage());
        configMap.put("friendLink.url", systemConfigVO.getFriendLinkUrl());
        configMap.put("friendLink.email", systemConfigVO.getFriendLinkEmail());
        configMap.put("search.mode", systemConfigVO.getSearchMode());
        configMap.put("upload.mode", systemConfigVO.getUploadMode());
        configMap.put("upload.oss.url", systemConfigVO.getUploadOssUrl());
        configMap.put("upload.oss.endpoint", systemConfigVO.getUploadOssEndpoint());
        configMap.put("upload.oss.accessKeyId", systemConfigVO.getUploadOssAccessKeyId());
        configMap.put("upload.oss.accessKeySecret", systemConfigVO.getUploadOssAccessKeySecret());
        configMap.put("upload.oss.bucketName", systemConfigVO.getUploadOssBucketName());
        configMap.put("upload.minio.url", systemConfigVO.getUploadMinioUrl());
        configMap.put("upload.minio.endpoint", systemConfigVO.getUploadMinioEndpoint());
        configMap.put("upload.minio.accesskey", systemConfigVO.getUploadMinioAccesskey());
        configMap.put("upload.minio.secretKey", systemConfigVO.getUploadMinioSecretKey());
        configMap.put("upload.minio.bucketName", systemConfigVO.getUploadMinioBucketName());
        configMap.put("upload.local.username", systemConfigVO.getUploadLocalUsername());
        configMap.put("upload.local.password", systemConfigVO.getUploadLocalPassword());
        configMap.put("upload.local.imagePath", systemConfigVO.getUploadLocalImagePath());
        configMap.put("upload.local.baseUrl", systemConfigVO.getUploadLocalBaseUrl());
        configMap.put("upload.local.bucketName", systemConfigVO.getUploadLocalBucketName());
        configMap.put("login.backgroundImage", systemConfigVO.getLoginBackgroundImage());

        configMap.forEach((key, value) -> {
            SystemConfig config = systemConfigMapper.selectOne(
                    new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key)
            );
            if (config != null) {
                config.setConfigValue(value);
                systemConfigMapper.updateById(config);
            }
        });

        redisService.del(SYSTEM_CONFIG);
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
