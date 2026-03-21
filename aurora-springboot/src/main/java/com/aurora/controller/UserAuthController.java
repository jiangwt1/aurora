package com.aurora.controller;


import com.aurora.annotation.AccessLimit;
import com.aurora.annotation.OptLog;
import com.aurora.model.dto.*;
import com.aurora.service.UserAuthService;
import com.aurora.model.vo.*;
import com.aurora.model.vo.SendCodeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.aurora.constant.OptTypeConstant.UPDATE;

@Api(tags = "用户账号模块")
@RestController
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;
    @ApiOperation(value = "发送邮箱验证码")
    @PostMapping("/users/code")
    public ResultVO<?> sendCode(@Valid @RequestBody SendCodeVO sendCodeVO) {
        userAuthService.sendCode(sendCodeVO);
        return ResultVO.ok();
    }

    @ApiOperation(value = "获取图形验证码")
    @GetMapping("/captcha")
    public ResultVO<CaptchaDTO> getCaptcha() {
        return ResultVO.ok(userAuthService.getCaptcha());
    }

    @ApiOperation(value = "获取用户区域分布")
    @GetMapping("/admin/users/area")
    public ResultVO<List<UserAreaDTO>> listUserAreas(ConditionVO conditionVO) {
        return ResultVO.ok(userAuthService.listUserAreas(conditionVO));
    }

    @ApiOperation(value = "查询后台用户列表")
    @GetMapping("/admin/users")
    public ResultVO<PageResultDTO<UserAdminDTO>> listUsers(ConditionVO conditionVO) {
        return ResultVO.ok(userAuthService.listUsers(conditionVO));
    }

    @AccessLimit(seconds = 300, maxCount = 3)
    @ApiOperation(value = "用户注册")
    @PostMapping("/users/register")
    public ResultVO<?> register(@Valid @RequestBody UserVO userVO) {
        userAuthService.register(userVO);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改密码")
    @PutMapping("/users/password")
    public ResultVO<?> updatePassword(@Valid @RequestBody UserVO user) {
        userAuthService.updatePassword(user);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改管理员密码")
    @PutMapping("/admin/users/password")
    public ResultVO<?> updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO) {
        userAuthService.updateAdminPassword(passwordVO);
        return ResultVO.ok();
    }

    @ApiOperation("用户登出")
    @PostMapping("/users/logout")
    public ResultVO<UserLogoutStatusDTO> logout() {
        return ResultVO.ok(userAuthService.logout());
    }

    @ApiOperation(value = "qq登录")
    @PostMapping("/users/oauth/qq")
    public ResultVO<UserInfoDTO> qqLogin(@Valid @RequestBody QQLoginVO qqLoginVO) {
        return ResultVO.ok(userAuthService.qqLogin(qqLoginVO));
    }

}
