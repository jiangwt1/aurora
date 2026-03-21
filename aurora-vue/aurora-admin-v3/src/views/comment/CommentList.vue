<template>
  <AppPage>
    <n-card title="评论管理" rounded-10>
      <n-space vertical size="large">
        <!-- 状态筛选标签 -->
        <n-space>
          <n-button :type="searchForm.isReview === null ? 'primary' : 'default'" size="small" @click="changeReview(null)">
            全部
          </n-button>
          <n-button :type="searchForm.isReview === 1 ? 'primary' : 'default'" size="small" @click="changeReview(1)">
            正常
          </n-button>
          <n-button :type="searchForm.isReview === 0 ? 'primary' : 'default'" size="small" @click="changeReview(0)">
            审核中
          </n-button>
        </n-space>

        <!-- 操作栏 -->
        <n-space justify="space-between">
          <n-space>
            <n-button type="success" :disabled="!selectedIds.length" @click="handleBatchApprove">
              <template #icon><n-icon><CheckmarkOutline /></n-icon></template>
              批量通过 ({{ selectedIds.length }})
            </n-button>
            <n-popconfirm @positive-click="handleBatchDelete" :disabled="!selectedIds.length">
              <template #trigger>
                <n-button type="error" :disabled="!selectedIds.length">
                  <template #icon><n-icon><TrashOutline /></n-icon></template>
                  批量删除 ({{ selectedIds.length }})
                </n-button>
              </template>
              确定删除选中的评论吗？
            </n-popconfirm>
          </n-space>

          <!-- 搜索栏 -->
          <n-space>
            <n-select
              v-model:value="searchForm.type"
              :options="typeOptions"
              placeholder="请选择来源"
              clearable
              style="width: 120px"
            />
            <n-input
              v-model:value="searchForm.keywords"
              placeholder="请输入用户昵称"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
            <n-button type="primary" @click="handleSearch">搜索</n-button>
          </n-space>
        </n-space>

        <!-- 表格 -->
        <n-data-table
          v-model:checked-row-keys="selectedIds"
          :columns="columns"
          :data="commentList"
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
  </AppPage>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NSpace, NTag, NPopconfirm, NAvatar, NPagination, useMessage } from 'naive-ui'
import { CheckmarkOutline, TrashOutline } from '@vicons/ionicons5'
import { getCommentsApi, reviewCommentsApi, deleteCommentsApi } from '@/api/comment'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const message = useMessage()

const loading = ref(false)
const commentList = ref([])
const selectedIds = ref([])

const searchForm = reactive({
  isReview: null,
  keywords: '',
  type: null
})

const statusOptions = [
  { label: '全部', value: null },
  { label: '审核中', value: 0 },
  { label: '正常', value: 1 }
]

const typeOptions = [
  { label: '全部', value: null },
  { label: '文章', value: 1 },
  { label: '留言', value: 2 },
  { label: '关于我', value: 3 },
  { label: '友链', value: 4 },
  { label: '文档', value: 5 }
]

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

const columns = [
  {
    type: 'selection'
  },
  {
    title: '头像',
    key: 'avatar',
    width: 80,
    render: (row) => {
      return h(NAvatar, {
        size: 'small',
        src: row.avatar,
        fallbackSrc: 'https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg'
      })
    }
  },
  {
    title: '评论人',
    key: 'nickname',
    width: 120
  },
  {
    title: '回复对象',
    key: 'replyNickname',
    width: 120,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '文章标题',
    key: 'articleTitle',
    width: 150,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '内容',
    key: 'commentContent',
    width: 200,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '状态',
    key: 'isReview',
    width: 100,
    render: (row) => {
      const statusMap = {
        0: { text: '待审核', type: 'warning' },
        1: { text: '通过', type: 'success' },
        2: { text: '拒绝', type: 'error' }
      }
      const status = statusMap[row.isReview] || { text: '未知', type: 'default' }
      return h(NTag, { type: status.type }, { default: () => status.text })
    }
  },
  {
    title: '来源',
    key: 'type',
    width: 100,
    render: (row) => {
      const typeMap = {
        1: { text: '文章', type: 'default' },
        2: { text: '留言', type: 'error' },
        3: { text: '关于我', type: 'success' },
        4: { text: '友链', type: 'warning' },
        5: { text: '文档', type: 'warning' }
      }
      const type = typeMap[row.type] || { text: '未知', type: 'default' }
      return h(NTag, { type: type.type }, { default: () => type.text })
    }
  },
  {
    title: '评论时间',
    key: 'createTime',
    width: 180,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    render: (row) => {
      if (row.isReview === 0) {
        return h(NSpace, null, {
          default: () => [
            h(NButton, {
              size: 'small',
              type: 'success',
              onClick: () => handleApprove(row.id)
            }, { default: () => '通过' }),
            h(NPopconfirm, {
              onPositiveClick: () => handleDelete(row.id)
            }, {
              trigger: () => h(NButton, {
                size: 'small',
                type: 'error'
              }, { default: () => '删除' }),
              default: () => '确定删除该评论吗？'
            })
          ]
        })
      } else {
        return h(NPopconfirm, {
          onPositiveClick: () => handleDelete(row.id)
        }, {
          trigger: () => h(NButton, {
            size: 'small',
            type: 'error'
          }, { default: () => '删除' }),
          default: () => '确定删除该评论吗？'
        })
      }
    }
  }
]

function fetchComments() {
  loading.value = true
  getCommentsApi({
    current: pagination.page,
    size: pagination.pageSize,
    isReview: searchForm.isReview,
    keywords: searchForm.keywords,
    type: searchForm.type
  }).then(res => {
    commentList.value = res.data.records || []
    pagination.itemCount = res.data.count || 0
  }).catch(err => {
    console.error('获取评论列表失败:', err)
    message.error('获取评论列表失败')
  }).finally(() => {
    loading.value = false
  })
}

function changeReview(value) {
  searchForm.isReview = value
  pagination.page = 1
  fetchComments()
}

function handleApprove(id) {
  reviewCommentsApi({ ids: [id], isReview: 1 }).then(() => {
    message.success('审核通过')
    fetchComments()
  }).catch(err => {
    console.error('审核失败:', err)
    message.error('审核失败')
  })
}

function handleBatchApprove() {
  reviewCommentsApi({ ids: selectedIds.value, isReview: 1 }).then(() => {
    message.success('批量审核通过')
    selectedIds.value = []
    fetchComments()
  }).catch(err => {
    console.error('批量审核失败:', err)
    message.error('批量审核失败')
  })
}

function handleDelete(id) {
  deleteCommentsApi([id]).then(() => {
    message.success('删除成功')
    fetchComments()
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handleBatchDelete() {
  deleteCommentsApi(selectedIds.value).then(() => {
    message.success('批量删除成功')
    selectedIds.value = []
    fetchComments()
  }).catch(err => {
    console.error('批量删除失败:', err)
    message.error('批量删除失败')
  })
}

function handleSearch() {
  pagination.page = 1
  fetchComments()
}

function handlePageChange(page) {
  pagination.page = page
  fetchComments()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchComments()
}

onMounted(() => {
  fetchComments()
})
</script>
