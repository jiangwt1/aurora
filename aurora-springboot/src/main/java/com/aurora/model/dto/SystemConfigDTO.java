package com.aurora.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigDTO {

    private String websiteUrl;

    private String websiteName;

    private String websiteMessage;

    private String friendLinkUrl;

    private String friendLinkEmail;

    private String searchMode;

    private String uploadMode;

    private String uploadOssUrl;

    private String uploadOssEndpoint;

    private String uploadOssAccessKeyId;

    private String uploadOssAccessKeySecret;

    private String uploadOssBucketName;

    private String uploadMinioUrl;

    private String uploadMinioEndpoint;

    private String uploadMinioAccesskey;

    private String uploadMinioSecretKey;

    private String uploadMinioBucketName;

    private String uploadLocalPath;

    private String loginBackgroundImage;

}
