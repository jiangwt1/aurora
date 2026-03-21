import { request } from '@/utils/http'

// 获取说说列表
export const getTalksApi = (params) => request.get('/api/admin/talks', { params })

// 获取说说详情
export const getTalkDetailApi = (id) => request.get(`/api/admin/talks/${id}`)

// 保存说说
export const saveTalkApi = (data) => request.post('/api/admin/talks', data)

// 删除说说
export const deleteTalkApi = (data) => request.delete('/api/admin/talks', { data })

// 更新说说
export const updateTalkApi = (data) => request.put('/api/admin/talks', data)
