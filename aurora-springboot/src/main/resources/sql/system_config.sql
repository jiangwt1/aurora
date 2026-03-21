-- 系统配置表
CREATE TABLE IF NOT EXISTS `t_system_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入默认配置
INSERT INTO `t_system_config` (`config_key`, `config_value`, `config_desc`) VALUES
('website.url', '', '网站URL（用于邮件模板和文章链接显示）'),
('website.name', '', '邮件模板中显示的网站名称'),
('website.message', '', '邮件模板底部显示的网站寄语'),
('friendLink.url', '', '友链审核邮件中的链接地址'),
('friendLink.email', '', '接收友链申请通知的邮箱'),
('swagger.contact.name', '', 'Swagger文档联系人（需重启生效）'),
('swagger.contact.email', '', 'Swagger文档联系邮箱（需重启生效）'),
('swagger.termsOfServiceUrl', '', 'Swagger服务条款URL（需重启生效）'),
('swagger.termsOfServiceUrlapi', '', 'Swagger API URL（需重启生效）'),
('search.mode', 'mysql', '搜索模式：mysql或elasticsearch'),
('upload.mode', '', '上传模式：oss、minio或local'),
('upload.oss.url', '', '阿里云OSS访问URL'),
('upload.oss.endpoint', '', '阿里云OSS Endpoint'),
('upload.oss.accessKeyId', '', '阿里云OSS AccessKeyId'),
('upload.oss.accessKeySecret', '', '阿里云OSS AccessKeySecret'),
('upload.oss.bucketName', '', '阿里云OSS存储桶名称'),
('upload.minio.url', '', 'MinIO访问URL'),
('upload.minio.endpoint', '', 'MinIO服务端点'),
('upload.minio.accesskey', '', 'MinIO访问密钥'),
('upload.minio.secretKey', '', 'MinIO密钥'),
('upload.minio.bucketName', '', 'MinIO存储桶名称'),
('upload.local.baseUrl', 'http://localhost:8080/api/imange/', '本地图片访问基础URL'),
('login.backgroundImage', '', '后台登录页背景图片URL');
