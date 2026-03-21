import { request } from '@/utils/http'

// ========== 系统配置 ==========

// 获取网站配置
export const getWebsiteConfigApi = () => request.get('/api/admin/website/config')

// 更新网站配置
export const updateWebsiteConfigApi = (data) => request.put('/api/admin/website/config', data)

// 获取系统配置
export const getSystemConfigApi = () => request.get('/api/admin/system/config')

// 更新系统配置
export const updateSystemConfigApi = (data) => request.put('/api/admin/system/config', data)
