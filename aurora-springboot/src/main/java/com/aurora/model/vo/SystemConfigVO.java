package com.aurora.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "系统配置")
public class SystemConfigVO {

    @ApiModelProperty(name = "websiteUrl", value = "网站URL", dataType = "String")
    private String websiteUrl;

    @ApiModelProperty(name = "websiteName", value = "网站名称", dataType = "String")
    private String websiteName;

    @ApiModelProperty(name = "websiteMessage", value = "网站寄语", dataType = "String")
    private String websiteMessage;

    @ApiModelProperty(name = "friendLinkUrl", value = "友链URL", dataType = "String")
    private String friendLinkUrl;

    @ApiModelProperty(name = "friendLinkEmail", value = "友链邮箱", dataType = "String")
    private String friendLinkEmail;

    @ApiModelProperty(name = "searchMode", value = "搜索模式", dataType = "String")
    private String searchMode;

    @ApiModelProperty(name = "uploadMode", value = "上传模式", dataType = "String")
    private String uploadMode;

    @ApiModelProperty(name = "uploadOssUrl", value = "OSS URL", dataType = "String")
    private String uploadOssUrl;

    @ApiModelProperty(name = "uploadOssEndpoint", value = "OSS Endpoint", dataType = "String")
    private String uploadOssEndpoint;

    @ApiModelProperty(name = "uploadOssAccessKeyId", value = "OSS AccessKeyId", dataType = "String")
    private String uploadOssAccessKeyId;

    @ApiModelProperty(name = "uploadOssAccessKeySecret", value = "OSS AccessKeySecret", dataType = "String")
    private String uploadOssAccessKeySecret;

    @ApiModelProperty(name = "uploadOssBucketName", value = "OSS BucketName", dataType = "String")
    private String uploadOssBucketName;

    @ApiModelProperty(name = "uploadMinioUrl", value = "MinIO URL", dataType = "String")
    private String uploadMinioUrl;

    @ApiModelProperty(name = "uploadMinioEndpoint", value = "MinIO Endpoint", dataType = "String")
    private String uploadMinioEndpoint;

    @ApiModelProperty(name = "uploadMinioAccesskey", value = "MinIO AccessKey", dataType = "String")
    private String uploadMinioAccesskey;

    @ApiModelProperty(name = "uploadMinioSecretKey", value = "MinIO SecretKey", dataType = "String")
    private String uploadMinioSecretKey;

    @ApiModelProperty(name = "uploadMinioBucketName", value = "MinIO BucketName", dataType = "String")
    private String uploadMinioBucketName;

    @ApiModelProperty(name = "uploadLocalUsername", value = "Local用户名", dataType = "String")
    private String uploadLocalUsername;

    @ApiModelProperty(name = "uploadLocalPassword", value = "Local密码", dataType = "String")
    private String uploadLocalPassword;

    @ApiModelProperty(name = "uploadLocalImagePath", value = "Local图片路径", dataType = "String")
    private String uploadLocalImagePath;

    @ApiModelProperty(name = "uploadLocalBaseUrl", value = "Local基础URL", dataType = "String")
    private String uploadLocalBaseUrl;

    @ApiModelProperty(name = "uploadLocalBucketName", value = "Local BucketName", dataType = "String")
    private String uploadLocalBucketName;

    @ApiModelProperty(name = "loginBackgroundImage", value = "登录背景图片URL", dataType = "String")
    private String loginBackgroundImage;

}
