import { request } from '@/utils/http'

// ========== 文章管理 ==========

// 获取文章列表
export const getArticlesApi = (params) => request.get('/api/admin/articles', { params })

// 删除文章
export const deleteArticleApi = (data) => request.delete('/api/admin/articles/delete', { data })

// 更新文章状态
export const updateArticleStatusApi = (data) => request.put('/api/admin/articles/status', data)

// 导出文章
export const exportArticlesApi = (articleIds) => request.post('/api/admin/articles/export', articleIds)

// 导入文章
export const importArticlesApi = (formData) => request.post('/api/admin/articles/import', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// 保存或更新文章
export const saveArticleApi = (data) => request.post('/api/admin/articles', data)

// 获取文章详情
export const getArticleDetailApi = (id) => request.get(`/api/admin/articles/${id}`)

// 上传文章图片
export const uploadArticleImageApi = (formData) => request.post('/api/admin/articles/images', formData, {
  headers: { 'Content-Type': 'multipart/form-data' }
})

// 修改文章置顶和推荐
export const updateArticleTopAndFeaturedApi = (data) => request.put('/api/admin/articles/topAndFeatured', data)

// 搜索文章
export const searchArticlesApi = (params) => request.get('/api/admin/articles/search', { params })

