<template>
  <div class="oauth-background">
    <div id="preloader_1">
      <span></span>
      <span></span>
      <span></span>
      <span></span>
      <span></span>
    </div>
  </div>
</template>
<script lang="ts">
import { defineComponent, getCurrentInstance, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useUserStore } from '@/stores/user'
import api from '@/api/api'
export default defineComponent({
  name: 'OauthLoginModel',
  setup() {
    const proxy: any = getCurrentInstance()?.appContext.config.globalProperties
    const userStore = useUserStore()
    const route = useRoute()
    const router = useRouter()
    const isLoading = ref(true)

    const handleQQLogin = async () => {
      try {
        if (route.path === '/oauth/login/qq') {
          // @ts-ignore
          if (QC.Login.check()) {
            // @ts-ignore
            QC.Login.getMe(function (openId, accessToken) {
              const params = {
                openId: openId,
                accessToken: accessToken
              }
              api.qqLogin(params).then(({ data }) => {
                if (data.flag) {
                  // 正确设置用户信息到 store
                  userStore.userInfo = data.data
                  userStore.token = data.data.token
                  userStore.userVisible = false
                  sessionStorage.setItem('token', data.data.token)

                  // 显示成功通知
                  proxy.$notify({
                    title: 'Success',
                    message: '登录成功',
                    type: 'success'
                  })

                  // 延迟跳转，让用户看到成功提示
                  setTimeout(() => {
                    isLoading.value = false
                    if (userStore.currentUrl === '' || userStore.currentUrl === '/oauth/login/qq') {
                      router.push({ path: '/' })
                    } else {
                      router.push({ path: userStore.currentUrl })
                    }
                  }, 800)
                } else {
                  proxy.$notify({
                    title: 'Error',
                    message: data.message || '登录失败',
                    type: 'error'
                  })
                  setTimeout(() => {
                    router.push({ path: '/' })
                  }, 1500)
                }
              }).catch((err) => {
                console.error('QQ登录失败:', err)
                proxy.$notify({
                  title: 'Error',
                  message: '登录失败，请重试',
                  type: 'error'
                })
                setTimeout(() => {
                  router.push({ path: '/' })
                }, 1500)
              })
            })
          } else {
            // QQ登录未授权
            proxy.$notify({
              title: 'Warning',
              message: '请先授权QQ登录',
              type: 'warning'
            })
            setTimeout(() => {
              router.push({ path: '/' })
            }, 1500)
          }
        }
      } catch (error) {
        console.error('登录处理失败:', error)
        router.push({ path: '/' })
      }
    }

    onMounted(() => {
      // 等待QQ SDK加载完成后执行
      setTimeout(() => {
        handleQQLogin()
      }, 500)
    })

    return {
      isLoading
    }
  }
})
</script>
<style lang="scss" scoped>
.oauth-background {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  z-index: 1000;
}
#preloader_1 {
  position: relative;
  top: 45vh;
  left: 45vw;
}
#preloader_1 span {
  display: block;
  bottom: 0px;
  width: 9px;
  height: 5px;
  background: #9b59b6;
  position: absolute;
  animation: preloader_1 1.5s infinite ease-in-out;
}
#preloader_1 span:nth-child(2) {
  left: 11px;
  animation-delay: 0.2s;
}
#preloader_1 span:nth-child(3) {
  left: 22px;
  animation-delay: 0.4s;
}
#preloader_1 span:nth-child(4) {
  left: 33px;
  animation-delay: 0.6s;
}
#preloader_1 span:nth-child(5) {
  left: 44px;
  animation-delay: 0.8s;
}
@keyframes preloader_1 {
  0% {
    height: 5px;
    transform: translateY(0px);
    background: #9b59b6;
  }
  25% {
    height: 30px;
    transform: translateY(15px);
    background: #3498db;
  }
  50% {
    height: 5px;
    transform: translateY(0px);
    background: #9b59b6;
  }
  100% {
    height: 5px;
    transform: translateY(0px);
    background: #9b59b6;
  }
}
</style>
