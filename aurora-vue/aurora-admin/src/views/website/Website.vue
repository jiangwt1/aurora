<template>
  <el-card class="main-card">
    <el-tabs v-model="activeName">
      <el-tab-pane label="网站信息" name="info">
        <el-form label-width="100px" :model="websiteConfigForm" label-position="left">
          <el-form-item label="作者头像">
            <el-upload
              class="avatar-uploader"
              action="/api/admin/config/images"
              :headers="headers"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="handleAuthorAvatarSuccess">
              <img v-if="websiteConfigForm.authorAvatar" :src="websiteConfigForm.authorAvatar" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
          <el-form-item label="网站logo">
            <el-upload
              class="avatar-uploader"
              action="/api/admin/config/images"
              :headers="headers"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="handleLogoSuccess">
              <img v-if="websiteConfigForm.logo" :src="websiteConfigForm.logo" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
          <el-form-item label="favicon">
            <el-upload
              class="avatar-uploader"
              action="/api/admin/config/images"
              :headers="headers"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="handleFaviconSuccess">
              <img v-if="websiteConfigForm.favicon" :src="websiteConfigForm.favicon" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
          <el-form-item label="网站名称">
            <el-input v-model="websiteConfigForm.name" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="网站英文名称">
            <el-input v-model="websiteConfigForm.englishName" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="网站作者">
            <el-input v-model="websiteConfigForm.author" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="网页标题">
            <el-input v-model="websiteConfigForm.websiteTitle" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="作者介绍">
            <el-input v-model="websiteConfigForm.authorIntro" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="多语言">
            <el-radio-group v-model="websiteConfigForm.multiLanguage">
              <el-radio :label="0">关闭</el-radio>
              <el-radio :label="1">开启</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="网站创建日期">
            <el-date-picker
              style="width: 400px"
              value-format="yyyy-MM-dd"
              v-model="websiteConfigForm.websiteCreateTime"
              type="date"
              placeholder="选择日期" />
          </el-form-item>
          <el-form-item label="网站公告">
            <el-input
              v-model="websiteConfigForm.notice"
              placeholder="请输入公告内容"
              style="width: 400px"
              type="textarea"
              :rows="5" />
          </el-form-item>
          <el-form-item label="工信部备案号">
            <el-input v-model="websiteConfigForm.beianNumber" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="公安部备案号">
            <el-input v-model="websiteConfigForm.gonganBeianNumber" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="qq登录">
            <el-radio-group v-model="websiteConfigForm.qqLogin">
              <el-radio :label="0">关闭</el-radio>
              <el-radio :label="1">开启</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-button type="primary" size="medium" style="margin-left: 6.3rem" @click="updateWebsiteConfig">
            修改
          </el-button>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="社交信息" name="notice">
        tip:空白默认不显示
        <el-form label-width="70px" :model="websiteConfigForm">
          <el-form-item label="Github">
            <el-input v-model="websiteConfigForm.github" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-form-item label="Gitee">
            <el-input v-model="websiteConfigForm.gitee" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-form-item label="QQ">
            <el-input v-model="websiteConfigForm.qq" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-form-item label="WeChat">
            <el-input v-model="websiteConfigForm.weChat" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-form-item label="微博">
            <el-input v-model="websiteConfigForm.weibo" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-form-item label="CSDN">
            <el-input v-model="websiteConfigForm.csdn" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-form-item label="知乎">
            <el-input v-model="websiteConfigForm.zhihu" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-form-item label="掘金">
            <el-input v-model="websiteConfigForm.juejin" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-form-item label="twitter">
            <el-input v-model="websiteConfigForm.twitter" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-form-item label="stackoverflow">
            <el-input v-model="websiteConfigForm.stackoverflow" size="small" style="width: 400px; margin-right: 1rem" />
          </el-form-item>
          <el-button type="primary" size="medium" style="margin-left: 4.375rem" @click="updateWebsiteConfig">
            修改
          </el-button>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="其他设置" name="password">
        <el-form label-width="120px" :model="websiteConfigForm" label-position="left">
          <el-row style="width: 600px">
            <el-col :md="12">
              <el-form-item label="用户头像">
                <el-upload
                  class="avatar-uploader"
                  action="/api/admin/config/images"
                  :headers="headers"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  :on-success="handleUserAvatarSuccess">
                  <img v-if="websiteConfigForm.userAvatar" :src="websiteConfigForm.userAvatar" class="avatar" />
                  <i v-else class="el-icon-plus avatar-uploader-icon" />
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :md="12">
              <el-form-item label="游客头像">
                <el-upload
                  class="avatar-uploader"
                  action="/api/admin/config/images"
                  :headers="headers"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  :on-success="handleTouristAvatarSuccess">
                  <img v-if="websiteConfigForm.touristAvatar" :src="websiteConfigForm.touristAvatar" class="avatar" />
                  <i v-else class="el-icon-plus avatar-uploader-icon" />
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="邮箱通知">
            <el-radio-group v-model="websiteConfigForm.isEmailNotice">
              <el-radio :label="1">开启</el-radio>
              <el-radio :label="0">关闭</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="评论审核">
            <el-radio-group v-model="websiteConfigForm.isCommentReview">
              <el-radio :label="1">开启</el-radio>
              <el-radio :label="0">关闭</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="打赏状态">
            <el-radio-group v-model="websiteConfigForm.isReward">
              <el-radio :label="1">开启</el-radio>
              <el-radio :label="0">关闭</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-row style="width: 600px" v-show="websiteConfigForm.isReward == 1">
            <el-col :md="12">
              <el-form-item label="微信收款码">
                <el-upload
                  class="avatar-uploader"
                  action="/api/admin/config/images"
                  :headers="headers"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  :on-success="handleWeiXinSuccess">
                  <img v-if="websiteConfigForm.weiXinQRCode" :src="websiteConfigForm.weiXinQRCode" class="avatar" />
                  <i v-else class="el-icon-plus avatar-uploader-icon" />
                </el-upload>
              </el-form-item>
            </el-col>
            <el-col :md="12">
              <el-form-item label="支付宝收款码">
                <el-upload
                  class="avatar-uploader"
                  action="/api/admin/config/images"
                  :headers="headers"
                  :show-file-list="false"
                  :before-upload="beforeUpload"
                  :on-success="handleAlipaySuccess">
                  <img v-if="websiteConfigForm.alipayQRCode" :src="websiteConfigForm.alipayQRCode" class="avatar" />
                  <i v-else class="el-icon-plus avatar-uploader-icon" />
                </el-upload>
              </el-form-item>
            </el-col>
          </el-row>
          <el-button type="primary" size="medium" style="margin-left: 6.3rem" @click="updateWebsiteConfig">
            修改
          </el-button>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="系统设置" name="system">
        <el-form label-width="120px" :model="systemConfigForm" label-position="left">
          <el-form-item label="网站URL">
            <el-input v-model="systemConfigForm.websiteUrl" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="网站名称">
            <el-input v-model="systemConfigForm.websiteName" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="网站寄语">
            <el-input v-model="systemConfigForm.websiteMessage" size="small" style="width: 400px" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="友链审核URL">
            <el-input v-model="systemConfigForm.friendLinkUrl" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="友链审核邮箱">
            <el-input v-model="systemConfigForm.friendLinkEmail" size="small" style="width: 400px" />
          </el-form-item>
          <el-form-item label="搜索模式">
            <el-radio-group v-model="systemConfigForm.searchMode">
              <el-radio label="mysql">MySQL</el-radio>
              <el-radio label="elasticsearch">Elasticsearch</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="上传模式">
            <el-radio-group v-model="systemConfigForm.uploadMode">
              <el-radio label="oss">阿里云OSS</el-radio>
              <el-radio label="minio">MinIO</el-radio>
              <el-radio label="local">本地</el-radio>
            </el-radio-group>
          </el-form-item>
          <div v-show="systemConfigForm.uploadMode === 'oss'">
            <el-form-item label="OSS URL">
              <el-input v-model="systemConfigForm.uploadOssUrl" size="small" style="width: 400px" />
            </el-form-item>
            <el-form-item label="OSS Endpoint">
              <el-input v-model="systemConfigForm.uploadOssEndpoint" size="small" style="width: 400px" />
            </el-form-item>
            <el-form-item label="OSS AccessKeyId">
              <el-input v-model="systemConfigForm.uploadOssAccessKeyId" size="small" style="width: 400px" />
            </el-form-item>
            <el-form-item label="OSS AccessKeySecret">
              <el-input v-model="systemConfigForm.uploadOssAccessKeySecret" size="small" style="width: 400px" type="password" show-password />
            </el-form-item>
            <el-form-item label="OSS BucketName">
              <el-input v-model="systemConfigForm.uploadOssBucketName" size="small" style="width: 400px" />
            </el-form-item>
          </div>
          <div v-show="systemConfigForm.uploadMode === 'minio'">
            <el-form-item label="MinIO URL">
              <el-input v-model="systemConfigForm.uploadMinioUrl" size="small" style="width: 400px" />
            </el-form-item>
            <el-form-item label="MinIO Endpoint">
              <el-input v-model="systemConfigForm.uploadMinioEndpoint" size="small" style="width: 400px" />
            </el-form-item>
            <el-form-item label="MinIO AccessKey">
              <el-input v-model="systemConfigForm.uploadMinioAccesskey" size="small" style="width: 400px" />
            </el-form-item>
            <el-form-item label="MinIO SecretKey">
              <el-input v-model="systemConfigForm.uploadMinioSecretKey" size="small" style="width: 400px" type="password" show-password />
            </el-form-item>
            <el-form-item label="MinIO BucketName">
              <el-input v-model="systemConfigForm.uploadMinioBucketName" size="small" style="width: 400px" />
            </el-form-item>
          </div>
          <div v-show="systemConfigForm.uploadMode === 'local'">
            <el-form-item label="基础URL" required>
              <el-input v-model="systemConfigForm.uploadLocalBaseUrl" size="small" style="width: 400px" placeholder="必填，如：https://www.域名/api/images/" />
            </el-form-item>
          </div>
          <el-form-item label="登录背景图片">
            <el-upload
              class="avatar-uploader"
              action="/api/admin/config/images"
              :headers="headers"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="handleLoginBgSuccess">
              <img v-if="systemConfigForm.loginBackgroundImage" :src="systemConfigForm.loginBackgroundImage" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon" />
            </el-upload>
          </el-form-item>
          <el-button type="primary" size="medium" style="margin-left: 6.3rem" @click="updateSystemConfig">
            修改
          </el-button>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script>
import * as imageConversion from 'image-conversion'

export default {
  created() {
    this.getWebsiteConfig()
    this.getSystemConfig()
  },
  data: function () {
    return {
      websiteConfigForm: {},
      systemConfigForm: {},
      activeName: 'info',
      headers: { Authorization: 'Bearer ' + sessionStorage.getItem('token') }
    }
  },
  methods: {
    getWebsiteConfig() {
      this.axios.get('/api/admin/website/config').then(({ data }) => {
        this.websiteConfigForm = data.data
      })
    },
    getSystemConfig() {
      this.axios.get('/api/admin/system/config').then(({ data }) => {
        this.systemConfigForm = data.data
      })
    },
    handleAuthorAvatarSuccess(response) {
      if (response.flag === false) {
        this.$notify.error({ title: '上传失败', message: response.message, duration: 5000 })
        return
      }
      this.websiteConfigForm.authorAvatar = response.data
    },
    handleFaviconSuccess(response) {
      if (response.flag === false) {
        this.$notify.error({ title: '上传失败', message: response.message, duration: 5000 })
        return
      }
      this.websiteConfigForm.favicon = response.data
    },
    handleLogoSuccess(response) {
      if (response.flag === false) {
        this.$notify.error({ title: '上传失败', message: response.message, duration: 5000 })
        return
      }
      this.websiteConfigForm.logo = response.data
    },
    handleUserAvatarSuccess(response) {
      if (response.flag === false) {
        this.$notify.error({ title: '上传失败', message: response.message, duration: 5000 })
        return
      }
      this.websiteConfigForm.userAvatar = response.data
    },
    handleTouristAvatarSuccess(response) {
      if (response.flag === false) {
        this.$notify.error({ title: '上传失败', message: response.message, duration: 5000 })
        return
      }
      this.websiteConfigForm.touristAvatar = response.data
    },
    handleWeiXinSuccess(response) {
      if (response.flag === false) {
        this.$notify.error({ title: '上传失败', message: response.message, duration: 5000 })
        return
      }
      this.websiteConfigForm.weiXinQRCode = response.data
    },
    handleAlipaySuccess(response) {
      if (response.flag === false) {
        this.$notify.error({ title: '上传失败', message: response.message, duration: 5000 })
        return
      }
      this.websiteConfigForm.alipayQRCode = response.data
    },
    handleLoginBgSuccess(response) {
      if (response.flag === false) {
        this.$notify.error({ title: '上传失败', message: response.message, duration: 5000 })
        return
      }
      this.systemConfigForm.loginBackgroundImage = response.data
    },
    beforeUpload(file) {
      return new Promise((resolve) => {
        if (file.size / 1024 < this.config.UPLOAD_SIZE) {
          resolve(file)
        }
        imageConversion.compressAccurately(file, this.config.UPLOAD_SIZE).then((res) => {
          resolve(res)
        })
      })
    },
    updateWebsiteConfig() {
      this.axios.put('/api/admin/website/config', this.websiteConfigForm).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: '成功',
            message: data.message
          })
        } else {
          this.$notify.error({
            title: '失败',
            message: data.message
          })
        }
      })
    },
    updateSystemConfig() {
      // 过滤掉不需要的本地上传配置字段
      const configToSubmit = { ...this.systemConfigForm }
      delete configToSubmit.uploadLocalUsername
      delete configToSubmit.uploadLocalPassword
      delete configToSubmit.uploadLocalImagePath
      delete configToSubmit.uploadLocalBucketName
      
      this.axios.put('/api/admin/system/config', configToSubmit).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: '成功',
            message: '系统配置更新成功'
          })
        } else {
          this.$notify.error({
            title: '失败',
            message: data.message
          })
        }
      })
    }
  }
}
</script>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}
.avatar {
  width: 120px;
  height: 120px;
  display: block;
}
</style>
