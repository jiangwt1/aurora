import { request } from '@/utils/http'

// ========== 照片管理 ==========

// 获取照片列表
export const getPhotosApi = (params) => request.get('/api/admin/photos', { params })

// 保存照片
export const savePhotoApi = (data) => request.post('/api/admin/photos', data)

// 删除照片
export const deletePhotoApi = (data) => request.delete('/api/admin/photos', { data })

// 更新照片信息
export const updatePhotoApi = (data) => request.put('/api/admin/photos', data)

// 移动照片到回收站
export const movePhotoToTrashApi = (id) => request.put(`/api/admin/photos/${id}/trash`)

// 恢复照片
export const restorePhotoApi = (id) => request.put(`/api/admin/photos/${id}/restore`)

// 批量更新照片删除状态（恢复或移到回收站）
export const updatePhotosDeleteApi = (data) => request.put('/api/admin/photos/delete', data)

// 上传照片
export const uploadPhotoApi = (data) => request.post('/api/admin/photos/upload', data)

// ========== 相册管理 ==========

// 获取后台相册列表
export const getAlbumsApi = (params) => request.get('/api/admin/photos/albums', { params })

// 保存或更新相册
export const saveAlbumApi = (data) => request.post('/api/admin/photos/albums', data)

// 删除相册
export const deleteAlbumApi = (id) => request.delete(`/api/admin/photos/albums/${id}`)

// 上传相册封面
export const uploadAlbumCoverApi = (data) => request.post('/api/admin/photos/albums/upload', data)

// 获取后台相册列表信息
export const getAlbumsInfoApi = () => request.get('/api/admin/photos/albums/info')

// 根据id获取后台相册信息
export const getAlbumInfoApi = (id) => request.get(`/api/admin/photos/albums/${id}/info`)

// 获取相册列表（前台）
export const getPublicAlbumsApi = () => request.get('/photos/albums')
