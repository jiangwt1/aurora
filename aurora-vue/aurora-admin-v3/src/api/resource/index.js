import { request } from '@/utils/http'

// ========== 资源权限管理 ==========

// 获取资源列表（树形结构）
export const getResourcesApi = () => request.get('/api/admin/resources')

// 保存或更新资源
export const saveResourceApi = (data) => request.post('/api/admin/resources', data)

// 删除资源
export const deleteResourceApi = (id) => request.delete(`/api/admin/resources/${id}`)

// 修改资源匿名访问状态
export const changeResourceAnonymousApi = (data) => request.put('/api/admin/resources/anonymous', data)

// 获取用户资源权限
export const getUserResourcesApi = () => request.get('/api/admin/user/resources')
