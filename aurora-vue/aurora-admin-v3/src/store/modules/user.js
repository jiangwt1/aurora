import { defineStore } from 'pinia'
import { loginApi, getUserInfoApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: sessionStorage.getItem('token') || '',
    userInfo: null,
    permissions: [], // 权限列表
    roles: [] // 角色列表
  }),

  // 持久化配置
  persist: {
    key: 'user',
    storage: window.sessionStorage,
    paths: ['token', 'userInfo'] // 只持久化token和userInfo
  },

  getters: {
    // 是否已登录
    isLoggedIn: (state) => !!state.token,
    // 获取用户头像
    avatar: (state) => state.userInfo?.avatar || '',
    // 获取用户昵称
    nickName: (state) => state.userInfo?.nickName || '管理员'
  },

  actions: {
    // 登录
    async login(loginForm) {
      try {
        const res = await loginApi(loginForm)

        // 保存token和用户信息
        this.token = res.data.token
        this.userInfo = res.data
        sessionStorage.setItem('token', res.data.token)

        return res
      } catch (error) {
        return Promise.reject(error)
      }
    },

    // 登出
    async logout() {
      this.token = ''
      this.userInfo = null
      this.permissions = []
      this.roles = []
      sessionStorage.removeItem('token')
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        const res = await getUserInfoApi()
        this.userInfo = res.data
        return res
      } catch (error) {
        return Promise.reject(error)
      }
    }
  }
})
