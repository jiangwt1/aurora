import { defineStore } from 'pinia'
import { getWebsiteConfigApi } from '@/api/website'

export const useWebsiteStore = defineStore('website', {
  state: () => ({
    config: null, // 网站配置
    logo: '', // 网站logo
    name: '', // 网站名称
    authorAvatar: '', // 作者头像
    favicon: '' // 网站图标
  }),

  getters: {
    // 获取logo
    getLogo: (state) => state.logo || state.config?.logo || '',
    // 获取网站名称
    getName: (state) => state.name || state.config?.name || 'Aurora',
    // 获取favicon
    getFavicon: (state) => state.favicon || state.config?.favicon || ''
  },

  actions: {
    // 获取网站配置
    async fetchWebsiteConfig() {
      try {
        const res = await getWebsiteConfigApi()
        this.config = res.data
        this.logo = res.data.logo || ''
        this.name = res.data.name || ''
        this.authorAvatar = res.data.authorAvatar || ''
        this.favicon = res.data.favicon || ''
        return res
      } catch (error) {
        // 对于40001（未登录）错误，静默处理
        if (error.code !== 40001) {
          console.error('获取网站配置失败:', error)
        }
        return Promise.reject(error)
      }
    },

    // 更新logo
    updateLogo(logo) {
      this.logo = logo
      if (this.config) {
        this.config.logo = logo
      }
    }
  },

  // 持久化配置
  persist: {
    key: 'website',
    storage: window.sessionStorage,
    paths: ['config', 'logo', 'name', 'authorAvatar', 'favicon']
  }
})
