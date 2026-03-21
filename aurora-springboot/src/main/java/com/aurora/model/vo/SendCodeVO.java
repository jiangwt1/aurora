package com.aurora.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "发送验证码请求")
public class SendCodeVO {

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(name = "username", value = "用户名", required = true, dataType = "String")
    private String username;

    @NotBlank(message = "图形验证码不能为空")
    @ApiModelProperty(name = "captcha", value = "图形验证码", required = true, dataType = "String")
    private String captcha;

    @NotBlank(message = "验证码标识不能为空")
    @ApiModelProperty(name = "captchaUuid", value = "图形验证码标识", required = true, dataType = "String")
    private String captchaUuid;

}
