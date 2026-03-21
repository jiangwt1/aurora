<template>
  <AppPage>
    <n-card title="个人中心" rounded-10>
      <n-tabs v-model:value="activeTab" type="line" animated>
        <!-- 修改信息 -->
        <n-tab-pane name="info" tab="修改信息">
          <div class="info-container">
            <n-upload
              class="avatar-uploader"
              :max="1"
              :show-file-list="false"
              :custom-request="handleAvatarUpload"
              accept="image/*"
            >
              <img v-if="userStore.userInfo?.avatar" :src="userStore.userInfo.avatar" class="avatar" />
              <div v-else class="avatar-uploader-icon">
                <n-icon size="48" color="#8c939d">
                  <AddOutline />
                </n-icon>
              </div>
            </n-upload>
            <n-form ref="infoFormRef" :model="infoForm" label-width="70px" style="width: 320px; margin-left: 3rem">
              <n-form-item label="昵称" path="nickname">
                <n-input
                  v-model:value="infoForm.nickname"
                  size="small"
                  placeholder="请输入昵称"
                />
              </n-form-item>
              <n-form-item label="个人简介" path="intro">
                <n-input
                  v-model:value="infoForm.intro"
                  size="small"
                  placeholder="请输入个人简介"
                />
              </n-form-item>
              <n-form-item label="个人网站" path="website">
                <n-input
                  v-model:value="infoForm.website"
                  size="small"
                  placeholder="请输入个人网站"
                />
              </n-form-item>
              <n-button @click="handleUpdateInfo" type="primary" size="medium" style="margin-left: 4.375rem">
                修改
              </n-button>
            </n-form>
          </div>
        </n-tab-pane>

        <!-- 修改密码 -->
        <n-tab-pane name="password" tab="修改密码">
          <n-form ref="passwordFormRef" :model="passwordForm" label-width="70px" style="width: 320px">
            <n-form-item label="旧密码" path="oldPassword">
              <n-input
                v-model:value="passwordForm.oldPassword"
                type="password"
                show-password-on="click"
                placeholder="请输入旧密码"
                size="small"
                @keyup.enter="handleUpdatePassword"
              />
            </n-form-item>
            <n-form-item label="新密码" path="newPassword">
              <n-input
                v-model:value="passwordForm.newPassword"
                type="password"
                show-password-on="click"
                placeholder="请输入新密码（至少6位）"
                size="small"
                @keyup.enter="handleUpdatePassword"
              />
            </n-form-item>
            <n-form-item label="确认密码" path="confirmPassword">
              <n-input
                v-model:value="passwordForm.confirmPassword"
                type="password"
                show-password-on="click"
                placeholder="请再次输入新密码"
                size="small"
                @keyup.enter="handleUpdatePassword"
              />
            </n-form-item>
            <n-button @click="handleUpdatePassword" type="primary" size="medium" style="margin-left: 4.4rem">
              修改
            </n-button>
          </n-form>
        </n-tab-pane>
      </n-tabs>
    </n-card>
  </AppPage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import { CameraOutline, AddOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/store/modules/user'
import { request } from '@/utils/http'

const message = useMessage()
const userStore = useUserStore()

const activeTab = ref('info')
const infoFormRef = ref(null)
const passwordFormRef = ref(null)

// 个人信息表单
const infoForm = reactive({
  nickname: '',
  intro: '',
  website: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 初始化表单数据
function initFormData() {
  // 直接从store中读取用户信息（登录时已经设置）
  if (userStore.userInfo) {
    infoForm.nickname = userStore.userInfo.nickname || ''
    infoForm.intro = userStore.userInfo.intro || ''
    infoForm.website = userStore.userInfo.website || ''
  }
}

// 上传头像
function handleAvatarUpload({ file }) {
  const formData = new FormData()
  formData.append('file', file.file)

  request.post('/api/users/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(res => {
    if (res.flag) {
      message.success(res.message || '头像上传成功')
      // 更新store中的头像
      if (userStore.userInfo) {
        userStore.userInfo.avatar = res.data
      }
    } else {
      message.error(res.message || '头像上传失败')
    }
  }).catch(err => {
    console.error('头像上传失败:', err)
    message.error('头像上传失败')
  })
}

// 修改个人信息
function handleUpdateInfo() {
  if (!infoForm.nickname || !infoForm.nickname.trim()) {
    message.error('昵称不能为空')
    return
  }

  request.put('/api/users/info', infoForm).then(res => {
    if (res.flag) {
      message.success(res.message || '修改成功')
      // 更新store中的用户信息
      if (userStore.userInfo) {
        userStore.userInfo.nickname = infoForm.nickname
        userStore.userInfo.intro = infoForm.intro
        userStore.userInfo.website = infoForm.website
      }
    } else {
      message.error(res.message || '修改失败')
    }
  }).catch(err => {
    console.error('修改个人信息失败:', err)
    message.error('修改失败')
  })
}

// 重置个人信息表单
function handleResetInfo() {
  initFormData()
  message.info('已重置')
}

// 修改密码
function handleUpdatePassword() {
  if (!passwordForm.oldPassword || !passwordForm.oldPassword.trim()) {
    message.error('旧密码不能为空')
    return
  }
  if (!passwordForm.newPassword || !passwordForm.newPassword.trim()) {
    message.error('新密码不能为空')
    return
  }
  if (passwordForm.newPassword.length < 6) {
    message.error('新密码不能少于6位')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    message.error('两次密码输入不一致')
    return
  }

  request.put('/api/admin/users/password', {
    oldPassword: passwordForm.oldPassword,
    newPassword: passwordForm.newPassword
  }).then(res => {
    if (res.flag) {
      message.success(res.message || '修改成功')
      // 清空表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
    } else {
      message.error(res.message || '修改失败')
    }
  }).catch(err => {
    console.error('修改密码失败:', err)
    message.error('修改失败')
  })
}

// 重置密码表单
function handleResetPassword() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  message.info('已重置')
}

onMounted(() => {
  initFormData()
})
</script>

<style scoped>
.avatar-uploader {
  width: 120px;
  height: 120px;
  cursor: pointer;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  object-fit: cover;
}

.info-container {
  display: flex;
  align-items: center;
  margin-left: 20%;
  margin-top: 5rem;
}
</style>
