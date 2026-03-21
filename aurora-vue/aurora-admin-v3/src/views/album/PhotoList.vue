<template>
  <AppPage>
    <n-card rounded-10>
      <n-space vertical size="large">
        <!-- 操作栏 -->
        <n-space justify="space-between">
          <n-space>
            <n-button @click="goBack">
              <template #icon><n-icon><ArrowBackOutline /></n-icon></template>
              返回相册
            </n-button>
            <n-button type="primary" @click="handleUpload">
              <template #icon><n-icon><CloudUploadOutline /></n-icon></template>
              上传照片
            </n-button>
          </n-space>
        </n-space>

        <!-- 相册信息 -->
        <n-card v-if="albumInfo" size="small" class="album-info-card">
          <n-space align="center">
            <n-avatar :size="60" :src="albumInfo.cover" />
            <div>
              <h3>{{ albumInfo.name }}</h3>
              <p class="album-desc">{{ albumInfo.description }}</p>
            </div>
          </n-space>
        </n-card>

        <!-- 照片列表 -->
        <n-spin :show="loading">
          <n-empty v-if="!loading && photoList.length === 0" description="暂无照片" />
          <n-grid v-else x-gap="16" y-gap="16" :cols="4" responsive="screen">
            <n-gi v-for="photo in photoList" :key="photo.id">
              <n-card class="photo-card" hoverable>
                <img :src="photo.url" :alt="photo.name" class="photo-image" />
                <div class="photo-actions">
                  <n-space>
                    <n-popconfirm @positive-click="handleDelete(photo)">
                      <template #trigger>
                        <n-button size="small" type="error">删除</n-button>
                      </template>
                      确定删除这张照片吗？
                    </n-popconfirm>
                  </n-space>
                </div>
              </n-card>
            </n-gi>
          </n-grid>
        </n-spin>

        <!-- 分页 -->
        <n-pagination
          v-if="pagination.total > 0"
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="pagination.total"
          :show-size-picker="true"
          :page-sizes="[10, 20]"
          @update:page="handlePageChange"
          @update:page-size="handlePageSizeChange"
          style="display: flex; justify-content: flex-end; margin-top: 16px;"
        />
      </n-space>
    </n-card>

    <!-- 上传对话框 -->
    <n-modal v-model:show="showUploadDialog" preset="card" title="上传照片" style="width: 600px">
      <n-space vertical size="large">
        <!-- 上传区域 -->
        <n-upload
          :max="10"
          :custom-request="handleUploadRequest"
          :before-upload="handleBeforeUpload"
          :show-file-list="false"
          accept="image/*"
          multiple
          directory-dnd
        >
          <n-upload-dragger>
            <div style="margin-bottom: 12px">
              <n-icon size="48" :depth="3">
                <CloudUploadOutline />
              </n-icon>
            </div>
            <n-text style="font-size: 16px">
              点击或者拖动文件到该区域来上传
            </n-text>
            <n-p depth="3" style="margin: 8px 0 0 0">
              支持批量上传，单张图片不超过5MB
            </n-p>
          </n-upload-dragger>
        </n-upload>

        <!-- 已上传照片列表 -->
        <div v-if="uploadList.length > 0">
          <n-divider />
          <n-text strong>已上传 {{ uploadList.length }} 张照片</n-text>
          <n-grid x-gap="12" y-gap="12" :cols="3" style="margin-top: 12px">
            <n-gi v-for="item in uploadList" :key="item.id">
              <n-card size="small" hoverable style="position: relative">
                <img :src="item.url" :alt="item.name" style="width: 100%; height: 120px; object-fit: cover; border-radius: 4px" />
                <div style="position: absolute; top: 8px; right: 8px">
                  <n-button circle size="small" type="error" @click="handleRemove(item)">
                    <template #icon>
                      <n-icon><CloseOutline /></n-icon>
                    </template>
                  </n-button>
                </div>
              </n-card>
            </n-gi>
          </n-grid>
        </div>
      </n-space>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showUploadDialog = false">取消</n-button>
          <n-button type="primary" :loading="isUploading" :disabled="uploadList.length === 0" @click="handleSavePhotos">
            保存到相册
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </AppPage>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NButton, NIcon, NModal, NCard, NText, NP, NDivider, NUpload, NUploadDragger, NGrid, NGi, NPagination, NPopconfirm, useMessage } from 'naive-ui'
import { ArrowBackOutline, CloudUploadOutline, CloseOutline } from '@vicons/ionicons5'
import { getPhotosApi, deletePhotoApi, getAlbumInfoApi, uploadPhotoApi, savePhotoApi, updatePhotosDeleteApi } from '@/api/photo'
import { getPlaceholder } from '@/utils/placeholder'

const route = useRoute()
const router = useRouter()
const message = useMessage()

const loading = ref(false)
const photoList = ref([])
const albumInfo = ref(null)
const showUploadDialog = ref(false)
const uploadList = ref([])
const isUploading = ref(false)

const pagination = ref({
  page: 1,
  pageSize: 16,
  total: 0
})

const albumId = route.params.id

function fetchPhotos() {
  loading.value = true
  getPhotosApi({
    albumId: albumId,
    isDelete: 0,
    current: pagination.value.page,
    size: pagination.value.pageSize
  }).then(res => {
    // 处理照片数据，映射字段名
    photoList.value = (res.data.records || []).map(photo => ({
      ...photo,
      // 兼容不同的字段名
      url: photo.photoSrc || photo.url,
      name: photo.photoName || photo.name || ''
    }))
    pagination.value.total = res.data.count || 0
    loading.value = false
  }).catch(err => {
    console.error('获取照片列表失败:', err)
    message.error('获取照片列表失败')
    loading.value = false
  })
}

function fetchAlbumInfo() {
  getAlbumInfoApi(albumId).then(res => {
    albumInfo.value = {
      ...res.data,
      name: res.data.albumName || res.data.name,
      description: res.data.albumDesc || res.data.description,
      cover: res.data.albumCover || res.data.cover
    }
  }).catch(err => {
    console.error('获取相册信息失败:', err)
  })
}

function goBack() {
  router.push('/albums')
}

function handleUpload() {
  showUploadDialog.value = true
  uploadList.value = []
}

// 自定义上传请求
function handleUploadRequest({ file, onFinish, onError }) {
  const formData = new FormData()
  formData.append('file', file.file)

  uploadPhotoApi(formData).then(res => {
    if (res.flag) {
      // 添加到上传列表
      uploadList.value.push({
        id: Date.now() + Math.random(), // 临时ID
        file: file,
        url: res.data, // 上传成功返回的URL
        name: file.name
      })
      message.success(`上传 ${file.name} 成功`)
      onFinish()
    } else {
      message.error(`上传 ${file.name} 失败`)
      onError()
    }
  }).catch(err => {
    console.error('上传失败:', err)
    message.error(`上传 ${file.name} 失败`)
    onError()
  })
}

// 移除上传项
function handleRemove(uploadItem) {
  const index = uploadList.value.findIndex(item => item.id === uploadItem.id)
  if (index > -1) {
    uploadList.value.splice(index, 1)
  }
}

// 保存照片到相册
async function handleSavePhotos() {
  if (uploadList.value.length === 0) {
    message.warning('请先上传照片')
    return
  }

  isUploading.value = true
  try {
    const photoUrls = uploadList.value.map(item => item.url)

    await savePhotoApi({
      albumId: albumId,
      photoUrls: photoUrls
    })

    message.success(`成功保存 ${uploadList.value.length} 张照片`)
    showUploadDialog.value = false
    uploadList.value = []
    fetchPhotos()
    fetchAlbumInfo() // 更新相册照片数量
  } catch (err) {
    console.error('保存照片失败:', err)
    message.error('保存照片失败')
  } finally {
    isUploading.value = false
  }
}

// 上传前验证
function handleBeforeUpload(file) {
  const isImage = file.file.type.startsWith('image/')
  const isLt5M = file.file.size / 1024 / 1024 < 5

  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    message.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

function handleDelete(photo) {
  updatePhotosDeleteApi({
    ids: [photo.id],
    isDelete: 1
  }).then(() => {
    message.success('删除成功')
    fetchPhotos()
    fetchAlbumInfo() // 更新相册照片数量
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handlePageChange(page) {
  pagination.value.page = page
  fetchPhotos()
}

function handlePageSizeChange(pageSize) {
  pagination.value.pageSize = pageSize
  pagination.value.page = 1
  fetchPhotos()
}

onMounted(() => {
  fetchAlbumInfo()
  fetchPhotos()
})
</script>

<style scoped>
.album-info-card {
  margin-bottom: 16px;
}

.album-info-card h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
}

.album-desc {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.photo-card {
  padding: 0;
  overflow: hidden;
}

.photo-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  display: block;
}

.photo-actions {
  padding: 12px;
  text-align: right;
}
</style>
