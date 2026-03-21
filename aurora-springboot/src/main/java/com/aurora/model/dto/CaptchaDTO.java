package com.aurora.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图形验证码DTO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaDTO {
    
    /**
     * 验证码图片Base64
     */
    private String image;
    
    /**
     * 验证码唯一标识
     */
    private String uuid;
}
