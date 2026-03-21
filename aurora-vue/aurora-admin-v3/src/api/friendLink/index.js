import { request } from '@/utils/http'

// ========== 友链管理 ==========

// 获取友链列表
export const getFriendLinksApi = (params) => request.get('/api/admin/links', { params })

// 保存友链（新增和编辑都用 POST）
export const saveFriendLinkApi = (data) => request.post('/api/admin/links', data)

// 删除友链
export const deleteFriendLinkApi = (ids) => request.delete('/api/admin/links', { data: ids })
