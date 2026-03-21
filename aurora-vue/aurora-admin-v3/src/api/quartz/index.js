import { request } from '@/utils/http'

// ========== 定时任务管理 ==========

// 获取定时任务列表
export const getJobsApi = (params) => request.get('/api/admin/jobs', { params })

// 获取任务组列表
export const getJobGroupsApi = () => request.get('/api/admin/jobs/jobGroups')

// 新增任务
export const saveJobApi = (data) => request.post('/api/admin/jobs', data)

// 更新任务
export const updateJobApi = (data) => request.put('/api/admin/jobs', data)

// 删除任务
export const deleteJobApi = (ids) => request.delete('/api/admin/jobs', { data: ids })

// 修改任务状态
export const changeJobStatusApi = (data) => request.put('/api/admin/jobs/status', data)

// 执行任务
export const runJobApi = (data) => request.put('/api/admin/jobs/run', data)

// 获取任务日志列表
export const getJobLogsApi = (params) => request.get('/api/admin/jobLogs', { params })

// 批量删除任务日志
export const deleteJobLogsApi = (ids) => request.delete('/api/admin/jobLogs', { data: ids })

// 清空任务日志
export const cleanJobLogsApi = () => request.delete('/api/admin/jobLogs/clean')
