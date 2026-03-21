import { request } from '@/utils/http'

// ========== 操作日志管理 ==========

// 获取操作日志列表
export const getOperationLogsApi = (params) => request.get('/api/admin/operation/logs', { params })

// 删除操作日志
export const deleteOperationLogsApi = (data) => request.delete('/api/admin/operation/logs', { data })

// ========== 异常日志管理 ==========

// 获取异常日志列表
export const getExceptionLogsApi = (params) => request.get('/api/admin/exception/logs', { params })

// 删除异常日志
export const deleteExceptionLogsApi = (data) => request.delete('/api/admin/exception/logs', { data })
