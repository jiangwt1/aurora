<template>
  <AppPage>
    <n-card title="网站配置" rounded-10>
      <n-tabs v-model:value="activeTab" type="line" animated>
        <!-- 网站信息 -->
        <n-tab-pane name="info" tab="网站信息">
          <n-form ref="infoFormRef" :model="websiteConfig" label-placement="left" label-width="120px">
            <n-form-item label="作者头像">
            <n-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              @before-upload="handleBeforeUpload"
              @finish="handleAuthorAvatarSuccess"
            >
              <div class="avatar-uploader" v-if="websiteConfig.authorAvatar">
                <img :src="websiteConfig.authorAvatar" class="avatar" />
              </div>
              <div class="avatar-uploader" v-else>
                <n-icon size="40" color="#ccc"><AddOutline /></n-icon>
              </div>
            </n-upload>
          </n-form-item>

          <n-form-item label="网站logo">
            <n-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              @before-upload="handleBeforeUpload"
              @finish="handleLogoSuccess"
            >
              <div class="avatar-uploader" v-if="websiteConfig.logo">
                <img :src="websiteConfig.logo" class="avatar" />
              </div>
              <div class="avatar-uploader" v-else>
                <n-icon size="40" color="#ccc"><AddOutline /></n-icon>
              </div>
            </n-upload>
          </n-form-item>

          <n-form-item label="favicon">
            <n-upload
              :action="uploadUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              @before-upload="handleBeforeUpload"
              @finish="handleFaviconSuccess"
            >
              <div class="avatar-uploader" v-if="websiteConfig.favicon">
                <img :src="websiteConfig.favicon" class="avatar" />
              </div>
              <div class="avatar-uploader" v-else>
                <n-icon size="40" color="#ccc"><AddOutline /></n-icon>
              </div>
            </n-upload>
          </n-form-item>

            <n-form-item label="网站名称" path="name">
              <n-input v-model:value="websiteConfig.name" placeholder="请输入网站名称" style="width: 400px" />
            </n-form-item>
            <n-form-item label="网站英文名称" path="englishName">
              <n-input v-model:value="websiteConfig.englishName" placeholder="请输入网站英文名称" style="width: 400px" />
            </n-form-item>
            <n-form-item label="网站作者" path="author">
              <n-input v-model:value="websiteConfig.author" placeholder="请输入网站作者" style="width: 400px" />
            </n-form-item>
            <n-form-item label="网页标题" path="websiteTitle">
              <n-input v-model:value="websiteConfig.websiteTitle" placeholder="请输入网页标题" style="width: 400px" />
            </n-form-item>
            <n-form-item label="作者介绍" path="authorIntro">
              <n-input v-model:value="websiteConfig.authorIntro" type="textarea" placeholder="请输入作者介绍" style="width: 400px" :rows="4" />
            </n-form-item>
            <n-form-item label="多语言" path="multiLanguage">
              <n-radio-group v-model:value="websiteConfig.multiLanguage">
                <n-radio :value="0">关闭</n-radio>
                <n-radio :value="1">开启</n-radio>
              </n-radio-group>
            </n-form-item>
            <n-form-item label="网站创建日期" path="websiteCreateTime">
              <n-date-picker
                v-model:value="websiteConfig.websiteCreateTime"
                type="date"
                placeholder="选择日期"
                style="width: 400px"
                value-format="yyyy-MM-dd"
              />
            </n-form-item>
            <n-form-item label="网站公告" path="notice">
              <n-input
                v-model:value="websiteConfig.notice"
                type="textarea"
                placeholder="请输入公告内容"
                style="width: 400px"
                :rows="5"
              />
            </n-form-item>
            <n-form-item label="QQ登录" path="qqLogin">
              <n-radio-group v-model:value="websiteConfig.qqLogin">
                <n-radio :value="0">关闭</n-radio>
                <n-radio :value="1">开启</n-radio>
              </n-radio-group>
            </n-form-item>
            <n-form-item label="工信部备案号" path="beianNumber">
              <n-input v-model:value="websiteConfig.beianNumber" placeholder="请输入工信部备案号" style="width: 400px" />
            </n-form-item>
            <n-form-item label="公安部备案号" path="gonganBeianNumber">
              <n-input v-model:value="websiteConfig.gonganBeianNumber" placeholder="请输入公安部备案号" style="width: 400px" />
            </n-form-item>
            <n-form-item>
              <n-space>
                <n-button type="primary" @click="handleSaveWebsiteConfig">保存配置</n-button>
              </n-space>
            </n-form-item>
          </n-form>
        </n-tab-pane>

        <!-- 社交信息 -->
        <n-tab-pane name="social" tab="社交信息">
          <n-form ref="socialFormRef" :model="socialConfig" label-placement="left" label-width="120px">
            <n-form-item label="Github">
              <n-input v-model:value="socialConfig.github" placeholder="请输入Github地址" style="width: 400px" />
            </n-form-item>
            <n-form-item label="Gitee">
              <n-input v-model:value="socialConfig.gitee" placeholder="请输入Gitee地址" style="width: 400px" />
            </n-form-item>
            <n-form-item label="QQ">
              <n-input v-model:value="socialConfig.qq" placeholder="请输入QQ号" style="width: 400px" />
            </n-form-item>
            <n-form-item label="WeChat">
              <n-input v-model:value="socialConfig.weChat" placeholder="请输入微信号" style="width: 400px" />
            </n-form-item>
            <n-form-item label="微博">
              <n-input v-model:value="socialConfig.weibo" placeholder="请输入微博地址" style="width: 400px" />
            </n-form-item>
            <n-form-item label="CSDN">
              <n-input v-model:value="socialConfig.csdn" placeholder="请输入CSDN地址" style="width: 400px" />
            </n-form-item>
            <n-form-item label="知乎">
              <n-input v-model:value="socialConfig.zhihu" placeholder="请输入知乎地址" style="width: 400px" />
            </n-form-item>
            <n-form-item label="掘金">
              <n-input v-model:value="socialConfig.juejin" placeholder="请输入掘金地址" style="width: 400px" />
            </n-form-item>
            <n-form-item label="twitter">
              <n-input v-model:value="socialConfig.twitter" placeholder="请输入Twitter地址" style="width: 400px" />
            </n-form-item>
            <n-form-item label="stackoverflow">
              <n-input v-model:value="socialConfig.stackoverflow" placeholder="请输入Stackoverflow地址" style="width: 400px" />
            </n-form-item>

            <n-form-item>
              <n-space>
                <n-button type="primary" @click="handleSaveSocialConfig">保存配置</n-button>
              </n-space>
            </n-form-item>
          </n-form>
        </n-tab-pane>

        <!-- 其他配置 -->
        <n-tab-pane name="other" tab="其他配置">
          <n-form ref="otherFormRef" :model="otherConfig" label-placement="left" label-width="120px">
            <n-grid :cols="2" :x-gap="20">
              <n-gi>
                <n-form-item label="用户头像">
                  <n-upload
                    :action="uploadUrl"
                    :headers="uploadHeaders"
                    :show-file-list="false"
                    @before-upload="handleBeforeUpload"
                    @finish="handleUserAvatarSuccess"
                  >
                    <div class="avatar-uploader" v-if="otherConfig.userAvatar">
                      <img :src="otherConfig.userAvatar" class="avatar" />
                    </div>
                    <div class="avatar-uploader" v-else>
                      <n-icon size="40" color="#ccc"><AddOutline /></n-icon>
                    </div>
                  </n-upload>
                </n-form-item>
              </n-gi>
              <n-gi>
                <n-form-item label="游客头像">
                  <n-upload
                    :action="uploadUrl"
                    :headers="uploadHeaders"
                    :show-file-list="false"
                    @before-upload="handleBeforeUpload"
                    @finish="handleTouristAvatarSuccess"
                  >
                    <div class="avatar-uploader" v-if="otherConfig.touristAvatar">
                      <img :src="otherConfig.touristAvatar" class="avatar" />
                    </div>
                    <div class="avatar-uploader" v-else>
                      <n-icon size="40" color="#ccc"><AddOutline /></n-icon>
                    </div>
                  </n-upload>
                </n-form-item>
              </n-gi>
            </n-grid>

            <n-form-item label="邮箱通知">
              <n-radio-group v-model:value="otherConfig.isEmailNotice">
                <n-radio :value="1">开启</n-radio>
                <n-radio :value="0">关闭</n-radio>
              </n-radio-group>
            </n-form-item>

            <n-form-item label="评论审核">
              <n-radio-group v-model:value="otherConfig.isCommentReview">
                <n-radio :value="1">开启</n-radio>
                <n-radio :value="0">关闭</n-radio>
              </n-radio-group>
            </n-form-item>

            <n-form-item label="打赏状态">
              <n-radio-group v-model:value="otherConfig.isReward">
                <n-radio :value="1">开启</n-radio>
                <n-radio :value="0">关闭</n-radio>
              </n-radio-group>
            </n-form-item>

            <!-- 打赏收款码，只在开启打赏时显示 -->
            <div v-if="otherConfig.isReward === 1">
              <n-grid :cols="2" :x-gap="20">
                <n-gi>
                  <n-form-item label="微信收款码">
                    <n-upload
                      :action="uploadUrl"
                      :headers="uploadHeaders"
                      :show-file-list="false"
                      @before-upload="handleBeforeUpload"
                      @finish="handleWeiXinSuccess"
                    >
                      <div class="avatar-uploader" v-if="otherConfig.weiXinQRCode">
                        <img :src="otherConfig.weiXinQRCode" class="avatar" />
                      </div>
                      <div class="avatar-uploader" v-else>
                        <n-icon size="40" color="#ccc"><AddOutline /></n-icon>
                      </div>
                    </n-upload>
                  </n-form-item>
                </n-gi>
                <n-gi>
                  <n-form-item label="支付宝收款码">
                    <n-upload
                      :action="uploadUrl"
                      :headers="uploadHeaders"
                      :show-file-list="false"
                      @before-upload="handleBeforeUpload"
                      @finish="handleAlipaySuccess"
                    >
                      <div class="avatar-uploader" v-if="otherConfig.alipayQRCode">
                        <img :src="otherConfig.alipayQRCode" class="avatar" />
                      </div>
                      <div class="avatar-uploader" v-else>
                        <n-icon size="40" color="#ccc"><AddOutline /></n-icon>
                      </div>
                    </n-upload>
                  </n-form-item>
                </n-gi>
              </n-grid>
            </div>

            <n-form-item>
              <n-space>
                <n-button type="primary" @click="handleSaveOtherConfig">保存配置</n-button>
              </n-space>
            </n-form-item>
          </n-form>
        </n-tab-pane>

        <!-- 系统设置 -->
        <n-tab-pane name="system" tab="系统设置">
          <n-form ref="systemFormRef" :model="systemConfig" label-placement="left" label-width="150px">
            <n-form-item label="邮件网站URL" path="websiteUrl">
              <n-input v-model:value="systemConfig.websiteUrl" placeholder="邮件中显示的网站URL" style="width: 400px" />
            </n-form-item>
            <n-form-item label="邮件网站名称" path="websiteName">
              <n-input v-model:value="systemConfig.websiteName" placeholder="邮件中显示的网站名称" style="width: 400px" />
            </n-form-item>
            <n-form-item label="邮件签名" path="websiteMessage">
              <n-input v-model:value="systemConfig.websiteMessage" type="textarea" placeholder="邮件底部的签名或寄语" style="width: 400px" :rows="3" />
            </n-form-item>
            <n-form-item label="友链审核URL" path="friendLinkUrl">
              <n-input v-model:value="systemConfig.friendLinkUrl" placeholder="请输入友链审核URL" style="width: 400px" />
            </n-form-item>
            <n-form-item label="友链审核邮箱" path="friendLinkEmail">
              <n-input v-model:value="systemConfig.friendLinkEmail" placeholder="请输入友链审核邮箱" style="width: 400px" />
            </n-form-item>
            <n-form-item label="搜索模式" path="searchMode">
              <n-radio-group v-model:value="systemConfig.searchMode">
                <n-radio value="mysql">MySQL</n-radio>
                <n-radio value="elasticsearch">Elasticsearch</n-radio>
              </n-radio-group>
            </n-form-item>
            <n-form-item label="上传模式" path="uploadMode">
              <n-radio-group v-model:value="systemConfig.uploadMode">
                <n-radio value="oss">阿里云OSS</n-radio>
                <n-radio value="minio">MinIO</n-radio>
                <n-radio value="local">本地</n-radio>
              </n-radio-group>
            </n-form-item>

            <!-- OSS配置 -->
            <div v-if="systemConfig.uploadMode === 'oss'">
              <n-form-item label="OSS URL" path="uploadOssUrl">
                <n-input v-model:value="systemConfig.uploadOssUrl" placeholder="请输入OSS URL" style="width: 400px" />
              </n-form-item>
              <n-form-item label="OSS Endpoint" path="uploadOssEndpoint">
                <n-input v-model:value="systemConfig.uploadOssEndpoint" placeholder="请输入OSS Endpoint" style="width: 400px" />
              </n-form-item>
              <n-form-item label="OSS AccessKeyId" path="uploadOssAccessKeyId">
                <n-input v-model:value="systemConfig.uploadOssAccessKeyId" placeholder="请输入OSS AccessKeyId" style="width: 400px" />
              </n-form-item>
              <n-form-item label="OSS AccessKeySecret" path="uploadOssAccessKeySecret">
                <n-input v-model:value="systemConfig.uploadOssAccessKeySecret" type="password" show-password-on="click" placeholder="请输入OSS AccessKeySecret" style="width: 400px" />
              </n-form-item>
              <n-form-item label="OSS BucketName" path="uploadOssBucketName">
                <n-input v-model:value="systemConfig.uploadOssBucketName" placeholder="请输入OSS BucketName" style="width: 400px" />
              </n-form-item>
            </div>

            <!-- MinIO配置 -->
            <div v-if="systemConfig.uploadMode === 'minio'">
              <n-form-item label="MinIO URL" path="uploadMinioUrl">
                <n-input v-model:value="systemConfig.uploadMinioUrl" placeholder="请输入MinIO URL" style="width: 400px" />
              </n-form-item>
              <n-form-item label="MinIO Endpoint" path="uploadMinioEndpoint">
                <n-input v-model:value="systemConfig.uploadMinioEndpoint" placeholder="请输入MinIO Endpoint" style="width: 400px" />
              </n-form-item>
              <n-form-item label="MinIO AccessKey" path="uploadMinioAccesskey">
                <n-input v-model:value="systemConfig.uploadMinioAccesskey" placeholder="请输入MinIO AccessKey" style="width: 400px" />
              </n-form-item>
              <n-form-item label="MinIO SecretKey" path="uploadMinioSecretKey">
                <n-input v-model:value="systemConfig.uploadMinioSecretKey" type="password" show-password-on="click" placeholder="请输入MinIO SecretKey" style="width: 400px" />
              </n-form-item>
              <n-form-item label="MinIO BucketName" path="uploadMinioBucketName">
                <n-input v-model:value="systemConfig.uploadMinioBucketName" placeholder="请输入MinIO BucketName" style="width: 400px" />
              </n-form-item>
            </div>

            <!-- 本地配置 -->
            <div v-if="systemConfig.uploadMode === 'local'">
              <n-form-item label="基础URL" path="uploadLocalBaseUrl">
                <n-input v-model:value="systemConfig.uploadLocalBaseUrl" placeholder="必填，如：https://www.域名/api/images/" style="width: 400px" />
              </n-form-item>
            </div>

            <n-form-item label="登录背景图片">
              <n-upload
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                @before-upload="handleBeforeUpload"
                @finish="handleLoginBgSuccess"
              >
                <div class="avatar-uploader" v-if="systemConfig.loginBackgroundImage">
                  <img :src="systemConfig.loginBackgroundImage" class="avatar" />
                </div>
                <div class="avatar-uploader" v-else>
                  <n-icon size="40" color="#ccc"><AddOutline /></n-icon>
                </div>
              </n-upload>
            </n-form-item>

            <n-form-item>
              <n-space>
                <n-button type="primary" @click="handleSaveSystemConfig">保存配置</n-button>
              </n-space>
            </n-form-item>
          </n-form>
        </n-tab-pane>
      </n-tabs>
    </n-card>
  </AppPage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { NIcon, useMessage } from 'naive-ui'
import { AddOutline } from '@vicons/ionicons5'
import { getWebsiteConfigApi, updateWebsiteConfigApi } from '@/api/website'
import { getSystemConfigApi, updateSystemConfigApi } from '@/api/system'

const message = useMessage()

const activeTab = ref('info')
const infoFormRef = ref(null)
const socialFormRef = ref(null)
const otherFormRef = ref(null)
const systemFormRef = ref(null)
const uploadUrl = '/api/admin/config/images'
const uploadHeaders = {
  Authorization: 'Bearer ' + sessionStorage.getItem('token')
}

const websiteConfig = reactive({
  authorAvatar: '',
  logo: '',
  favicon: '',
  name: '',
  englishName: '',
  author: '',
  websiteTitle: '',
  authorIntro: '',
  multiLanguage: 0,
  websiteCreateTime: null,
  notice: '',
  beianNumber: '',
  gonganBeianNumber: '',
  qqLogin: 0
})

const socialConfig = reactive({
  github: '',
  gitee: '',
  qq: '',
  weChat: '',
  weibo: '',
  csdn: '',
  zhihu: '',
  juejin: '',
  twitter: '',
  stackoverflow: ''
})

const otherConfig = reactive({
  userAvatar: '',
  touristAvatar: '',
  isEmailNotice: 0,
  isCommentReview: 0,
  isReward: 0,
  weiXinQRCode: '',
  alipayQRCode: ''
})

const systemConfig = reactive({
  websiteUrl: '',
  websiteName: '',
  websiteMessage: '',
  friendLinkUrl: '',
  friendLinkEmail: '',
  searchMode: 'mysql',
  uploadMode: 'local',
  uploadOssUrl: '',
  uploadOssEndpoint: '',
  uploadOssAccessKeyId: '',
  uploadOssAccessKeySecret: '',
  uploadOssBucketName: '',
  uploadMinioUrl: '',
  uploadMinioEndpoint: '',
  uploadMinioAccesskey: '',
  uploadMinioSecretKey: '',
  uploadMinioBucketName: '',
  uploadLocalBaseUrl: '',
  loginBackgroundImage: ''
})

function fetchWebsiteConfig() {
  getWebsiteConfigApi().then(res => {
    const data = { ...res.data }
    // 将日期转换为时间戳，支持时间戳字符串和日期字符串两种格式
    if (data.websiteCreateTime) {
      let date
      if (typeof data.websiteCreateTime === 'number') {
        date = new Date(data.websiteCreateTime)
      } else if (/^\d+$/.test(data.websiteCreateTime)) {
        // 纯数字字符串，当作时间戳处理
        date = new Date(Number(data.websiteCreateTime))
      } else {
        // 日期字符串格式，如 "2023-01-01"
        date = new Date(data.websiteCreateTime)
      }
      if (!isNaN(date.getTime())) {
        data.websiteCreateTime = date.getTime()
      } else {
        data.websiteCreateTime = null
      }
    }
    // 同时更新所有配置
    Object.assign(websiteConfig, data)
    Object.assign(socialConfig, data)
    Object.assign(otherConfig, data)
  }).catch(err => {
    console.error('获取网站配置失败:', err)
    message.error('获取网站配置失败')
  })
}

function fetchSystemConfig() {
  getSystemConfigApi().then(res => {
    Object.assign(systemConfig, res.data)
  }).catch(err => {
    console.error('获取系统配置失败:', err)
    message.error('获取系统配置失败')
  })
}

function handleBeforeUpload(data) {
  const file = data.file.file
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

function getUploadResponse(file, event) {
  if (!file && !event) return null

  const xhrTarget = event?.target || event?.currentTarget
  const rawResponse = xhrTarget?.response ?? xhrTarget?.responseText ?? file?.response
  if (!rawResponse) return null

  if (typeof rawResponse === 'string') {
    try {
      return JSON.parse(rawResponse)
    } catch (error) {
      console.error('解析上传响应失败:', error)
      return null
    }
  }

  return rawResponse
}

function getUploadedImageUrl(file, event, fallback = '') {
  const response = getUploadResponse(file, event)
  if ((response?.flag || response?.code === 200) && response?.data) {
    return response.data
  }

  const fileUrl = [file?.fileUrl, file?.url].find((url) => (
    typeof url === 'string' &&
    url &&
    !url.startsWith('blob:')
  ))

  const safeFallback = typeof fallback === 'string' && !fallback.startsWith('blob:')
    ? fallback
    : ''

  return fileUrl || safeFallback
}

function hasBlobUrl(value) {
  return typeof value === 'string' && value.startsWith('blob:')
}

function validateImageUrls(data, fields) {
  const invalidField = fields.find((field) => hasBlobUrl(data[field]))
  if (!invalidField) return true

  message.error('检测到临时图片地址，请重新上传对应图片后再保存')
  return false
}

function handleAuthorAvatarSuccess({ file, event }) {
  websiteConfig.authorAvatar = getUploadedImageUrl(file, event, websiteConfig.authorAvatar)
  message.success('作者头像上传成功')
}

function handleLogoSuccess({ file, event }) {
  websiteConfig.logo = getUploadedImageUrl(file, event, websiteConfig.logo)
  message.success('logo上传成功')
}

function handleFaviconSuccess({ file, event }) {
  websiteConfig.favicon = getUploadedImageUrl(file, event, websiteConfig.favicon)
  message.success('favicon上传成功')
}

function handleUserAvatarSuccess({ file, event }) {
  otherConfig.userAvatar = getUploadedImageUrl(file, event, otherConfig.userAvatar)
  message.success('用户头像上传成功')
}

function handleTouristAvatarSuccess({ file, event }) {
  otherConfig.touristAvatar = getUploadedImageUrl(file, event, otherConfig.touristAvatar)
  message.success('游客头像上传成功')
}

function handleWeiXinSuccess({ file, event }) {
  otherConfig.weiXinQRCode = getUploadedImageUrl(file, event, otherConfig.weiXinQRCode)
  message.success('微信收款码上传成功')
}

function handleAlipaySuccess({ file, event }) {
  otherConfig.alipayQRCode = getUploadedImageUrl(file, event, otherConfig.alipayQRCode)
  message.success('支付宝收款码上传成功')
}

function handleLoginBgSuccess({ file, event }) {
  systemConfig.loginBackgroundImage = getUploadedImageUrl(file, event, systemConfig.loginBackgroundImage)
  message.success('登录背景上传成功')
}

function handleSaveWebsiteConfig() {
  const data = { ...websiteConfig }
  if (!validateImageUrls(data, ['authorAvatar', 'logo', 'favicon'])) return
  // 将时间戳转换为日期字符串
  if (data.websiteCreateTime && typeof data.websiteCreateTime === 'number') {
    const date = new Date(data.websiteCreateTime)
    data.websiteCreateTime = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }
  updateWebsiteConfigApi(data).then(() => {
    message.success('保存网站配置成功')
    fetchWebsiteConfig()
  }).catch(err => {
    console.error('保存配置失败:', err)
    message.error('保存配置失败')
  })
}

function handleSaveSocialConfig() {
  const data = { ...websiteConfig, ...socialConfig }
  if (!validateImageUrls(data, ['authorAvatar', 'logo', 'favicon'])) return
  // 将时间戳转换为日期字符串
  if (data.websiteCreateTime && typeof data.websiteCreateTime === 'number') {
    const date = new Date(data.websiteCreateTime)
    data.websiteCreateTime = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }
  updateWebsiteConfigApi(data).then(() => {
    message.success('保存社交配置成功')
    fetchWebsiteConfig()
  }).catch(err => {
    console.error('保存配置失败:', err)
    message.error('保存配置失败')
  })
}

function handleSaveOtherConfig() {
  const data = { ...websiteConfig, ...otherConfig }
  if (!validateImageUrls(data, ['authorAvatar', 'logo', 'favicon', 'userAvatar', 'touristAvatar', 'weiXinQRCode', 'alipayQRCode'])) return
  // 将时间戳转换为日期字符串
  if (data.websiteCreateTime && typeof data.websiteCreateTime === 'number') {
    const date = new Date(data.websiteCreateTime)
    data.websiteCreateTime = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }
  updateWebsiteConfigApi(data).then(() => {
    message.success('保存其他配置成功')
    fetchWebsiteConfig()
  }).catch(err => {
    console.error('保存配置失败:', err)
    message.error('保存配置失败')
  })
}

function handleSaveSystemConfig() {
  if (!validateImageUrls(systemConfig, ['loginBackgroundImage'])) return
  updateSystemConfigApi(systemConfig).then(() => {
    message.success('保存系统配置成功')
  }).catch(err => {
    console.error('保存配置失败:', err)
    message.error('保存配置失败')
  })
}

onMounted(() => {
  fetchWebsiteConfig()
  fetchSystemConfig()
})
</script>

<style scoped>
.avatar-uploader {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
