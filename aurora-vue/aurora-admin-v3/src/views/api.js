import request from '@/utils/http'

// 获取首页卡片数据
export const getHomeCardData = () => {
  return request.get('/api/admin')
}

// 获取用户地域分布
export const getUserArea = (params) => {
  return request.get('/api/admin/users/area', { params })
}
