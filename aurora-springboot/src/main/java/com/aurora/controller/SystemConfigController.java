package com.aurora.controller;

import com.aurora.annotation.OptLog;
import com.aurora.model.dto.SystemConfigDTO;
import com.aurora.model.vo.ResultVO;
import com.aurora.model.vo.SystemConfigVO;
import com.aurora.service.SystemConfigProviderService;
import com.aurora.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static com.aurora.constant.OptTypeConstant.UPDATE;

@Api(tags = "系统配置模块")
@RestController
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemConfigProviderService configProvider;

    @ApiOperation(value = "获取系统配置")
    @GetMapping("/admin/system/config")
    public ResultVO<SystemConfigDTO> getSystemConfig() {
        return ResultVO.ok(systemConfigService.getSystemConfig());
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "更新系统配置")
    @PutMapping("/admin/system/config")
    public ResultVO<?> updateSystemConfig(@Valid @RequestBody SystemConfigVO systemConfigVO) {
        systemConfigService.updateSystemConfig(systemConfigVO);
        return ResultVO.ok();
    }

    @ApiOperation(value = "测试配置读取")
    @GetMapping("/admin/system/config/test")
    public ResultVO<Map<String, String>> testConfig() {
        Map<String, String> result = new HashMap<>();
        result.put("upload.mode", configProvider.getConfig("upload.mode", "未配置"));
        result.put("search.mode", configProvider.getConfig("search.mode", "未配置"));
        result.put("website.url", configProvider.getConfig("website.url", "未配置"));
        result.put("login.backgroundImage", configProvider.getConfig("login.backgroundImage", "未配置"));
        return ResultVO.ok(result);
    }

}
