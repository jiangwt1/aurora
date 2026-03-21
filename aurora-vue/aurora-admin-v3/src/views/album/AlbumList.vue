<template>
  <AppPage>
    <n-card title="相册管理" rounded-10>
      <n-space vertical size="large">
        <!-- 操作栏 -->
        <n-space justify="space-between">
          <n-space>
            <n-button type="primary" @click="handleAddAlbum">
              <template #icon><n-icon><AddOutline /></n-icon></template>
              新建相册
            </n-button>
            <n-button @click="fetchAlbums">
              <template #icon><n-icon><RefreshOutline /></n-icon></template>
              刷新
            </n-button>
            <n-button @click="handleGoToDelete">
              <template #icon><n-icon><ImageOutline /></n-icon></template>
              回收站
            </n-button>
          </n-space>
          <n-space>
            <n-input
              v-model:value="keywords"
              placeholder="请输入相册名"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
            <n-button type="primary" @click="handleSearch">搜索</n-button>
          </n-space>
        </n-space>

        <!-- 相册列表 -->
        <n-spin :show="loading">
          <n-empty v-if="!loading && albumList.length === 0" description="暂无相册" />
          <n-grid v-else x-gap="16" y-gap="16" :cols="4" responsive="screen">
            <n-gi v-for="album in albumList" :key="album.id">
              <n-card class="album-card" hoverable @click="handleViewPhotos(album)">
                <div class="album-cover">
                  <img v-if="album.cover" :src="album.cover" :alt="album.name" />
                  <div v-else class="album-placeholder">
                    <img :src="getPlaceholder(300, 200, '暂无封面')" class="placeholder-image" />
                  </div>
                </div>
                <div class="album-info">
                  <h3 class="album-name">{{ album.name }}</h3>
                  <p class="album-desc">{{ album.description || '暂无描述' }}</p>
                  <div class="album-meta">
                    <span>{{ album.photoCount }} 张照片</span>
                  </div>
                </div>
                <div class="album-actions" @click.stop>
                  <n-button-group>
                    <n-button size="small" @click="handleEditAlbum(album)">编辑</n-button>
                    <n-popconfirm @positive-click="() => handleDeleteAlbum(album)">
                      <template #trigger>
                        <n-button size="small" type="error">删除</n-button>
                      </template>
                      确定删除该相册吗？
                    </n-popconfirm>
                  </n-button-group>
                </div>
              </n-card>
            </n-gi>
          </n-grid>
        </n-spin>

        <!-- 分页 -->
        <n-pagination
          v-if="pagination.total > 0"
          v-model:page="pagination.current"
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

    <!-- 添加/编辑对话框 -->
    <n-modal v-model:show="showEditDialog" preset="dialog" :title="dialogTitle">
      <n-form ref="formRef" :model="albumForm" :rules="rules" label-placement="left" label-width="80px">
        <n-form-item label="相册名称" path="albumName">
          <n-input v-model:value="albumForm.albumName" placeholder="请输入相册名称" />
        </n-form-item>
        <n-form-item label="相册描述" path="albumDesc">
          <n-input v-model:value="albumForm.albumDesc" type="textarea" placeholder="请输入相册描述" />
        </n-form-item>
        <n-form-item label="相册封面" path="albumCover">
          <n-upload
            :max="1"
            :custom-request="handleUploadCover"
            :show-file-list="false"
            accept="image/*"
          >
            <n-button>选择图片</n-button>
          </n-upload>
          <div v-if="albumForm.albumCover" class="cover-preview">
            <img :src="albumForm.albumCover" alt="封面预览" />
          </div>
        </n-form-item>
        <n-form-item label="发布形式" path="status">
          <n-radio-group v-model:value="albumForm.status">
            <n-radio :value="1">公开</n-radio>
            <n-radio :value="2">私密</n-radio>
          </n-radio-group>
        </n-form-item>
      </n-form>
      <template #action>
        <n-space>
          <n-button @click="showEditDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSubmit">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </AppPage>
</template>

<script setup>
import { ref, h, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NIcon, NPopconfirm, NPagination, useMessage, NModal, NForm, NFormItem, NInput, NRadioGroup, NRadio, NUpload } from 'naive-ui'
import { AddOutline, RefreshOutline, ImageOutline } from '@vicons/ionicons5'
import { getPlaceholder } from '@/utils/placeholder'
import { getAlbumsApi, deleteAlbumApi, saveAlbumApi, uploadAlbumCoverApi } from '@/api/photo'

const router = useRouter()
const message = useMessage()

const albumList = ref([])
const loading = ref(false)
const keywords = ref('')

// 对话框相关
const showEditDialog = ref(false)
const dialogTitle = ref('新建相册')
const formRef = ref(null)
const albumForm = ref({
  id: null,
  albumName: '',
  albumDesc: '',
  albumCover: '',
  status: 1
})

const rules = {
  albumName: { required: true, message: '请输入相册名称', trigger: 'blur' },
  albumDesc: { required: true, message: '请输入相册描述', trigger: 'blur' }
}

const pagination = ref({
  current: 1,
  pageSize: 8,
  total: 0
})

function fetchAlbums() {
  loading.value = true
  getAlbumsApi({
    current: pagination.value.current,
    size: pagination.value.pageSize,
    keywords: keywords.value
  }).then(res => {
    // 处理数据，确保字段名一致
    albumList.value = (res.data.records || []).map(album => ({
      ...album,
      // 兼容不同的字段名
      name: album.albumName || album.name,
      description: album.albumDesc || album.description || '',
      cover: album.albumCover || album.cover || '',
      photoCount: album.photoCount || 0,
      createTime: album.createTime || album.createTime || ''
    }))
    pagination.value.total = res.data.count || 0
    loading.value = false
  }).catch(err => {
    console.error('获取相册列表失败:', err)
    message.error('获取相册列表失败')
    loading.value = false
  })
}

function handleSearch() {
  pagination.value.current = 1
  fetchAlbums()
}

function handleAddAlbum() {
  dialogTitle.value = '新建相册'
  Object.assign(albumForm.value, {
    id: null,
    albumName: '',
    albumDesc: '',
    albumCover: '',
    status: 1
  })
  showEditDialog.value = true
}

function handleViewPhotos(album) {
  // 跳转到照片列表页面
  router.push(`/albums/${album.id}`)
}

function handleEditAlbum(album) {
  dialogTitle.value = '编辑相册'
  Object.assign(albumForm.value, {
    id: album.id,
    albumName: album.name || album.albumName,
    albumDesc: album.description || album.albumDesc || '',
    albumCover: album.cover || album.albumCover || '',
    status: album.status || 1
  })
  showEditDialog.value = true
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()

    // 验证封面
    if (!albumForm.value.albumCover) {
      message.error('请上传相册封面')
      return
    }

    await saveAlbumApi(albumForm.value)
    message.success(albumForm.value.id ? '修改相册成功' : '新建相册成功')
    showEditDialog.value = false
    fetchAlbums()
  } catch (error) {
    if (error !== false) {
      console.error('保存相册失败:', error)
      message.error('保存相册失败')
    }
  }
}

function handleDeleteAlbum(album) {
  deleteAlbumApi(album.id).then(() => {
    message.success('删除相册成功')
    fetchAlbums()
  }).catch(err => {
    console.error('删除相册失败:', err)
    message.error('删除相册失败')
  })
}

// 上传封面
function handleUploadCover({ file }) {
  const formData = new FormData()
  formData.append('file', file.file)

  uploadAlbumCoverApi(formData).then(res => {
    albumForm.value.albumCover = res.data
    message.success('上传封面成功')
  }).catch(err => {
    console.error('上传封面失败:', err)
    message.error('上传封面失败')
  })

  return false // 阻止自动上传
}

function handlePageChange(page) {
  pagination.value.current = page
  fetchAlbums()
}

function handlePageSizeChange(pageSize) {
  pagination.value.pageSize = pageSize
  pagination.value.current = 1
  fetchAlbums()
}

function handleGoToDelete() {
  router.push('/albums/delete')
}

onMounted(() => {
  fetchAlbums()
})
</script>

<style scoped>
.album-card {
  height: 100%;
  cursor: pointer;
  transition: transform 0.2s;
}

.album-card:hover {
  transform: translateY(-2px);
}

.album-cover {
  width: 100%;
  height: 150px;
  margin-bottom: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.album-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.album-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 4px;
}

.placeholder-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.album-info {
  margin-bottom: 12px;
}

.album-name {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.album-desc {
  font-size: 12px;
  color: #999;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.album-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
}

.album-actions {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.cover-preview {
  margin-top: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
}

.cover-preview img {
  width: 100%;
  max-width: 360px;
  height: auto;
  display: block;
}
</style>
