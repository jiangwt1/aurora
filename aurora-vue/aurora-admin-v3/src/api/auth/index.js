import { request } from '@/utils/http'

// ========== 认证相关 ==========

// 用户登录
export const loginApi = (data) => {
  const formData = `username=${encodeURIComponent(data.username)}&password=${encodeURIComponent(data.password)}`
  return request.post('/api/users/login', formData, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

// 用户登出
export const logoutApi = () => request.post('/api/users/logout')

// 获取用户信息
export const getUserInfoApi = () => request.get('/api/admin/users/info')

// 刷新token
export const refreshTokenApi = () => request.post('/api/users/refresh')
