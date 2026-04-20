<template>
  <div class="header-controls absolute top-10 right-0 flex flex-row" @keydown.k="handleOpenModel" tabindex="0">
    <span class="ob-drop-shadow" data-dia="search" @click="handleOpenModel">
      <svg-icon icon-class="search" />
    </span>
    <Dropdown v-if="multiLanguage === 1" @command="handleClick">
      <span class="ob-drop-shadow" data-dia="language">
        <svg-icon icon-class="globe" />
        <span v-if="$i18n.locale == 'cn'">中文</span>
        <span v-if="$i18n.locale == 'en'">EN</span>
      </span>
      <DropdownMenu>
        <DropdownItem name="en">English</DropdownItem>
        <DropdownItem name="cn">中文</DropdownItem>
      </DropdownMenu>
    </Dropdown>
    <template v-if="userInfo === ''">
      <span class="mr-3" @click="openLoginDialog">{{ t('settings.login') }}</span>
    </template>
    <template v-if="userInfo !== ''">
      <Dropdown hover>
        <span class="mr-2">
          <div class="flex-shrink-0">
            <div class="rounded-full ring-gray-100 overflow-hidden shaodw-lg w-9">
              <img class="avatar-img" :src="userInfo.avatar" alt="" />
            </div>
          </div>
        </span>
        <DropdownMenu>
          <template v-if="!isMobile">
            <DropdownItem @click="openUserCenter">{{ t('settings.personal-center') }}</DropdownItem>
          </template>
          <DropdownItem @click="logout">{{ t('settings.logout') }}</DropdownItem>
        </DropdownMenu>
      </Dropdown>
    </template>
    <span no-hover-effect class="ob-drop-shadow" data-dia="light-switch">
      <ThemeToggle />
    </span>
  </div>
  <el-dialog v-model="loginDialogVisible" width="30%" :fullscreen="isMobile">
    <div class="dialog-title">欢迎登录</div>
    <el-form @keyup.enter.native="login">
      <el-form-item model="userInfo">
        <div class="input-label">📧 邮箱账号</div>
        <el-input v-model="loginInfo.username" placeholder="请输入邮箱账号" />
      </el-form-item>
      <el-form-item model="userInfo" type="password">
        <div class="input-label">🔒 密码</div>
        <el-input v-model="loginInfo.password" type="password" show-password placeholder="请输入密码" />
      </el-form-item>
      <el-form-item class="mt-6">
        <el-button type="primary" @click="login" size="large" class="mx-auto">登录</el-button>
      </el-form-item>
      <el-form-item v-if="qqLoginEnabled === 1" class="mt-3">
        <el-button @click="qqLogin" size="large" class="mx-auto qq-login-btn">
          <svg-icon icon-class="qq" style="margin-right: 8px;" />
          QQ登录
        </el-button>
      </el-form-item>
      <div class="mt-5 flex justify-between items-center">
        <span class="text" @click="openRegisterDialog">立即注册</span>
        <span class="text" @click="openForgetPasswordDialog">忘记密码?</span>
      </div>
    </el-form>
  </el-dialog>
  <el-dialog v-model="registerDialogVisible" width="30%" :fullscreen="isMobile">
    <div class="dialog-title">立即注册</div>
    <el-form>
      <el-form-item model="userInfo">
        <div class="input-label">📧 邮箱账号</div>
        <el-input v-model="loginInfo.username" placeholder="请输入邮箱账号" />
      </el-form-item>
      <el-form-item model="userInfo">
        <div class="input-label">🔢 图形验证码</div>
        <div class="flex items-center">
          <el-input v-model="loginInfo.captcha" placeholder="请输入图形验证码" class="flex-1 mr-2" />
          <img 
            :src="captchaImage" 
            @click="getCaptcha" 
            class="captcha-img cursor-pointer border rounded" 
            alt="验证码"
            title="点击刷新验证码"
          />
        </div>
      </el-form-item>
      <el-form-item model="userInfo">
        <div class="input-label">✉️ 邮箱验证码</div>
        <el-input v-model="loginInfo.code" placeholder="请输入邮箱验证码">
          <template #append>
            <span class="text" @click="sendCode">发送</span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item model="userInfo" type="password">
        <div class="input-label">🔒 密码</div>
        <el-input v-model="loginInfo.password" type="password" show-password placeholder="请输入密码" />
      </el-form-item>
      <el-form-item class="mt-6">
        <el-button type="primary" @click="register" size="large" class="mx-auto">注册</el-button>
      </el-form-item>
      <div class="mt-5">
        <span class="text" @click="returnLoginDialog">已有帐号?登录</span>
      </div>
    </el-form>
  </el-dialog>
  <el-dialog v-model="forgetPasswordDialogVisible" width="30%" :fullscreen="isMobile">
    <div class="dialog-title">重置密码</div>
    <el-form>
      <el-form-item model="userInfo">
        <div class="input-label">📧 邮箱账号</div>
        <el-input v-model="loginInfo.username" placeholder="请输入邮箱账号" />
      </el-form-item>
      <el-form-item model="userInfo">
        <div class="input-label">✉️ 邮箱验证码</div>
        <el-input v-model="loginInfo.code" placeholder="请输入验证码">
          <template #append>
            <span class="text" @click="sendCode">发送</span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item model="userInfo" type="password">
        <div class="input-label">🔒 新密码</div>
        <el-input v-model="loginInfo.password" type="password" show-password placeholder="请输入新密码" />
      </el-form-item>
      <el-form-item class="mt-6">
        <el-button type="primary" @click="updatePassword" size="large" class="mx-auto">确定</el-button>
      </el-form-item>
      <div class="mt-5">
        <span class="text" @click="returnLoginDialog">返回登录</span>
      </div>
    </el-form>
  </el-dialog>
  <el-dialog v-model="articlePasswordDialogVisible" width="30%" :fullscreen="isMobile">
    <el-form @submit.native.prevent @keyup.enter.native="accessArticle">
      <el-form-item model="userInfo" class="mt-5">
        <el-input id="article-password-input" v-model="articlePassword" placeholder="文章受密码保护,请输入密码" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="accessArticle" size="large" class="mx-auto mt-3">校验密码</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
  <teleport to="body">
    <SearchModel />
  </teleport>
</template>

<script lang="ts">
import { computed, defineComponent, toRef, toRefs, reactive, getCurrentInstance, nextTick } from 'vue'
import { Dropdown, DropdownMenu, DropdownItem } from '@/components/Dropdown'
import { useAppStore } from '@/stores/app'
import { useCommonStore } from '@/stores/common'
import { useUserStore } from '@/stores/user'
import { useRoute, useRouter } from 'vue-router'
import ThemeToggle from '@/components/ToggleSwitch/ThemeToggle.vue'
import api from '@/api/api'
import SearchModel from '@/components/SearchModel.vue'
import { useSearchStore } from '@/stores/search'
import config from '@/config/config'
import { useI18n } from 'vue-i18n'
import emitter from '@/utils/mitt'

export default defineComponent({
  name: 'Controls',
  components: {
    Dropdown,
    DropdownMenu,
    DropdownItem,
    ThemeToggle,
    SearchModel
  },
  setup() {
    const { t } = useI18n()
    const proxy: any = getCurrentInstance()?.appContext.config.globalProperties
    const appStore = useAppStore()
    const commonStore = useCommonStore()
    const userStore = useUserStore()
    const searchStore = useSearchStore()
    const route = useRoute()
    const router = useRouter()
    const loginInfo = reactive({
      username: '' as any,
      password: '' as any,
      code: '' as any,
      captcha: '' as any,
      captchaUuid: '' as any
    })
    const reactiveDate = reactive({
      loginDialogVisible: false,
      registerDialogVisible: false,
      forgetPasswordDialogVisible: false,
      articlePasswordDialogVisible: false,
      articlePassword: '',
      articleId: '',
      captchaImage: '' as any
    })
    emitter.on('changeArticlePasswordDialogVisible', (articleId: any) => {
      reactiveDate.articlePasswordDialogVisible = true
      reactiveDate.articlePassword = ''
      reactiveDate.articleId = articleId
      nextTick(() => {
        document.getElementById('article-password-input')?.focus()
      })
    })
    const handleClick = (name: string): void => {
      appStore.changeLocale(name)
    }
    
    // 动态加载QQ SDK
    const loadQQSDK = (): Promise<void> => {
      return new Promise((resolve, reject) => {
        // 检查是否已加载
        //@ts-ignore
        if (window.QC) {
          resolve()
          return
        }
        
        // 创建script标签
        const script = document.createElement('script')
        script.src = 'https://connect.qq.com/qc_jssdk.js'
        script.setAttribute('data-appid', config.qqLogin.QQ_APP_ID)
        script.setAttribute('data-redirecturi', config.qqLogin.QQ_REDIRECT_URI)
        script.onload = () => resolve()
        script.onerror = () => reject(new Error('QQ SDK加载失败'))
        document.head.appendChild(script)
      })
    }
    
    const login = () => {
      if (loginInfo.username.trim().length == 0 || loginInfo.password.trim().length == 0) {
        proxy.$notify({
          title: 'Warning',
          message: '账号或者密码不能为空',
          type: 'warning'
        })
        return
      }
      let params = new URLSearchParams()
      params.append('username', loginInfo.username)
      params.append('password', loginInfo.password)
      api.login(params).then(({ data }) => {
        if (data.flag) {
          userStore.userInfo = data.data
          sessionStorage.setItem('token', data.data.token)
          userStore.token = data.data.token
          proxy.$notify({
            title: 'Success',
            message: '登录成功',
            type: 'success'
          })
          reactiveDate.loginDialogVisible = false
        }
      })
    }
    const logout = () => {
      api.logout().then(({ data }) => {
        if (data.flag) {
          userStore.userInfo = ''
          userStore.token = ''
          userStore.accessArticles = []
          sessionStorage.removeItem('token')
          proxy.$notify({
            title: 'Success',
            message: '登出成功',
            type: 'success'
          })
        }
      })
    }
    const openUserCenter = () => {
      userStore.userVisible = true
    }
    const openLoginDialog = () => {
      reactiveDate.loginDialogVisible = true
    }
    const openRegisterDialog = () => {
      loginInfo.code = ''
      loginInfo.captcha = ''
      loginInfo.captchaUuid = ''
      reactiveDate.loginDialogVisible = false
      reactiveDate.registerDialogVisible = true
      // 打开注册对话框时自动获取验证码
      getCaptcha()
    }
    const getCaptcha = (): void => {
      api.getCaptcha().then(({ data }: any) => {
        if (data.flag) {
          reactiveDate.captchaImage = data.data.image
          loginInfo.captchaUuid = data.data.uuid
        } else {
          proxy.$notify({
            title: '失败',
            message: '获取验证码失败',
            type: 'error'
          })
        }
      }).catch(() => {
        proxy.$notify({
          title: '失败',
          message: '获取验证码失败',
          type: 'error'
        })
      })
    }
    const sendCode = (): void => {
      console.log('发送验证码 - 当前数据:', {
        username: loginInfo.username,
        captcha: loginInfo.captcha,
        captchaUuid: loginInfo.captchaUuid
      })
      
      if (loginInfo.username.trim() == '') {
        proxy.$notify({
          title: '警告',
          message: '邮箱不能为空',
          type: 'warning'
        })
        return
      }
      
      if (loginInfo.captcha.trim() == '') {
        proxy.$notify({
          title: '警告',
          message: '图形验证码不能为空',
          type: 'warning'
        })
        return
      }
      
      // 发送邮箱验证码时携带图形验证码信息
      const params = {
        username: loginInfo.username,
        captcha: loginInfo.captcha,
        captchaUuid: loginInfo.captchaUuid
      }
      
      console.log('发送验证码请求参数:', params)
      
      api.sendValidationCode(params).then(({ data }: any) => {
        console.log('发送验证码响应:', data)
        if (data.flag) {
          proxy.$notify({
            title: '成功',
            message: data.message,
            type: 'success'
          })
        } else {
          proxy.$notify({
            title: '失败',
            message: data.message,
            type: 'error'
          })
          // 发送失败时刷新验证码
          getCaptcha()
        }
      }).catch((error) => {
        console.error('发送验证码错误:', error)
        proxy.$notify({
          title: '失败',
          message: error.response?.data?.message || '发送验证码失败，请重试',
          type: 'error'
        })
        // 发送失败时刷新验证码
        getCaptcha()
      })
    }
    const register = (): void => {
      console.log('开始注册 - 当前数据:', {
        username: loginInfo.username,
        password: loginInfo.password,
        code: loginInfo.code,
        captcha: loginInfo.captcha,
        captchaUuid: loginInfo.captchaUuid
      })
      
      if (loginInfo.username.trim() == '') {
        proxy.$notify({
          title: '警告',
          message: '邮箱不能为空',
          type: 'warning'
        })
        return
      }
      if (loginInfo.password.trim() == '') {
        proxy.$notify({
          title: '警告',
          message: '密码不能为空',
          type: 'warning'
        })
        return
      }
      if (loginInfo.code.trim() == '') {
        proxy.$notify({
          title: '警告',
          message: '邮箱验证码不能为空',
          type: 'warning'
        })
        return
      }
      if (loginInfo.captcha.trim() == '') {
        proxy.$notify({
          title: '警告',
          message: '图形验证码不能为空',
          type: 'warning'
        })
        return
      }
      console.log('发送注册请求，参数:', loginInfo)
      
      api.register(loginInfo).then(({ data }: any) => {
        console.log('注册响应:', data)
        if (data.flag) {
          proxy.$notify({
            title: '成功',
            message: '注册成功',
            type: 'success'
          })
          reactiveDate.registerDialogVisible = false
          reactiveDate.loginDialogVisible = true
        } else {
          proxy.$notify({
            title: '失败',
            message: data.message,
            type: 'error'
          })
          // 注册失败时刷新验证码
          getCaptcha()
        }
      }).catch((error) => {
        console.error('注册请求错误:', error)
        proxy.$notify({
          title: '失败',
          message: error.response?.data?.message || '注册失败，请重试',
          type: 'error'
        })
        // 注册失败时刷新验证码
        getCaptcha()
      })
    }
    const returnLoginDialog = (): void => {
      reactiveDate.registerDialogVisible = false
      reactiveDate.forgetPasswordDialogVisible = false
      reactiveDate.loginDialogVisible = true
    }
    const openForgetPasswordDialog = (): void => {
      reactiveDate.loginDialogVisible = false
      reactiveDate.forgetPasswordDialogVisible = true
    }
    const handleOpenModel: any = (status: boolean) => {
      searchStore.setOpenModal(status)
    }

    const qqLogin = async () => {
      try {
        // 先加载QQ SDK
        await loadQQSDK()
        
        userStore.currentUrl = route.path
        reactiveDate.loginDialogVisible = false
        
        if (commonStore.isMobile) {
          //@ts-ignore
          QC.Login.showPopup({
            appId: config.qqLogin.QQ_APP_ID,
            redirectURI: config.qqLogin.QQ_REDIRECT_URI
          })
        } else {
          window.open(
            'https://graph.qq.com/oauth2.0/show?which=Login&display=pc&client_id=' +
              config.qqLogin.QQ_APP_ID +
              '&response_type=token&scope=all&redirect_uri=' +
              config.qqLogin.QQ_REDIRECT_URI,
            '_self'
          )
        }
      } catch (error) {
        proxy.$notify({
          title: '错误',
          message: 'QQ登录功能加载失败',
          type: 'error'
        })
      }
    }
    const updatePassword = () => {
      api.updatePassword(loginInfo).then(({ data }) => {
        if (data.flag) {
          proxy.$notify({
            title: 'Success',
            message: '修改成功',
            type: 'success'
          })
          reactiveDate.forgetPasswordDialogVisible = false
          reactiveDate.loginDialogVisible = true
        }
      })
    }
    const accessArticle = () => {
      if (reactiveDate.articlePassword.trim().length == 0) {
        proxy.$notify({
          title: 'Warning',
          message: '密码不能为空',
          type: 'warning'
        })
        return
      }
      api
        .accessArticle({
          articleId: reactiveDate.articleId,
          articlePassword: reactiveDate.articlePassword
        })
        .then(({ data }) => {
          if (data.flag) {
            reactiveDate.articlePasswordDialogVisible = false
            userStore.accessArticles.push(reactiveDate.articleId)
            router.push({ path: '/articles/' + reactiveDate.articleId })
          }
        })
    }
    return {
      handleOpenModel,
      loginInfo,
      ...toRefs(reactiveDate),
      userInfo: toRef(userStore.$state, 'userInfo'),
      isMobile: toRef(commonStore.$state, 'isMobile'),
      login,
      qqLogin,
      logout,
      handleClick,
      openUserCenter,
      openLoginDialog,
      openRegisterDialog,
      returnLoginDialog,
      getCaptcha,
      sendCode,
      register,
      updatePassword,
      openForgetPasswordDialog,
      accessArticle,
      multiLanguage: computed(() => {
        let websiteConfig: any = appStore.websiteConfig
        return websiteConfig.multiLanguage
      }),
      qqLoginEnabled: computed(() => {
        let websiteConfig: any = appStore.websiteConfig
        return websiteConfig.qqLogin
      }),
      t
    }
  }
})
</script>
<style lang="scss">
.my-el-button {
  width: 300px !important;
}

.el-dialog {
  border-radius: 24px !important;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(250, 250, 255, 0.95) 100%) !important;
  backdrop-filter: blur(20px) !important;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3), 0 0 1px rgba(255, 255, 255, 0.5) inset !important;
  overflow: hidden !important;
  animation: dialogFadeIn 0.4s cubic-bezier(0.16, 1, 0.3, 1) !important;
}

@keyframes dialogFadeIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.el-dialog__header {
  padding: 0 !important;
  margin: 0 !important;
}

.el-dialog__body {
  padding: 40px 45px 45px !important;
}

.dialog-title {
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 25px;
  padding-bottom: 15px;
  position: relative;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.dialog-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, transparent, #409EFF, #66b1ff, transparent);
  border-radius: 2px;
}

.el-dialog__headerbtn {
  outline: none !important;
  top: 20px !important;
  right: 20px !important;
  width: 40px !important;
  height: 40px !important;
  font-size: 24px !important;
  color: #999 !important;
  transition: all 0.3s ease !important;
}

.el-dialog__headerbtn:hover {
  color: #333 !important;
  transform: rotate(90deg) !important;
}

.el-button {
  width: 100% !important;
  height: 45px !important;
  border-radius: 12px !important;
  font-size: 15px !important;
  font-weight: 600 !important;
  letter-spacing: 2px !important;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1) !important;
  border: none !important;
}

.el-button--primary {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%) !important;
  box-shadow: 0 4px 20px rgba(64, 158, 255, 0.4) !important;
  color: #fff !important;
}

.el-button--primary:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 8px 30px rgba(64, 158, 255, 0.5) !important;
}

.el-button--primary:active {
  transform: translateY(-1px) !important;
}

.el-form-item {
  margin-bottom: 20px !important;
}

.el-form-item:last-of-type {
  margin-bottom: 0 !important;
}

.input-label {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.el-input-group__append {
  background-color: var(--background-primary-alt) !important;
  cursor: pointer !important;
  transition: all 0.3s ease !important;
  border-radius: 0 10px 10px 0 !important;
  border: 2px solid rgba(64, 158, 255, 0.2) !important;
  border-left: none !important;
  padding: 0 18px !important;
  height: auto !important;
  display: flex !important;
  align-items: center !important;
}

.el-input-group__append:hover {
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%) !important;
  color: #fff !important;
  border-color: #409EFF !important;
}

.el-input-group__append .text {
  color: #409EFF !important;
  font-weight: 600 !important;
  font-size: 14px !important;
}

.el-input-group__append:hover .text {
  color: #fff !important;
}

.el-form-item__label {
  text-align: left;
  width: 70px;
  color: var(--text-normal) !important;
}

.el-input__inner {
  color: var(--text-normal) !important;
  background-color: var(--background-primary-alt) !important;
  border: 2px solid rgba(64, 158, 255, 0.2) !important;
  border-radius: 12px !important;
  height: 45px !important;
  line-height: 45px !important;
  padding: 0 20px !important;
  font-size: 14px !important;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1) !important;
}

.el-input__inner:focus {
  border-color: rgba(64, 158, 255, 0.6) !important;
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1) !important;
  transform: translateY(-2px) !important;
}

.el-input__wrapper {
  background: var(--background-primary-alt) !important;
  border-radius: 12px !important;
  box-shadow: none !important;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1) !important;
  padding: 1px 15px !important;
}

.el-input__wrapper:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08) !important;
}

.el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.1) !important;
}

.el-input__prefix {
  color: #409EFF !important;
  font-size: 16px !important;
}

.el-input__prefix-inner {
  display: flex !important;
  align-items: center !important;
}
</style>
<style lang="scss" scoped>
.text {
  color: #409EFF;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
  position: relative;
  display: inline-block;
}

.text::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, #409EFF, #66b1ff);
  transition: width 0.3s ease;
}

.text:hover {
  color: #66b1ff;
}

.text:hover::after {
  width: 100%;
}

#submit-button {
  outline: none;
  background: #0fb6d6;
}
.header-controls {
  span {
    display: flex;
    justify-content: center;
    align-items: center;
    color: #fff;
    cursor: pointer;
    transition: opacity 250ms ease;
    padding-right: 0.5rem;
    &[no-hover-effect] {
      &:hover {
        opacity: 1;
      }
    }
    &:hover {
      opacity: 0.5;
    }
    .svg-icon {
      stroke: #fff;
      height: 2rem;
      width: 2rem;
      margin-right: 0.5rem;
      pointer-events: none;
    }
  }
  .search-bar {
    @apply bg-transparent flex flex-row px-0 mr-2 rounded-full;
    opacity: 0;
    width: 0;
    transition: 300ms all ease-out;
    &.active {
      @apply bg-ob-deep-800;
      opacity: 0.95;
      width: 200px;
      imput {
        width: initial;
      }
    }
    &:focus {
      appearance: none;
      outline: none;
    }
    input {
      @apply flex flex-1 bg-transparent text-ob-normal px-6 box-border;
      width: 0;
      appearance: none;
      outline: none;
    }
    svg {
      @apply float-right;
    }
  }
}
.avatar-img {
  transition-property: transform;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 800ms;
  transform: rotate(-360deg);
}
.avatar-img:hover {
  transform: rotate(360deg);
}
.captcha-img {
  width: 110px;
  height: 45px;
  border: 2px solid rgba(64, 158, 255, 0.2);
  border-radius: 10px;
  transition: all 0.3s ease;
  cursor: pointer;
  object-fit: cover;
}

.captcha-img:hover {
  border-color: rgba(64, 158, 255, 0.6);
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.qq-login-btn {
  background: linear-gradient(135deg, #12B7F5 0%, #00A6ED 100%) !important;
  box-shadow: 0 4px 20px rgba(18, 183, 245, 0.4) !important;
  color: #fff !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

.qq-login-btn:hover {
  background: linear-gradient(135deg, #00A6ED 0%, #0095D9 100%) !important;
  box-shadow: 0 8px 30px rgba(18, 183, 245, 0.5) !important;
}
</style>
