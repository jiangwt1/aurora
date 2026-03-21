<template>
  <AppPage>
    <n-card title="定时任务管理" rounded-10>
      <n-space vertical size="large">
        <!-- 搜索栏 -->
        <n-form inline>
          <n-form-item label="任务名称">
            <n-input v-model:value="searchForm.jobName" placeholder="请输入任务名称" clearable />
          </n-form-item>
          <n-form-item label="任务组名">
            <n-select v-model:value="searchForm.jobGroup" :options="jobGroups" placeholder="请选择任务组名" clearable style="width: 150px" />
          </n-form-item>
          <n-form-item label="任务状态">
            <n-select v-model:value="searchForm.status" :options="statusOptions" placeholder="请选择任务状态" clearable style="width: 120px" />
          </n-form-item>
          <n-form-item>
            <n-button type="primary" @click="handleSearch">搜索</n-button>
            <n-button @click="handleReset" style="margin-left: 8px">重置</n-button>
          </n-form-item>
        </n-form>

        <!-- 操作栏 -->
        <n-space>
          <n-button type="primary" @click="handleAdd">
            <template #icon><n-icon><AddOutline /></n-icon></template>
            新增
          </n-button>
          <n-button @click="handleOpenLog">
            <template #icon><n-icon><DocumentTextOutline /></n-icon></template>
            日志
          </n-button>
          <n-button type="error" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
            <template #icon><n-icon><TrashOutline /></n-icon></template>
            批量删除
          </n-button>
        </n-space>

        <!-- 任务表格 -->
        <n-data-table
          v-model:checked-row-keys="selectedIds"
          :columns="columns"
          :data="jobList"
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

    <!-- 添加/编辑任务对话框 -->
    <n-modal v-model:show="showJobDialog" preset="dialog" :title="isEdit ? '编辑任务' : '新增任务'" style="width: 800px">
      <n-form ref="jobFormRef" :model="jobForm" :rules="jobRules" label-placement="left" label-width="120px">
        <n-grid :cols="2" :x-gap="20">
          <n-gi>
            <n-form-item label="任务名称" path="jobName">
              <n-input v-model:value="jobForm.jobName" placeholder="请输入任务名称" />
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="任务组名" path="jobGroup">
              <n-input v-model:value="jobForm.jobGroup" placeholder="请输入任务组名" />
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-form-item label="调用目标" path="invokeTarget">
          <n-input v-model:value="jobForm.invokeTarget" placeholder="请输入调用目标字符串，例如：com.example.JobTask.execute" />
        </n-form-item>
        <n-form-item label="cron表达式" path="cronExpression">
          <n-input v-model:value="jobForm.cronExpression" placeholder="请输入cron执行表达式">
            <template #suffix>
              <n-text depth="3">例如：0 0/5 * * * ?</n-text>
            </template>
          </n-input>
        </n-form-item>
        <n-form-item label="错误策略" path="misfirePolicy">
          <n-radio-group v-model:value="jobForm.misfirePolicy">
            <n-radio :value="0">默认策略</n-radio>
            <n-radio :value="1">立即执行</n-radio>
            <n-radio :value="2">执行一次</n-radio>
            <n-radio :value="3">放弃执行</n-radio>
          </n-radio-group>
        </n-form-item>
        <n-grid :cols="2" :x-gap="20">
          <n-gi>
            <n-form-item label="是否并发" path="concurrent">
              <n-radio-group v-model:value="jobForm.concurrent">
                <n-radio :value="0">允许</n-radio>
                <n-radio :value="1">禁止</n-radio>
              </n-radio-group>
            </n-form-item>
          </n-gi>
          <n-gi>
            <n-form-item label="状态" path="status">
              <n-radio-group v-model:value="jobForm.status">
                <n-radio :value="1">正常</n-radio>
                <n-radio :value="0">暂停</n-radio>
              </n-radio-group>
            </n-form-item>
          </n-gi>
        </n-grid>
        <n-form-item label="备注" path="remark">
          <n-input v-model:value="jobForm.remark" type="textarea" placeholder="请输入备注" />
        </n-form-item>
      </n-form>
      <template #action>
        <n-space>
          <n-button @click="showJobDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSaveJob">确定</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 任务详细对话框 -->
    <n-modal v-model:show="showViewDialog" preset="card" title="任务详细" style="width: 700px">
      <n-descriptions v-if="currentJob" label-placement="left" :column="2">
        <n-descriptions-item label="任务编号">{{ currentJob.id }}</n-descriptions-item>
        <n-descriptions-item label="任务名称">{{ currentJob.jobName }}</n-descriptions-item>
        <n-descriptions-item label="任务分组">{{ currentJob.jobGroup }}</n-descriptions-item>
        <n-descriptions-item label="创建时间">{{ formatDateTime(currentJob.createTime) }}</n-descriptions-item>
        <n-descriptions-item label="cron表达式">{{ currentJob.cronExpression }}</n-descriptions-item>
        <n-descriptions-item label="下次执行时间">{{ formatDateTime(currentJob.nextValidTime) }}</n-descriptions-item>
        <n-descriptions-item label="调用目标方法" :span="2">{{ currentJob.invokeTarget }}</n-descriptions-item>
        <n-descriptions-item label="任务状态">
          <n-tag :type="currentJob.status === 1 ? 'success' : 'default'">
            {{ currentJob.status === 1 ? '正常' : '暂停' }}
          </n-tag>
        </n-descriptions-item>
        <n-descriptions-item label="是否并发">
          <n-tag :type="currentJob.concurrent === 0 ? 'info' : 'warning'">
            {{ currentJob.concurrent === 0 ? '允许' : '禁止' }}
          </n-tag>
        </n-descriptions-item>
        <n-descriptions-item label="执行策略">
          <span v-if="currentJob.misfirePolicy === 0">默认策略</span>
          <span v-else-if="currentJob.misfirePolicy === 1">立即执行</span>
          <span v-else-if="currentJob.misfirePolicy === 2">执行一次</span>
          <span v-else-if="currentJob.misfirePolicy === 3">放弃执行</span>
          <span v-else>未知</span>
        </n-descriptions-item>
        <n-descriptions-item label="备注" :span="2">{{ currentJob.remark || '无' }}</n-descriptions-item>
      </n-descriptions>
    </n-modal>

    <!-- 任务日志对话框 -->
    <n-modal v-model:show="showLogDialog" preset="card" title="任务日志" style="width: 1200px">
      <n-space vertical size="large">
        <!-- 搜索栏 -->
        <n-form inline>
          <n-form-item label="任务名称">
            <n-input v-model:value="logSearchForm.jobName" placeholder="请输入任务名称" clearable style="width: 200px" />
          </n-form-item>
          <n-form-item label="任务组名">
            <n-select v-model:value="logSearchForm.jobGroup" :options="jobGroups" placeholder="请选择组名" clearable style="width: 150px" />
          </n-form-item>
          <n-form-item label="执行状态">
            <n-select v-model:value="logSearchForm.status" :options="logStatusOptions" placeholder="请选择任务状态" clearable style="width: 120px" />
          </n-form-item>
          <n-form-item>
            <n-button type="primary" @click="handleSearchLogs">搜索</n-button>
            <n-button @click="handleResetLogs" style="margin-left: 8px">重置</n-button>
          </n-form-item>
        </n-form>

        <!-- 操作栏 -->
        <n-space>
          <n-button type="error" :disabled="selectedLogIds.length === 0" @click="handleBatchDeleteLogs">
            <template #icon><n-icon><TrashOutline /></n-icon></template>
            批量删除
          </n-button>
          <n-button type="error" @click="handleCleanLogs">
            <template #icon><n-icon><TrashOutline /></n-icon></template>
            清空
          </n-button>
        </n-space>

        <!-- 日志表格 -->
        <n-data-table
          v-model:checked-row-keys="selectedLogIds"
          :columns="logColumns"
          :data="logList"
          :loading="logLoading"
          :row-key="(row) => row.id"
        />
        <n-pagination
          v-model:page="logPagination.page"
          :page-size="logPagination.pageSize"
          :item-count="logPagination.itemCount"
          :show-size-picker="true"
          :page-sizes="[10, 20]"
          @update:page="handleLogPageChange"
          @update:page-size="handleLogPageSizeChange"
          style="display: flex; justify-content: flex-end; margin-top: 16px;"
        />
      </n-space>
    </n-modal>

    <!-- 日志详细对话框 -->
    <n-modal v-model:show="showLogDetailDialog" preset="card" :title="'调度日志详细'" :style="{ width: currentLog?.status === 1 ? '700px' : '80%' }">
      <n-descriptions v-if="currentLog" label-placement="left" :column="2">
        <n-descriptions-item label="日志序号">{{ currentLog.id }}</n-descriptions-item>
        <n-descriptions-item label="任务名称">{{ currentLog.jobName }}</n-descriptions-item>
        <n-descriptions-item label="任务分组">{{ currentLog.jobGroup }}</n-descriptions-item>
        <n-descriptions-item label="执行时间">{{ formatDateTime(currentLog.startTime) }}</n-descriptions-item>
        <n-descriptions-item label="调用方法" :span="2">{{ currentLog.invokeTarget }}</n-descriptions-item>
        <n-descriptions-item label="日志信息" :span="2">{{ currentLog.jobMessage }}</n-descriptions-item>
        <n-descriptions-item label="执行状态">
          <n-tag :type="currentLog.status === 1 ? 'success' : 'error'">
            {{ currentLog.status === 1 ? '成功' : '失败' }}
          </n-tag>
        </n-descriptions-item>
      </n-descriptions>
      <div v-if="currentLog && currentLog.status === 0" style="margin-top: 16px">
        <pre style="background: #f5f5f5; padding: 12px; border-radius: 4px; overflow-x: auto; max-height: 400px; overflow-y: auto;">
          <code>{{ currentLog.exceptionInfo || '' }}</code>
        </pre>
      </div>
    </n-modal>
  </AppPage>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import dayjs from 'dayjs'
import { NButton, NSpace, NSwitch, NTag, NPopconfirm, NDropdown, NDescriptions, NDescriptionsItem, NGrid, NGi, NText, NPagination, useMessage } from 'naive-ui'
import { AddOutline, DocumentTextOutline, TrashOutline, PlayOutline, EyeOutline } from '@vicons/ionicons5'
import { getJobsApi, getJobGroupsApi, saveJobApi, updateJobApi, deleteJobApi, changeJobStatusApi, runJobApi, getJobLogsApi, deleteJobLogsApi, cleanJobLogsApi } from '@/api/quartz'

const message = useMessage()

// 日期格式化函数
function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const loading = ref(false)
const jobList = ref([])
const selectedIds = ref([])
const jobGroups = ref([])

const searchForm = reactive({
  jobName: '',
  jobGroup: null,
  status: null
})

const statusOptions = [
  { label: '正常', value: 1 },
  { label: '暂停', value: 0 }
]

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
    title: '任务名称',
    key: 'jobName',
    width: 160,
    ellipsis: { tooltip: true }
  },
  {
    title: '任务组名',
    key: 'jobGroup',
    width: 120,
    render: (row) => {
      return h(NTag, { type: 'info' }, { default: () => row.jobGroup })
    }
  },
  {
    title: '调用目标',
    key: 'invokeTarget',
    width: 200,
    ellipsis: { tooltip: true }
  },
  {
    title: 'cron表达式',
    key: 'cronExpression',
    width: 150
  },
  {
    title: '状态',
    key: 'status',
    width: 80,
    render: (row) => {
      return h(NSwitch, {
        value: row.status === 1,
        onUpdateValue: (val) => handleChangeStatus(row, val)
      })
    }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 160,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '备注',
    key: 'remark',
    width: 200,
    ellipsis: { tooltip: true }
  },
  {
    title: '操作',
    key: 'actions',
    width: 200,
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
            default: () => '确定删除该任务吗？'
          }),
          h(NDropdown, {
            options: [
              { label: '执行一次', key: 'run', icon: () => h(PlayOutline) },
              { label: '任务详细', key: 'view', icon: () => h(EyeOutline) }
            ],
            onSelect: (key) => handleCommand(key, row)
          }, {
            default: () => h(NButton, { size: 'small' }, { default: () => '更多' })
          })
        ]
      })
    }
  }
]

// 任务表单
const showJobDialog = ref(false)
const showViewDialog = ref(false)
const isEdit = ref(false)
const currentJob = ref(null)
const jobFormRef = ref(null)
const jobForm = reactive({
  id: null,
  jobName: '',
  jobGroup: '',
  invokeTarget: '',
  cronExpression: '',
  misfirePolicy: 0,
  concurrent: 0,
  status: 1,
  remark: ''
})

const jobRules = {
  jobName: { required: true, message: '请输入任务名称', trigger: 'blur' },
  jobGroup: { required: true, message: '请输入任务组名', trigger: 'blur' },
  invokeTarget: { required: true, message: '请输入调用目标', trigger: 'blur' },
  cronExpression: { required: true, message: '请输入cron表达式', trigger: 'blur' }
}

// 日志相关
const showLogDialog = ref(false)
const showLogDetailDialog = ref(false)
const logLoading = ref(false)
const logList = ref([])
const selectedLogIds = ref([])
const currentLog = ref(null)

const logSearchForm = reactive({
  jobName: '',
  jobGroup: null,
  status: null
})

const logStatusOptions = [
  { label: '成功', value: 1 },
  { label: '失败', value: 0 }
]

const logPagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20]
})

const logColumns = [
  {
    title: '日志编号',
    key: 'index',
    width: 80,
    align: 'center',
    render: (row, index) => index + 1
  },
  {
    title: '任务名称',
    key: 'jobName',
    width: 150,
    ellipsis: { tooltip: true }
  },
  {
    title: '任务组名',
    key: 'jobGroup',
    width: 120,
    render: (row) => {
      return h(NTag, { type: 'info' }, { default: () => row.jobGroup })
    }
  },
  {
    title: '调用目标',
    key: 'invokeTarget',
    ellipsis: { tooltip: true }
  },
  {
    title: '日志信息',
    key: 'jobMessage',
    ellipsis: { tooltip: true }
  },
  {
    title: '执行状态',
    key: 'status',
    width: 100,
    align: 'center',
    render: (row) => {
      return h(NTag, {
        type: row.status === 1 ? 'success' : 'error'
      }, { default: () => row.status === 1 ? '成功' : '失败' })
    }
  },
  {
    title: '执行时间',
    key: 'startTime',
    width: 180,
    render: (row) => formatDateTime(row.startTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 80,
    align: 'center',
    render: (row) => {
      return h(NButton, {
        size: 'small',
        onClick: () => handleViewLogDetail(row)
      }, { default: () => '详细' })
    }
  }
]

function fetchJobs() {
  loading.value = true
  getJobsApi({
    current: pagination.page,
    size: pagination.pageSize,
    jobName: searchForm.jobName,
    jobGroup: searchForm.jobGroup,
    status: searchForm.status
  }).then(res => {
    jobList.value = res.data.records || []
    pagination.itemCount = res.data.total || 0
    loading.value = false
  }).catch(err => {
    console.error('获取任务列表失败:', err)
    message.error('获取任务列表失败')
    loading.value = false
  })
}

function fetchJobGroups() {
  getJobGroupsApi().then(res => {
    jobGroups.value = (res.data || []).map(item => ({
      label: item,
      value: item
    }))
  }).catch(err => {
    console.error('获取任务组失败:', err)
  })
}

function handleSearch() {
  pagination.page = 1
  fetchJobs()
}

function handleReset() {
  Object.assign(searchForm, {
    jobName: '',
    jobGroup: null,
    status: null
  })
  handleSearch()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(jobForm, {
    id: null,
    jobName: '',
    jobGroup: 'DEFAULT',
    invokeTarget: '',
    cronExpression: '',
    misfirePolicy: 0,
    concurrent: 0,
    status: 1,
    remark: ''
  })
  showJobDialog.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(jobForm, row)
  showJobDialog.value = true
}

async function handleSaveJob() {
  try {
    await jobFormRef.value?.validate()
    if (isEdit.value) {
      await updateJobApi(jobForm)
      message.success('更新任务成功')
    } else {
      await saveJobApi(jobForm)
      message.success('新增任务成功')
    }
    showJobDialog.value = false
    fetchJobs()
  } catch (error) {
    if (error !== false) { // 不是验证错误
      console.error('保存任务失败:', error)
      message.error('保存任务失败')
    }
  }
}

function handleDelete(id) {
  deleteJobApi([id]).then(() => {
    message.success('删除成功')
    fetchJobs()
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handleBatchDelete() {
  deleteJobApi(selectedIds.value).then(() => {
    message.success('批量删除成功')
    selectedIds.value = []
    fetchJobs()
  }).catch(err => {
    console.error('批量删除失败:', err)
    message.error('批量删除失败')
  })
}

function handleChangeStatus(row, value) {
  changeJobStatusApi({
    id: row.id,
    status: value ? 1 : 0
  }).then(() => {
    message.success('状态修改成功')
    fetchJobs()
  }).catch(err => {
    console.error('修改状态失败:', err)
    message.error('修改状态失败')
  })
}

function handleCommand(command, row) {
  switch (command) {
    case 'run':
      handleRunJob(row)
      break
    case 'view':
      handleViewJob(row)
      break
  }
}

function handleRunJob(row) {
  runJobApi({
    id: row.id,
    jobGroup: row.jobGroup
  }).then(res => {
    message.success('执行成功')
  }).catch(err => {
    console.error('执行失败:', err)
    message.error('执行失败')
  })
}

function handleViewJob(row) {
  currentJob.value = row
  showViewDialog.value = true
}

function handleOpenLog() {
  showLogDialog.value = true
  fetchJobLogs()
}

function fetchJobLogs() {
  logLoading.value = true
  getJobLogsApi({
    current: logPagination.page,
    size: logPagination.pageSize,
    jobName: logSearchForm.jobName || undefined,
    jobGroup: logSearchForm.jobGroup || undefined,
    status: logSearchForm.status !== null ? logSearchForm.status : undefined
  }).then(res => {
    logList.value = res.data.records || []
    logPagination.itemCount = res.data.total || 0
    logLoading.value = false
  }).catch(err => {
    console.error('获取日志失败:', err)
    message.error('获取日志失败')
    logLoading.value = false
  })
}

function handleSearchLogs() {
  logPagination.page = 1
  fetchJobLogs()
}

function handleResetLogs() {
  Object.assign(logSearchForm, {
    jobName: '',
    jobGroup: null,
    status: null
  })
  handleSearchLogs()
}

function handleLogPageChange(page) {
  logPagination.page = page
  fetchJobLogs()
}

function handleLogPageSizeChange(pageSize) {
  logPagination.pageSize = pageSize
  logPagination.page = 1
  fetchJobLogs()
}

function handleViewLogDetail(row) {
  currentLog.value = row
  showLogDetailDialog.value = true
}

function handleBatchDeleteLogs() {
  if (selectedLogIds.value.length === 0) {
    message.warning('请选择要删除的日志')
    return
  }
  deleteJobLogsApi(selectedLogIds.value).then(() => {
    message.success('删除成功')
    selectedLogIds.value = []
    fetchJobLogs()
  }).catch(err => {
    console.error('删除日志失败:', err)
    message.error('删除日志失败')
  })
}

function handleCleanLogs() {
  cleanJobLogsApi().then(() => {
    message.success('清空成功')
    fetchJobLogs()
  }).catch(err => {
    console.error('清空日志失败:', err)
    message.error('清空日志失败')
  })
}

function handlePageChange(page) {
  pagination.page = page
  fetchJobs()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchJobs()
}

onMounted(() => {
  fetchJobs()
  fetchJobGroups()
})
</script>