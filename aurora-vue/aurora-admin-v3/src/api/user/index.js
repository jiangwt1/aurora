import { request } from '@/utils/http'

// ========== 用户管理 ==========

// 获取用户列表
export const getUsersApi = (params) => request.get('/api/admin/users', { params })

// 获取在线用户
export const getOnlineUsersApi = (params) => request.get('/api/admin/users/online', { params })

// 下线在线用户
export const offlineUserApi = (userId) => request.delete(`/api/admin/users/${userId}/online`)

// 编辑用户
export const updateUserApi = (data) => request.put('/api/admin/users', data)

// 更新用户禁用状态
export const updateUserDisableApi = (data) => request.put('/api/admin/users/disable', data)

// 更新用户角色
export const updateUserRoleApi = (data) => request.put('/api/admin/users/role', data)

// 获取角色列表
export const getUserRolesApi = () => request.get('/api/admin/users/role')

// 修改用户密码
export const updateUserPasswordApi = (data) => request.put('/api/admin/users/password', data)

// 删除用户
export const deleteUserApi = (id) => request.delete(`/api/admin/users/${id}`)

// 导出用户
export const exportUsersApi = (data) => request.post('/api/admin/users/export', data, {
  responseType: 'blob'
})
