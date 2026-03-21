<template>
  <n-space vertical size="large">
    <!-- 搜索栏 -->
    <n-form inline>
      <n-form-item label="关键词">
        <n-input v-model:value="keywords" placeholder="请输入操作描述" clearable style="width: 200px" @keyup.enter="handleSearch" />
      </n-form-item>
      <n-form-item>
        <n-button type="primary" @click="handleSearch">搜索</n-button>
      </n-form-item>
    </n-form>

    <!-- 操作栏 -->
    <n-space>
      <n-button type="error" :disabled="selectedIds.length === 0" @click="isDeleteDialog = true">
        <template #icon><n-icon><TrashOutline /></n-icon></template>
        批量删除
      </n-button>
    </n-space>

    <!-- 日志表格 -->
    <n-data-table
      v-model:checked-row-keys="selectedIds"
      :columns="columns"
      :data="logList"
      :loading="loading"
      :row-key="(row) => row.id"
    />

    <!-- 分页 -->
    <n-pagination
      v-model:page="pagination.page"
      :page-size="pagination.pageSize"
      :item-count="pagination.itemCount"
      :show-size-picker="true"
      :page-sizes="[10, 20]"
      @update:page="handlePageChange"
      @update:page-size="handlePageSizeChange"
      style="display: flex; justify-content: flex-end;"
    />
  </n-space>

  <!-- 查看详情对话框 -->
  <n-modal v-model:show="isCheckDialog" preset="card" title="详细信息" style="width: 80%; top: 20px">
    <n-descriptions v-if="currentLog" label-placement="left" :column="1">
      <n-descriptions-item label="请求接口">{{ currentLog.optUri }}</n-descriptions-item>
      <n-descriptions-item label="操作方法">{{ currentLog.optMethod }}</n-descriptions-item>
      <n-descriptions-item label="请求方式">
        <n-tag :type="getMethodTagType(currentLog.requestMethod)">
          {{ currentLog.requestMethod }}
        </n-tag>
      </n-descriptions-item>
      <n-descriptions-item label="请求参数">{{ currentLog.requestParam }}</n-descriptions-item>
      <n-descriptions-item label="异常信息">
        <pre style="background: #f5f5f5; padding: 12px; border-radius: 4px; overflow-x: auto; max-height: 400px; overflow-y: auto; margin: 0;">
          <code>{{ currentLog.exceptionInfo }}</code>
        </pre>
      </n-descriptions-item>
    </n-descriptions>
  </n-modal>

  <!-- 批量删除确认对话框 -->
  <n-modal v-model:show="isDeleteDialog" preset="dialog" title="提示">
    <span>是否删除选中项？</span>
    <template #action>
      <n-space>
        <n-button @click="isDeleteDialog = false">取消</n-button>
        <n-button type="primary" @click="handleBatchDelete">确定</n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NSpace, NTag, NPopconfirm, NDescriptions, NDescriptionsItem, NIcon, NPagination, useMessage, useDialog } from 'naive-ui'
import { TrashOutline } from '@vicons/ionicons5'
import dayjs from 'dayjs'
import { getExceptionLogsApi, deleteExceptionLogsApi } from '@/api/log'

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const logList = ref([])
const selectedIds = ref([])
const keywords = ref('')
const isCheckDialog = ref(false)
const isDeleteDialog = ref(false)
const currentLog = ref(null)

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20]
})

function getMethodTagType(method) {
  switch (method) {
    case 'GET':
      return 'default'
    case 'POST':
      return 'success'
    case 'PUT':
      return 'warning'
    case 'DELETE':
      return 'error'
    default:
      return 'default'
  }
}

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const columns = [
  { type: 'selection' },
  {
    title: '请求接口',
    key: 'optUri',
    width: 160,
    ellipsis: { tooltip: true }
  },
  {
    title: '操作描述',
    key: 'optDesc',
    width: 150,
    ellipsis: { tooltip: true }
  },
  {
    title: '请求方式',
    key: 'requestMethod',
    width: 100,
    align: 'center',
    render: (row) => {
      return h(NTag, {
        type: getMethodTagType(row.requestMethod)
      }, { default: () => row.requestMethod })
    }
  },
  {
    title: '登录IP',
    key: 'ipAddress',
    width: 130
  },
  {
    title: '登录地址',
    key: 'ipSource',
    width: 150,
    ellipsis: { tooltip: true }
  },
  {
    title: '操作日期',
    key: 'createTime',
    width: 180,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    render: (row) => {
      return h(NSpace, null, {
        default: () => [
          h(NButton, {
            size: 'small',
            onClick: () => handleCheck(row)
          }, { default: () => '查看' }),
          h(NPopconfirm, {
            onPositiveClick: () => handleDelete(row.id)
          }, {
            trigger: () => h(NButton, {
              size: 'small',
              type: 'error'
            }, { default: () => '删除' }),
            default: () => '确定删除吗？'
          })
        ]
      })
    }
  }
]

function fetchLogs() {
  loading.value = true
  const params = {
    current: pagination.page,
    size: pagination.pageSize
  }
  if (keywords.value) {
    params.keywords = keywords.value
  }

  getExceptionLogsApi(params).then(res => {
    logList.value = res.data.records || []
    // 兼容不同的总数字段名
    const total = res.data.count || res.data.total || 0
    pagination.itemCount = total
  }).catch(err => {
    console.error('获取异常日志失败:', err)
    message.error('获取异常日志失败')
  }).finally(() => {
    loading.value = false
  })
}

function handleSearch() {
  pagination.page = 1
  fetchLogs()
}

function handleCheck(row) {
  currentLog.value = row
  isCheckDialog.value = true
}

function handleDelete(id) {
  deleteExceptionLogsApi([id]).then(() => {
    message.success('删除成功')
    fetchLogs()
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handleBatchDelete() {
  if (selectedIds.value.length === 0) {
    message.warning('请选择要删除的日志')
    return
  }
  deleteExceptionLogsApi(selectedIds.value).then(() => {
    message.success('删除成功')
    selectedIds.value = []
    isDeleteDialog.value = false
    fetchLogs()
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handlePageChange(page) {
  pagination.page = page
  fetchLogs()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchLogs()
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
:deep(.n-code) {
  max-height: 200px;
  overflow-y: auto;
}
</style>
