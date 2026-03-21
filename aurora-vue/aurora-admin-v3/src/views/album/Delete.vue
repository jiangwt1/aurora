<template>
  <AppPage>
    <n-card title="照片回收站" rounded-10>
      <n-space vertical size="large">
        <!-- 操作栏 -->
        <n-space>
          <n-button type="primary" @click="handleRestoreAll">
            <template #icon><n-icon><RefreshOutline /></n-icon></template>
            恢复全部
          </n-button>
          <n-button type="error" @click="handleClearAll">
            <template #icon><n-icon><TrashOutline /></n-icon></template>
            清空回收站
          </n-button>
          <n-button @click="fetchPhotos">
            <template #icon><n-icon><RefreshOutline /></n-icon></template>
            刷新
          </n-button>
        </n-space>

        <!-- 搜索栏 -->
        <n-form inline>
          <n-form-item label="关键词">
            <n-input v-model:value="searchForm.keywords" placeholder="搜索照片" clearable />
          </n-form-item>
          <n-form-item>
            <n-button type="primary" @click="handleSearch">搜索</n-button>
          </n-form-item>
        </n-form>

        <!-- 照片网格 -->
        <n-grid x-gap="16" y-gap="16" :cols="4" responsive="screen">
          <n-gi v-for="photo in photoList" :key="photo.id">
            <n-card class="photo-card" hoverable>
              <div class="photo-container">
                <img :src="photo.url" :alt="photo.name" class="photo-image" />
                <div class="photo-overlay">
                  <n-space vertical>
                    <n-button type="success" size="small" style="width: 80px" @click="handleRestore(photo)">
                      恢复
                    </n-button>
                    <n-button type="error" size="small" style="width: 80px" @click="handleDelete(photo)">
                      彻底删除
                    </n-button>
                  </n-space>
                </div>
              </div>
              <div class="photo-info">
                <p class="photo-name">{{ photo.name }}</p>
                <p class="photo-time">{{ formatDateTime(photo.deleteTime) }}</p>
              </div>
            </n-card>
          </n-gi>
        </n-grid>

        <!-- 分页 -->
        <n-pagination
          v-model:page="pagination.page"
          :page-count="pagination.pageCount"
          @update:page="handlePageChange"
        />
      </n-space>
    </n-card>
  </AppPage>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { NIcon, useMessage, useDialog } from 'naive-ui'
import { RefreshOutline, TrashOutline } from '@vicons/ionicons5'
import { getPlaceholder } from '@/utils/placeholder'
import { getPhotosApi, deletePhotoApi, updatePhotosDeleteApi } from '@/api/photo'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const photoList = ref([])

const searchForm = reactive({
  keywords: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 16,
  itemCount: 0,
  pageCount: 1
})

function fetchPhotos() {
  loading.value = true
  getPhotosApi({
    isDelete: 1,  // 获取回收站的照片
    current: pagination.page,
    size: pagination.pageSize
  }).then(res => {
    // 处理照片数据，映射字段名
    photoList.value = (res.data.records || []).map(photo => ({
      ...photo,
      // 兼容不同的字段名
      url: photo.photoSrc || photo.url,
      name: photo.photoName || photo.name || '',
      deleteTime: photo.deleteTime || photo.updateTime || ''
    }))
    pagination.itemCount = res.data.count || 0
    pagination.pageCount = Math.ceil(pagination.itemCount / pagination.pageSize)
    loading.value = false
  }).catch(err => {
    console.error('获取回收站照片失败:', err)
    message.error('获取回收站照片失败')
    loading.value = false
  })
}

function handleSearch() {
  pagination.page = 1
  fetchPhotos()
}

function handleRestore(photo) {
  updatePhotosDeleteApi({
    ids: [photo.id],
    isDelete: 0
  }).then(() => {
    message.success('恢复照片成功')
    fetchPhotos()
  }).catch(err => {
    console.error('恢复照片失败:', err)
    message.error('恢复照片失败')
  })
}

function handleRestoreAll() {
  if (photoList.value.length === 0) {
    message.warning('回收站为空')
    return
  }

  dialog.warning({
    title: '确认恢复',
    content: `确定要恢复回收站中的所有照片吗？(${photoList.value.length}张)`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      const photoIds = photoList.value.map(photo => photo.id)
      updatePhotosDeleteApi({
        ids: photoIds,
        isDelete: 0
      }).then(() => {
        message.success(`已恢复${photoIds.length}张照片`)
        fetchPhotos()
      }).catch(err => {
        console.error('恢复照片失败:', err)
        message.error('恢复照片失败')
      })
    }
  })
}

function handleDelete(photo) {
  dialog.warning({
    title: '确认删除',
    content: '确定要彻底删除这张照片吗？此操作不可恢复！',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      deletePhotoApi([photo.id]).then(() => {
        message.success(`已删除：${photo.name}`)
        fetchPhotos()
      }).catch(err => {
        console.error('删除照片失败:', err)
        message.error('删除照片失败')
      })
    }
  })
}

function handleClearAll() {
  if (photoList.value.length === 0) {
    message.warning('回收站为空')
    return
  }

  dialog.warning({
    title: '确认清空',
    content: `确定要清空回收站吗？此操作将永久删除 ${photoList.value.length} 张照片！`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      const photoIds = photoList.value.map(photo => photo.id)
      deletePhotoApi(photoIds).then(() => {
        message.success('已清空回收站')
        fetchPhotos()
      }).catch(err => {
        console.error('清空回收站失败:', err)
        message.error('清空回收站失败')
      })
    }
  })
}

function handlePageChange(page) {
  pagination.page = page
  fetchPhotos()
}

onMounted(() => {
  fetchPhotos()
})
</script>

<style scoped>
.photo-card {
  height: 100%;
}

.photo-container {
  position: relative;
  width: 100%;
  height: 150px;
  margin-bottom: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.photo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.photo-container:hover .photo-overlay {
  opacity: 1;
}

.photo-info {
  text-align: center;
}

.photo-name {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-time {
  font-size: 12px;
  color: #999;
  margin: 0;
}
</style>