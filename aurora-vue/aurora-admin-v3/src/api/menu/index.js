import { request } from '@/utils/http'

// ========== 菜单管理 ==========

// 获取用户菜单
export const getUserMenusApi = () => request.get('/api/admin/user/menus')

// 获取所有菜单（树形结构）
export const getAllMenusApi = () => request.get('/api/admin/menus')

// 获取菜单列表（支持搜索）
export const getMenusApi = (params) => request.get('/api/admin/menus', { params })

// 保存或更新菜单
export const saveMenuApi = (data) => request.post('/api/admin/menus', data)

// 删除菜单
export const deleteMenuApi = (id) => request.delete(`/api/admin/menus/${id}`)

// 更新菜单隐藏状态
export const updateMenuHiddenApi = (data) => request.put('/api/admin/menus/hidden', data)

// 更新菜单排序
export const updateMenuSortApi = (data) => request.put('/api/admin/menus/sort', data)
