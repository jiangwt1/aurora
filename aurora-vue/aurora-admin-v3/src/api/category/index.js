import { request } from '@/utils/http'

// ========== 分类管理 ==========

// 获取分类列表
export const getCategoriesApi = (params) => request.get('/api/admin/categories', { params })

// 搜索分类
export const searchCategoriesApi = (params) => request.get('/api/admin/categories/search', { params })

// 获取所有分类（不分页）
export const getAllCategoriesApi = () => request.get('/api/admin/categories/all')

// 保存或更新分类
export const saveCategoryApi = (data) => request.post('/api/admin/categories', data)

// 删除分类（批量删除）
export const deleteCategoryApi = (ids) => request.delete('/api/admin/categories', { data: ids })

// 更新分类排序
export const updateCategorySortApi = (data) => request.put('/api/admin/categories/sort', data)
