import { request } from '@/utils/http'

// ========== 角色管理 ==========

// 获取角色列表
export const getRolesApi = (params) => request.get('/api/admin/roles', { params })

// 获取所有角色（不分页）
export const getAllRolesApi = () => request.get('/api/admin/roles/all')

// 获取所有菜单
export const getAllMenusApi = () => request.get('/api/admin/role/menus')

// 获取所有资源
export const getAllResourcesApi = () => request.get('/api/admin/role/resources')

// 保存或更新角色
export const saveRoleApi = (data) => request.post('/api/admin/role', data)

// 删除角色
export const deleteRoleApi = (id) => request.delete(`/api/admin/roles/${id}`)

// 批量删除角色
export const batchDeleteRolesApi = (data) => request.delete('/api/admin/roles', { data })
