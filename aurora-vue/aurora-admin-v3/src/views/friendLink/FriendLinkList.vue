<template>
  <AppPage>
    <n-card title="友链管理" rounded-10>
      <n-space vertical size="large">
        <!-- 搜索栏 -->
        <n-form inline>
          <n-form-item label="关键词">
            <n-input v-model:value="searchForm.keywords" placeholder="请输入友链名" clearable style="width: 200px" @keyup.enter="handleSearch" />
          </n-form-item>
          <n-form-item>
            <n-button type="primary" @click="handleSearch">搜索</n-button>
          </n-form-item>
        </n-form>

        <!-- 操作栏 -->
        <n-space>
          <n-button type="primary" @click="handleAdd">
            <template #icon><n-icon><AddOutline /></n-icon></template>
            新增
          </n-button>
          <n-button type="error" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
            <template #icon><n-icon><TrashOutline /></n-icon></template>
            批量删除
          </n-button>
        </n-space>

        <!-- 友链表格 -->
        <n-data-table
          v-model:checked-row-keys="selectedIds"
          :columns="columns"
          :data="linkList"
          :loading="loading"
          :row-key="(row) => row.id"
        />
        <n-pagination
          v-model:page="pagination.page"
          :page-size="pagination.pageSize"
          :item-count="pagination.itemCount"
          :show-size-picker="true"
          :page-sizes="[10, 20]"
          @update:page="handlePageChange"
          @update:page-size="handlePageSizeChange"
          style="display: flex; justify-content: flex-end; margin-top: 16px;"
        />
      </n-space>
    </n-card>

    <!-- 添加/编辑对话框 -->
    <n-modal v-model:show="showDialog" preset="dialog" :title="isEdit ? '编辑友链' : '新增友链'">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80px">
        <n-form-item label="链接名" path="linkName">
          <n-input v-model:value="form.linkName" placeholder="请输入链接名" />
        </n-form-item>
        <n-form-item label="链接头像" path="linkAvatar">
          <n-space vertical>
            <n-space>
              <n-input v-model:value="form.linkAvatar" placeholder="请输入链接头像URL或点击上传" style="width: 300px" />
              <n-upload
                action="/api/links/avatar"
                :headers="uploadHeaders"
                :show-file-list="false"
                @before-upload="handleBeforeUpload"
                @finish="handleUploadSuccess"
                @error="handleUploadError"
                accept="image/jpeg,image/jpg,image/png,image/gif"
              >
                <n-button type="primary" :loading="uploading" size="small">
                  {{ uploading ? '上传中...' : '上传' }}
                </n-button>
              </n-upload>
            </n-space>
            <div v-if="form.linkAvatar" style="display: flex; align-items: center; gap: 8px;">
              <img :src="form.linkAvatar" width="60" height="60" style="border-radius: 4px; object-fit: cover;" />
              <n-text depth="3">预览</n-text>
            </div>
          </n-space>
        </n-form-item>
        <n-form-item label="链接地址" path="linkAddress">
          <n-input v-model:value="form.linkAddress" placeholder="请输入链接地址" />
        </n-form-item>
        <n-form-item label="链接介绍" path="linkIntro">
          <n-input v-model:value="form.linkIntro" type="textarea" placeholder="请输入链接介绍" />
        </n-form-item>
        <n-form-item label="审核状态" path="isReview">
          <n-radio-group v-model:value="form.isReview">
            <n-radio :value="0">未审核</n-radio>
            <n-radio :value="1">审核通过</n-radio>
            <n-radio :value="2">审核未通过</n-radio>
          </n-radio-group>
        </n-form-item>
      </n-form>
      <template #action>
        <n-space>
          <n-button @click="showDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSubmit">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </AppPage>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NSpace, NTag, NAvatar, NPopconfirm, NText, NPagination, useMessage } from 'naive-ui'
import { AddOutline, TrashOutline } from '@vicons/ionicons5'
import { getAvatarPlaceholder } from '@/utils/placeholder'
import { getFriendLinksApi, saveFriendLinkApi, deleteFriendLinkApi } from '@/api/friendLink'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const message = useMessage()

const loading = ref(false)
const linkList = ref([])
const selectedIds = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const uploading = ref(false)
const uploadHeaders = {
  Authorization: 'Bearer ' + sessionStorage.getItem('token')
}

const searchForm = reactive({
  keywords: ''
})

const form = reactive({
  id: null,
  linkName: '',
  linkAvatar: '',
  linkAddress: '',
  linkIntro: '',
  isReview: 1
})

const rules = {
  linkName: { required: true, message: '请输入链接名', trigger: 'blur' },
  linkAddress: { required: true, message: '请输入链接地址', trigger: 'blur' }
}

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

const columns = [
  { type: 'selection' },
  {
    title: '链接头像',
    key: 'linkAvatar',
    width: 100,
    render: (row) => {
      return h(NAvatar, {
        size: 40,
        src: row.linkAvatar,
        fallbackSrc: getAvatarPlaceholder(row.linkName || '友链')
      })
    }
  },
  {
    title: '链接名',
    key: 'linkName',
    width: 160
  },
  {
    title: '链接地址',
    key: 'linkAddress',
    width: 200,
    ellipsis: { tooltip: true }
  },
  {
    title: '链接介绍',
    key: 'linkIntro',
    ellipsis: { tooltip: true }
  },
  {
    title: '审核状态',
    key: 'isReview',
    width: 120,
    render: (row) => {
      const statusMap = {
        0: { text: '未审核', type: 'warning' },
        1: { text: '审核通过', type: 'success' },
        2: { text: '审核未通过', type: 'error' }
      }
      const status = statusMap[row.isReview]
      return h(NTag, { type: status.type }, { default: () => status.text })
    }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 160,
    render: (row) => {
      return h(NSpace, null, {
        default: () => [
          h(NButton, {
            size: 'small',
            onClick: () => handleEdit(row)
          }, { default: () => '编辑' }),
          h(NPopconfirm, {
            onPositiveClick: () => handleDelete(row.id)
          }, {
            trigger: () => h(NButton, {
              size: 'small',
              type: 'error'
            }, { default: () => '删除' }),
            default: () => '确定删除该友链吗？'
          })
        ]
      })
    }
  }
]

function fetchLinks() {
  loading.value = true
  getFriendLinksApi({
    current: pagination.page,
    size: pagination.pageSize,
    keywords: searchForm.keywords
  }).then(res => {
    linkList.value = res.data.records || []
    // 兼容不同的总数字段名
    const total = res.data.count || res.data.total || 0
    pagination.itemCount = total
  }).catch(err => {
    console.error('获取友链列表失败:', err)
    message.error('获取友链列表失败')
  }).finally(() => {
    loading.value = false
  })
}

function handleSearch() {
  pagination.page = 1
  fetchLinks()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    linkName: '',
    linkAvatar: '',
    linkAddress: '',
    linkIntro: '',
    isReview: 1
  })
  showDialog.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  showDialog.value = true
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    await saveFriendLinkApi(form)
    message.success(isEdit.value ? '更新友链成功' : '新增友链成功')
    showDialog.value = false
    fetchLinks()
  } catch (error) {
    if (error !== false) {
      console.error('保存友链失败:', error)
      message.error('保存友链失败')
    }
  }
}

function handleDelete(id) {
  deleteFriendLinkApi([id]).then(() => {
    message.success('删除成功')
    fetchLinks()
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handleBatchDelete() {
  deleteFriendLinkApi(selectedIds.value).then(() => {
    message.success('批量删除成功')
    selectedIds.value = []
    fetchLinks()
  }).catch(err => {
    console.error('批量删除失败:', err)
    message.error('批量删除失败')
  })
}

function handlePageChange(page) {
  pagination.page = page
  fetchLinks()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchLinks()
}

function handleBeforeUpload(data) {
  const file = data.file.file
  const isImage = /^image\/(jpeg|jpg|png|gif)$/.test(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    message.warning('只能上传 JPG/PNG/GIF 格式的图片')
    return false
  }
  if (!isLt2M) {
    message.warning('图片大小不能超过 2MB')
    return false
  }
  uploading.value = true
  return true
}

function handleUploadSuccess({ file }) {
  uploading.value = false
  if (file.response && file.response.flag) {
    form.linkAvatar = file.response.data
    message.success('图片上传成功')
  } else {
    message.error(file.response?.message || '图片上传失败')
  }
}

function handleUploadError() {
  uploading.value = false
  message.error('图片上传失败，请重试')
}

onMounted(() => {
  fetchLinks()
})
</script>