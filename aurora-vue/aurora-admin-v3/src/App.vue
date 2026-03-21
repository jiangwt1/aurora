<template>
  <n-config-provider :theme-overrides="naiveThemeOverrides">
    <n-message-provider>
      <n-dialog-provider>
        <n-notification-provider>
          <router-view />
        </n-notification-provider>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { NConfigProvider, NMessageProvider, NDialogProvider, NNotificationProvider } from 'naive-ui'
import { useWebsiteStore } from '@/store/modules/website'

const route = useRoute()
const websiteStore = useWebsiteStore()

const naiveThemeOverrides = {
  common: {
    primaryColor: '#409EFF',
    primaryColorHover: '#66B1FF',
    primaryColorPressed: '#3A8EE6',
    primaryColorSuppl: '#409EFF'
  }
}

// 动态更新favicon和页面标题
onMounted(async () => {
  // 如果是登录页，不需要获取网站配置
  if (route.path === '/login') {
    document.title = '登录 - 后台管理系统'
    return
  }

  try {
    await websiteStore.fetchWebsiteConfig()

    // 更新favicon
    const faviconUrl = websiteStore.getFavicon
    if (faviconUrl) {
      // 移除所有现有的 favicon links
      const existingLinks = document.querySelectorAll("link[rel*='icon']")
      existingLinks.forEach(link => link.remove())

      // 添加新的 favicon
      const link = document.createElement('link')
      link.rel = 'icon'
      link.href = faviconUrl
      document.getElementsByTagName('head')[0].appendChild(link)
    }

    // 更新页面标题
    document.title = '后台管理系统'
  } catch (error) {
    // 对于40001（未登录）错误，静默处理
    if (error.code !== 40001) {
      console.error('初始化网站配置失败:', error)
    }
  }
})
</script>

<style>
#app {
  width: 100%;
  height: 100%;
}
</style>
