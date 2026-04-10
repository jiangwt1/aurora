<template>
  <div class="article-list-container">
    <n-card title="文章管理">
      <!-- 状态筛选 -->
      <div class="status-filter mb-4">
        <span class="filter-label">状态：</span>
        <n-radio-group v-model:value="status" @update:value="handleStatusChange">
          <n-radio-button value="">全部</n-radio-button>
          <n-radio-button value="1">公开</n-radio-button>
          <n-radio-button value="2">私密</n-radio-button>
          <n-radio-button value="3">草稿箱</n-radio-button>
        </n-radio-group>
      </div>

      <!-- 操作栏 -->
      <div class="operation-bar mb-4 flex items-center justify-between">
        <div class="left-actions flex items-center gap-2">
          <n-button
            type="error"
            :disabled="selectedArticleIds.length === 0"
            @click="handleBatchDelete"
          >
            批量删除
          </n-button>
          <n-button
            type="success"
            :disabled="selectedArticleIds.length === 0"
            @click="handleExport"
          >
            批量导出
          </n-button>
          <n-upload
            action="/api/admin/articles/import"
            :show-file-list="false"
            accept=".md"
            :max="9"
            multiple
            :headers="uploadHeaders"
            @finish="handleUploadFinish"
          >
            <n-button type="primary">批量导入</n-button>
          </n-upload>
        </div>

        <div class="right-actions flex items-center gap-2">
          <n-select
            v-model:value="type"
            placeholder="文章类型"
            clearable
            style="width: 150px"
            :options="typeOptions"
          />
          <n-select
            v-model:value="categoryId"
            placeholder="请选择分类"
            clearable
            filterable
            style="width: 150px"
            :options="categoryOptions"
          />
          <n-select
            v-model:value="tagId"
            placeholder="请选择标签"
            clearable
            filterable
            style="width: 150px"
            :options="tagOptions"
          />
          <n-input
            v-model:value="keywords"
            placeholder="请输入文章名"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
          <n-button type="primary" @click="handleSearch">搜索</n-button>
        </div>
      </div>

      <!-- 文章表格 -->
      <n-data-table
        v-model:checked-row-keys="selectedArticleIds"
        :columns="columns"
        :data="articles"
        :loading="loading"
        :row-key="row => row.id"
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
    </n-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NTag, NImage, NSpace, NPopconfirm, NSwitch, NPagination, useMessage, useDialog } from 'naive-ui'
import { getArticlesApi, deleteArticleApi, exportArticlesApi, updateArticleTopAndFeaturedApi } from '@/api/article'
import { searchCategoriesApi } from '@/api/category'
import { searchTagsApi } from '@/api/tag'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

// 数据
const articles = ref([])
const loading = ref(false)
const selectedArticleIds = ref([])
const status = ref('')
const type = ref(null)
const categoryId = ref(null)
const tagId = ref(null)
const keywords = ref('')

// 分类和标签选项
const categoryOptions = ref([])
const tagOptions = ref([])
const typeOptions = [
  { label: '原创', value: 1 },
  { label: '转载', value: 2 },
  { label: '翻译', value: 3 }
]

// 上传请求头
const uploadHeaders = {
  Authorization: 'Bearer ' + sessionStorage.getItem('token')
}

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

// 表格列
const columns = [
  { type: 'selection' },
  {
    title: '文章封面',
    key: 'articleCover',
    width: 120,
    render: (row) => {
      return row.articleCover
        ? h(NImage, {
            width: 80,
            height: 50,
            src: row.articleCover,
            objectFit: 'cover',
            style: { borderRadius: '4px' }
          })
        : h('div', { class: 'no-cover' }, '无')
    }
  },
  {
    title: '文章标题',
    key: 'articleTitle',
    width: 200,
    ellipsis: { tooltip: true }
  },
  {
    title: '分类',
    key: 'categoryName',
    width: 100,
    render: (row) => row.categoryName || h('span', { class: 'text-gray-400' }, '无')
  },
  {
    title: '标签',
    key: 'tagDTOs',
    width: 150,
    render: (row) => {
      const tagDTOs = row.tagDTOs || []
      if (!tagDTOs || tagDTOs.length === 0) {
        return h('span', { class: 'text-gray-400' }, '无')
      }
      return h(NSpace, { size: 'small' }, {
        default: () => tagDTOs.map(tag =>
          h(NTag, { size: 'small', type: 'info' }, { default: () => tag.tagName })
        )
      })
    }
  },
  {
    title: '浏览量',
    key: 'viewsCount',
    width: 80,
    render: (row) => row.viewsCount || 0
  },
  {
    title: '类型',
    key: 'type',
    width: 80,
    render: (row) => {
      const typeMap = {
        1: { text: '原创', type: 'success' },
        2: { text: '转载', type: 'warning' },
        3: { text: '翻译', type: 'info' }
      }
      const typeInfo = typeMap[row.type] || { text: '未知', type: 'default' }
      return h(NTag, { size: 'small', type: typeInfo.type }, { default: () => typeInfo.text })
    }
  },
  {
    title: '发表时间',
    key: 'createTime',
    width: 160,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '置顶',
    key: 'isTop',
    width: 80,
    render: (row) => {
      return h(NSwitch, {
        value: row.isTop === 1,
        size: 'small',
        onUpdateValue: (value) => handleToggleTop(row, value)
      })
    }
  },
  {
    title: '推荐',
    key: 'isFeatured',
    width: 80,
    render: (row) => {
      return h(NSwitch, {
        value: row.isFeatured === 1,
        size: 'small',
        onUpdateValue: (value) => handleToggleFeatured(row, value)
      })
    }
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    fixed: 'right',
    render: (row) => {
      return h(NSpace, {}, {
        default: () => [
          h(NButton, {
            size: 'small',
            type: 'primary',
            onClick: () => handleEdit(row)
          }, { default: () => '编辑' }),
          h(NPopconfirm, {
            onPositiveClick: () => handleDelete(row.id)
          }, {
            default: () => '确定删除这篇文章吗？',
            trigger: () => h(NButton, {
              size: 'small',
              type: 'error'
            }, { default: () => '删除' })
          })
        ]
      })
    }
  }
]

// 获取文章列表
const fetchArticles = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.page,
      size: pagination.pageSize,
      status: status.value,
      type: type.value,
      categoryId: categoryId.value,
      tagId: tagId.value,
      keywords: keywords.value,
      isDelete: 0
    }

    const res = await getArticlesApi(params)
    articles.value = res.data.records || []
    pagination.itemCount = res.data.count || res.data.total || 0
  } catch (error) {
    console.error('获取文章列表失败:', error)
    message.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res = await searchCategoriesApi({ current: 1, size: 1000 })
    categoryOptions.value = (res.data.records || []).map(item => ({
      label: item.categoryName,
      value: item.id
    }))
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取标签列表
const fetchTags = async () => {
  try {
    const res = await searchTagsApi({ current: 1, size: 1000 })
    tagOptions.value = (res.data.records || []).map(item => ({
      label: item.tagName,
      value: item.id
    }))
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

// 状态变化
const handleStatusChange = () => {
  pagination.page = 1
  fetchArticles()
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchArticles()
}

// 分页变化
const handlePageChange = (page) => {
  pagination.page = page
  fetchArticles()
}

// 分页大小变化
const handlePageSizeChange = (pageSize) => {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchArticles()
}

// 批量删除
const handleBatchDelete = () => {
  dialog.warning({
    title: '批量删除',
    content: `确定删除选中的 ${selectedArticleIds.value.length} 篇文章吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        await deleteArticleApi(selectedArticleIds.value)
        message.success('删除成功')
        selectedArticleIds.value = []
        fetchArticles()
      } catch (error) {
        message.error('删除失败')
      }
    }
  })
}

// 删除文章
const handleDelete = async (id) => {
  try {
    await deleteArticleApi([id])
    message.success('删除成功')
    fetchArticles()
  } catch (error) {
    message.error('删除失败')
  }
}

// 导出文章
const handleExport = async () => {
  try {
    const res = await exportArticlesApi(selectedArticleIds.value)
    if (res.flag && res.data) {
      message.success('导出成功')
      // 逐个下载文件
      res.data.forEach((url) => {
        downloadFile(url)
      })
    } else {
      message.error(res.message || '导出失败')
    }
  } catch (error) {
    message.error('导出失败')
  }
}

// 下载文件
const downloadFile = (url) => {
  const iframe = document.createElement('iframe')
  iframe.style.display = 'none'
  iframe.style.height = 0
  iframe.src = url
  document.body.appendChild(iframe)
  setTimeout(() => {
    iframe.remove()
  }, 5 * 60 * 1000)
}

// 上传文章完成
const handleUploadFinish = ({ file, event }) => {
  try {
    const response = JSON.parse(event.target.response)
    if (response.flag) {
      message.success('导入成功')
      // 延迟刷新列表，确保后端处理完成
      setTimeout(() => {
        fetchArticles()
      }, 500)
    } else {
      message.error(response.message || '导入失败')
    }
  } catch (error) {
    message.error('导入失败')
  }
}

// 编辑文章
const handleEdit = (row) => {
  router.push(`/articles/${row.id}`)
}

// 切换置顶
const handleToggleTop = async (row, value) => {
  try {
    await updateArticleTopAndFeaturedApi({
      id: row.id,
      isTop: value ? 1 : 0,
      isFeatured: row.isFeatured
    })
    row.isTop = value ? 1 : 0
    message.success(value ? '置顶成功' : '取消置顶成功')
  } catch (error) {
    message.error('操作失败')
  }
}

// 切换推荐
const handleToggleFeatured = async (row, value) => {
  try {
    await updateArticleTopAndFeaturedApi({
      id: row.id,
      isTop: row.isTop,
      isFeatured: value ? 1 : 0
    })
    row.isFeatured = value ? 1 : 0
    message.success(value ? '推荐成功' : '取消推荐成功')
  } catch (error) {
    message.error('操作失败')
  }
}

onMounted(() => {
  fetchArticles()
  fetchCategories()
  fetchTags()
})
</script>

<style scoped>
.article-list-container {
  padding: 20px;
}

.status-filter {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-weight: 500;
  color: #333;
}

.no-cover {
  color: #999;
  font-size: 12px;
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 4px;
  text-align: center;
  min-width: 40px;
}

.text-gray-400 {
  color: #9ca3af;
}
</style>
