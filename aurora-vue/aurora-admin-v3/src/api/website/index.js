import { request } from '@/utils/http'

// ========== 网站配置 ==========

// 获取网站配置
export const getWebsiteConfigApi = () => request.get('/api/admin/website/config')

// 更新网站配置
export const updateWebsiteConfigApi = (data) => request.put('/api/admin/website/config', data)

// 获取作者信息
export const getAuthorInfoApi = () => request.get('/api/admin/about')

// 更新作者信息
export const updateAuthorInfoApi = (data) => request.put('/api/admin/about', data)

// 上传配置图片
export const uploadConfigImageApi = (formData) => request.post('/api/admin/config/images', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// 获取公告
export const getNoticeApi = () => request.get('/api/admin/notice')

// 更新公告
export const updateNoticeApi = (data) => request.put('/api/admin/notice', data)
