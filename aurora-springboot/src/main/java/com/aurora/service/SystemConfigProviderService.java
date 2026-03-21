package com.aurora.service;

/**
 * 系统配置提供者服务
 * 用于从数据库获取系统配置，替代yml文件配置
 */
public interface SystemConfigProviderService {

    String getConfig(String key);

    String getConfig(String key, String defaultValue);

}
