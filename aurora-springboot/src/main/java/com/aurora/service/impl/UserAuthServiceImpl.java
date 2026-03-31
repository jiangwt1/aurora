package com.aurora.service.impl;

import com.alibaba.fastjson.JSON;
import com.aurora.constant.CommonConstant;
import com.aurora.model.dto.*;
import com.aurora.entity.UserAuth;
import com.aurora.entity.UserInfo;
import com.aurora.entity.UserRole;
import com.aurora.enums.LoginTypeEnum;
import com.aurora.enums.RoleEnum;
import com.aurora.exception.BizException;
import com.aurora.mapper.UserAuthMapper;
import com.aurora.mapper.UserInfoMapper;
import com.aurora.mapper.UserRoleMapper;
import com.aurora.service.*;
import com.aurora.strategy.context.SocialLoginStrategyContext;
import com.aurora.util.PageUtil;
import com.aurora.util.UserUtil;
import com.aurora.model.vo.*;
import com.aurora.model.vo.SendCodeVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.aurora.constant.RabbitMQConstant.EMAIL_EXCHANGE;
import static com.aurora.constant.RedisConstant.*;
import static com.aurora.enums.UserAreaTypeEnum.getUserAreaType;
import static com.aurora.util.CommonUtil.checkEmail;
import static com.aurora.util.CommonUtil.getRandomCode;
import com.aurora.util.CaptchaUtil;
import com.aurora.util.EmailDomainValidator;
import java.util.UUID;


@Service
public class UserAuthServiceImpl implements UserAuthService {

    @Autowired
    private UserAuthMapper userAuthMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AuroraInfoService auroraInfoService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SocialLoginStrategyContext socialLoginStrategyContext;

    @Autowired
    private SystemConfigProviderService configProvider;

    @Override
    public void sendCode(SendCodeVO sendCodeVO) {
        System.out.println("发送验证码请求 - 邮箱: " + sendCodeVO.getUsername() + ", 图形验证码: " + sendCodeVO.getCaptcha() + ", UUID: " + sendCodeVO.getCaptchaUuid());
        
        if (!checkEmail(sendCodeVO.getUsername())) {
            throw new BizException("邮箱格式不对!");
        }
        
        // 验证图形验证码参数
        if (sendCodeVO.getCaptcha() == null || sendCodeVO.getCaptcha().trim().isEmpty()) {
            throw new BizException("图形验证码不能为空！");
        }
        if (sendCodeVO.getCaptchaUuid() == null || sendCodeVO.getCaptchaUuid().trim().isEmpty()) {
            throw new BizException("验证码UUID不能为空！");
        }
        
        // 验证图形验证码
        String captchaKey = "captcha:" + sendCodeVO.getCaptchaUuid();
        String storedCaptcha = (String) redisService.get(captchaKey);
        System.out.println("Redis中的验证码: " + storedCaptcha + ", 用户输入: " + sendCodeVO.getCaptcha().toLowerCase());
        
        if (storedCaptcha == null) {
            throw new BizException("图形验证码已过期！");
        }
        if (!sendCodeVO.getCaptcha().toLowerCase().equals(storedCaptcha)) {
            throw new BizException("图形验证码错误！");
        }
        
        // 验证通过，但不删除验证码，留给注册时使用
        // 可以设置一个标记表示此验证码已用于发送邮件
        redisService.set(captchaKey + ":used", "true", 300L);
        
        String websiteUrl = configProvider.getConfig("website.url", "https://www.gysy.ltd");
        String websiteName = configProvider.getConfig("website.name", "Aurora博客");
        
        String code = getRandomCode();
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("url", websiteUrl);
        commentMap.put("name", websiteName);
        commentMap.put("content", "您的验证码是：<strong style='color:#49BDAD;font-size:18px;'>" + code + "</strong><br/>验证码有效期为15分钟，请尽快使用。");
        commentMap.put("message", "此邮件由系统自动发送，请勿回复。如果您没有进行此操作，请忽略此邮件。");
        EmailDTO emailDTO = EmailDTO.builder()
                .email(sendCodeVO.getUsername())
                .subject("邮箱验证码")
                .commentMap(commentMap)
                .template("common.html")
                .build();
        rabbitTemplate.convertAndSend(EMAIL_EXCHANGE, "*", new Message(JSON.toJSONBytes(emailDTO), new MessageProperties()));
        redisService.set(USER_CODE_KEY + sendCodeVO.getUsername(), code, CODE_EXPIRE_TIME);
        
        System.out.println("验证码发送成功: " + code);
    }

    @Override
    public CaptchaDTO getCaptcha() {
        // 
        CaptchaUtil.CaptchaResult captchaResult = CaptchaUtil.generateCaptcha();
        
        // 生成唯一标识
        String uuid = UUID.randomUUID().toString();
        
        // 将验证码文本存储到Redis，5分钟过期
        redisService.set("captcha:" + uuid, captchaResult.getText().toLowerCase(), 300L);
        
        return CaptchaDTO.builder()
                .image(captchaResult.getImage())
                .uuid(uuid)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<UserAreaDTO> listUserAreas(ConditionVO conditionVO) {
        List<UserAreaDTO> userAreaDTOs = new ArrayList<>();
        switch (Objects.requireNonNull(getUserAreaType(conditionVO.getType()))) {
            case USER:
                Object userArea = redisService.get(USER_AREA);
                if (Objects.nonNull(userArea)) {
                    userAreaDTOs = JSON.parseObject(userArea.toString(), List.class);
                }
                return userAreaDTOs;
            case VISITOR:
                Map<String, Object> visitorArea = redisService.hGetAll(VISITOR_AREA);
                if (Objects.nonNull(visitorArea)) {
                    userAreaDTOs = visitorArea.entrySet().stream()
                            .map(item -> UserAreaDTO.builder()
                                    .name(item.getKey())
                                    .value(Long.valueOf(item.getValue().toString()))
                                    .build())
                            .collect(Collectors.toList());
                }
                return userAreaDTOs;
            default:
                break;
        }
        return userAreaDTOs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserVO userVO) {
        if (!checkEmail(userVO.getUsername())) {
            throw new BizException("邮箱格式不对!");
        }
        
        // 验证邮箱域名是否被允许
        if (!EmailDomainValidator.isAllowedDomain(userVO.getUsername())) {
            if (EmailDomainValidator.isTemporaryEmail(userVO.getUsername())) {
                throw new BizException("不允许使用临时邮箱注册！");
            } else {
                throw new BizException("该邮箱域名不在允许范围内，请使用常见邮箱服务商！");
            }
        }
        
        if (checkUser(userVO)) {
            throw new BizException("邮箱已被注册！");
        }
        UserInfo userInfo = UserInfo.builder()
                .email(userVO.getUsername())
                .nickname(CommonConstant.DEFAULT_NICKNAME + IdWorker.getId())
                .avatar(auroraInfoService.getWebsiteConfig().getUserAvatar())
                .build();
        userInfoMapper.insert(userInfo);
        UserRole userRole = UserRole.builder()
                .userId(userInfo.getId())
                .roleId(RoleEnum.USER.getRoleId())
                .build();
        userRoleMapper.insert(userRole);
        UserAuth userAuth = UserAuth.builder()
                .userInfoId(userInfo.getId())
                .username(userVO.getUsername())
                .password(BCrypt.hashpw(userVO.getPassword(), BCrypt.gensalt()))
                .loginType(LoginTypeEnum.EMAIL.getType())
                .build();
        userAuthMapper.insert(userAuth);
    }

    @Override
    public void updatePassword(UserVO userVO) {
        if (!checkUser(userVO)) {
            throw new BizException("邮箱尚未注册！");
        }
        userAuthMapper.update(new UserAuth(), new LambdaUpdateWrapper<UserAuth>()
                .set(UserAuth::getPassword, BCrypt.hashpw(userVO.getPassword(), BCrypt.gensalt()))
                .eq(UserAuth::getUsername, userVO.getUsername()));
    }

    @Override
    @SuppressWarnings("all")
    public void updateAdminPassword(PasswordVO passwordVO) {
        UserAuth user = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .eq(UserAuth::getId, UserUtil.getUserDetailsDTO().getId()));
        if (Objects.nonNull(user) && BCrypt.checkpw(passwordVO.getOldPassword(), user.getPassword())) {
            UserAuth userAuth = UserAuth.builder()
                    .id(UserUtil.getUserDetailsDTO().getId())
                    .password(BCrypt.hashpw(passwordVO.getNewPassword(), BCrypt.gensalt()))
                    .build();
            userAuthMapper.updateById(userAuth);
        } else {
            throw new BizException("旧密码不正确");
        }
    }

    @Override
    public PageResultDTO<UserAdminDTO> listUsers(ConditionVO conditionVO) {
        Integer count = userAuthMapper.countUser(conditionVO);
        if (count == 0) {
            return new PageResultDTO<>();
        }
        List<UserAdminDTO> UserAdminDTOs = userAuthMapper.listUsers(PageUtil.getLimitCurrent(), PageUtil.getSize(), conditionVO);
        return new PageResultDTO<>(UserAdminDTOs, count);
    }

    @SneakyThrows
    @Override
    public UserLogoutStatusDTO logout() {
        tokenService.delLoginUser(UserUtil.getUserDetailsDTO().getId());
        return new UserLogoutStatusDTO("注销成功");
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserInfoDTO qqLogin(QQLoginVO qqLoginVO) {
        return socialLoginStrategyContext.executeLoginStrategy(JSON.toJSONString(qqLoginVO), LoginTypeEnum.QQ);
    }

    private Boolean checkUser(UserVO user) {
        // 验证邮箱验证码
        if (!user.getCode().equals(redisService.get(USER_CODE_KEY + user.getUsername()))) {
            throw new BizException("邮箱验证码错误！");
        }
        
        // 验证图形验证码
        String captchaKey = "captcha:" + user.getCaptchaUuid();
        String storedCaptcha = (String) redisService.get(captchaKey);
        if (storedCaptcha == null) {
            throw new BizException("图形验证码已过期！");
        }
        if (!user.getCaptcha().toLowerCase().equals(storedCaptcha)) {
            throw new BizException("图形验证码错误！");
        }
        
        // 检查验证码是否已被使用过发送邮件
        String usedFlag = (String) redisService.get(captchaKey + ":used");
        if (usedFlag == null) {
            throw new BizException("请先发送邮箱验证码！");
        }
        
        // 验证成功后删除验证码和使用标记
        redisService.del(captchaKey);
        redisService.del(captchaKey + ":used");
        
        UserAuth userAuth = userAuthMapper.selectOne(new LambdaQueryWrapper<UserAuth>()
                .select(UserAuth::getUsername)
                .eq(UserAuth::getUsername, user.getUsername()));
        return Objects.nonNull(userAuth);
    }

}
