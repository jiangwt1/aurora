import { request } from '@/utils/http'

// 获取标签列表
export const getTagsApi = (params) => request.get('/api/admin/tags', { params })

// 搜索标签（不分页，用于下拉选择）
export const searchTagsApi = (params) => request.get('/api/admin/tags/search', { params })

// 保存标签
export const saveTagApi = (data) => request.post('/api/admin/tags', data)

// 删除标签
export const deleteTagApi = (ids) => request.delete('/api/admin/tags', { data: ids })

// 获取所有标签（不分页）
export const getAllTagsApi = () => request.get('/api/admin/tags/all')
