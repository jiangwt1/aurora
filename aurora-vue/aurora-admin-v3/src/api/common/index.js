import { request } from '@/utils/http'

// ========== 公共接口 ==========

// 上传图片
export const uploadImageApi = (formData) => request.post('/api/admin/upload/image', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// 获取关于我信息
export const getAboutApi = () => request.get('/api/about')

// 更新关于我信息
export const updateAboutApi = (data) => request.put('/api/admin/about', data)

// 获取统计数据
export const getStatisticsApi = () => request.get('/api/admin/statistics')
