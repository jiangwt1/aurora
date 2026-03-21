import { request } from '@/utils/http'

// ========== 评论管理 ==========

// 获取评论列表
export const getCommentsApi = (params) => request.get('/api/admin/comments', { params })

// 获取待审核评论数量
export const getPendingCommentsCountApi = () => request.get('/api/admin/comments', { params: { isReview: 0, size: 1 } })

// 审核评论（批量通过）
export const reviewCommentsApi = (data) => request.put('/api/admin/comments/review', data)

// 删除评论（批量删除）
export const deleteCommentsApi = (ids) => request.delete('/api/admin/comments', { data: ids })
