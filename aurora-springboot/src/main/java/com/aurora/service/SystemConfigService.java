package com.aurora.service;

import com.aurora.entity.SystemConfig;
import com.aurora.model.dto.SystemConfigDTO;
import com.aurora.model.vo.SystemConfigVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SystemConfigService extends IService<SystemConfig> {

    SystemConfigDTO getSystemConfig();

    void updateSystemConfig(SystemConfigVO systemConfigVO);

}
