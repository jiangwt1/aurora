<template>
  <AppPage>
    <n-card title="资源权限管理" rounded-10>
      <n-space vertical size="large">
        <!-- 搜索栏 -->
        <n-form inline>
          <n-form-item label="资源名">
            <n-input v-model:value="keywords" placeholder="请输入资源名" clearable style="width: 200px" @keyup.enter="fetchResources" />
          </n-form-item>
          <n-form-item>
            <n-button type="primary" @click="fetchResources">搜索</n-button>
          </n-form-item>
        </n-form>

        <!-- 操作栏 -->
        <n-space>
          <n-button type="primary" @click="handleAddModule">
            <template #icon><n-icon><AddOutline /></n-icon></template>
            新增模块
          </n-button>
        </n-space>

        <!-- 资源树表格 -->
        <n-data-table
          :columns="columns"
          :data="resourceTree"
          :loading="loading"
          :row-key="(row) => row.id"
          :pagination="false"
          default-expand-all
          children-key="children"
        />
      </n-space>
    </n-card>

    <!-- 添加/编辑模块对话框 -->
    <n-modal v-model:show="showModuleDialog" preset="dialog" title="新增模块">
      <n-form ref="moduleFormRef" :model="moduleForm" :rules="moduleRules" label-placement="left" label-width="80px">
        <n-form-item label="模块名" path="resourceName">
          <n-input v-model:value="moduleForm.resourceName" placeholder="请输入模块名" />
        </n-form-item>
      </n-form>
      <template #action>
        <n-space>
          <n-button @click="showModuleDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSaveModule">确定</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 添加/编辑资源对话框 -->
    <n-modal v-model:show="showResourceDialog" preset="dialog" :title="isEditResource ? '修改资源' : '新增资源'">
      <n-form ref="resourceFormRef" :model="resourceForm" :rules="resourceRules" label-placement="left" label-width="100px">
        <n-form-item label="资源名" path="resourceName">
          <n-input v-model:value="resourceForm.resourceName" placeholder="请输入资源名" />
        </n-form-item>
        <n-form-item label="资源路径" path="url">
          <n-input v-model:value="resourceForm.url" placeholder="请输入资源路径，例如：/api/admin/users" />
        </n-form-item>
        <n-form-item label="请求方式" path="requestMethod">
          <n-select v-model:value="resourceForm.requestMethod" :options="methodOptions" placeholder="请选择请求方式" />
        </n-form-item>
        <n-form-item label="匿名访问" path="isAnonymous">
          <n-switch v-model:value="resourceForm.isAnonymous" />
          <template #feedback>
            开启后该资源无需登录即可访问
          </template>
        </n-form-item>
      </n-form>
      <template #action>
        <n-space>
          <n-button @click="showResourceDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSaveResource">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </AppPage>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NSpace, NTag, NSwitch, NPopconfirm, useMessage } from 'naive-ui'
import { AddOutline } from '@vicons/ionicons5'
import { getResourcesApi, saveResourceApi, deleteResourceApi } from '@/api/resource'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const message = useMessage()

const loading = ref(false)
const resourceTree = ref([])
const keywords = ref('')

// 模块表单
const showModuleDialog = ref(false)
const moduleFormRef = ref(null)
const moduleForm = reactive({
  resourceName: '',
  parentId: null
})

const moduleRules = {
  resourceName: { required: true, message: '请输入模块名', trigger: 'blur' }
}

// 资源表单
const showResourceDialog = ref(false)
const isEditResource = ref(false)
const resourceFormRef = ref(null)
const resourceForm = reactive({
  id: null,
  resourceName: '',
  url: '',
  requestMethod: 'GET',
  isAnonymous: 0,
  parentId: null
})

const resourceRules = {
  resourceName: { required: true, message: '请输入资源名', trigger: 'blur' },
  url: { required: true, message: '请输入资源路径', trigger: 'blur' },
  requestMethod: { required: true, message: '请选择请求方式', trigger: 'change' }
}

const methodOptions = [
  { label: 'GET', value: 'GET' },
  { label: 'POST', value: 'POST' },
  { label: 'PUT', value: 'PUT' },
  { label: 'DELETE', value: 'DELETE' },
  { label: 'PATCH', value: 'PATCH' }
]

const columns = [
  {
    title: '资源名',
    key: 'resourceName',
    width: 220,
    tree: true  // 标记为树形列
  },
  {
    title: '资源路径',
    key: 'url',
    width: 300
  },
  {
    title: '请求方式',
    key: 'requestMethod',
    render: (row) => {
      if (!row.requestMethod || !row.url) return null

      const methodTypeMap = {
        'GET': 'success',
        'POST': 'info',
        'PUT': 'warning',
        'DELETE': 'error',
        'PATCH': 'default'
      }

      return h(NTag, {
        type: methodTypeMap[row.requestMethod] || 'default'
      }, {
        default: () => row.requestMethod
      })
    }
  },
  {
    title: '匿名访问',
    key: 'isAnonymous',
    width: 120,
    render: (row) => {
      if (!row.url) return null
      return h(NSwitch, {
        value: row.isAnonymous,
        checkedValue: 1,
        uncheckedValue: 0,
        onUpdateValue: (val) => handleChangeAnonymous(row, val)
      })
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
    width: 280,
    fixed: 'right',
    render: (row) => {
      const actions = []

      // 如果是模块（没有URL），显示新增子资源按钮
      if (!row.url) {
        actions.push(
          h(NButton, {
            size: 'small',
            type: 'primary',
            onClick: () => handleAddResource(row)
          }, { default: () => '新增资源' })
        )
      }

      // 如果有URL（是具体资源），显示修改按钮
      if (row.url) {
        actions.push(
          h(NButton, {
            size: 'small',
            onClick: () => handleEditResource(row)
          }, { default: () => '修改' })
        )
      }

      // 删除按钮
      actions.push(
        h(NPopconfirm, {
          onPositiveClick: () => handleDelete(row.id)
        }, {
          trigger: () => h(NButton, {
            size: 'small',
            type: 'error'
          }, { default: () => '删除' }),
          default: () => '确定删除该资源吗？删除后子资源也会被删除。'
        })
      )

      return h(NSpace, {
        size: 'small'
      }, {
        default: () => actions
      })
    }
  }
]

function fetchResources() {
  loading.value = true
  getResourcesApi().then(res => {
    // API返回的已经是树形结构，直接使用
    // 清理 null 和空数组的 children 字段
    const cleanTree = (nodes) => {
      if (!nodes || nodes.length === 0) return []

      return nodes.map(node => {
        const cleaned = { ...node }
        // 如果有子节点且不为空，递归清理；否则删除children字段
        if (cleaned.children && Array.isArray(cleaned.children) && cleaned.children.length > 0) {
          cleaned.children = cleanTree(cleaned.children)
        } else {
          // 删除null或空数组的children
          delete cleaned.children
        }
        return cleaned
      })
    }
    resourceTree.value = cleanTree(res.data || [])
    loading.value = false
  }).catch(err => {
    console.error('获取资源列表失败:', err)
    message.error('获取资源列表失败')
    loading.value = false
  })
}

function handleAddModule() {
  Object.assign(moduleForm, {
    resourceName: '',
    parentId: null
  })
  showModuleDialog.value = true
}

async function handleSaveModule() {
  try {
    await moduleFormRef.value?.validate()
    await saveResourceApi(moduleForm)
    message.success('新增模块成功')
    showModuleDialog.value = false
    fetchResources()
  } catch (error) {
    if (error !== false) {
      console.error('保存模块失败:', error)
      message.error('保存模块失败')
    }
  }
}

function handleAddResource(parent) {
  isEditResource.value = false
  Object.assign(resourceForm, {
    id: null,
    resourceName: '',
    url: '',
    requestMethod: 'GET',
    isAnonymous: 0,
    parentId: parent.id
  })
  showResourceDialog.value = true
}

function handleEditResource(row) {
  isEditResource.value = true
  Object.assign(resourceForm, {
    id: row.id,
    resourceName: row.resourceName,
    url: row.url,
    requestMethod: row.requestMethod || 'GET',
    isAnonymous: row.isAnonymous === 1,
    parentId: row.parentId
  })
  showResourceDialog.value = true
}

async function handleSaveResource() {
  try {
    await resourceFormRef.value?.validate()
    const data = { ...resourceForm, isAnonymous: resourceForm.isAnonymous ? 1 : 0 }
    await saveResourceApi(data)
    message.success(isEditResource.value ? '修改资源成功' : '新增资源成功')
    showResourceDialog.value = false
    fetchResources()
  } catch (error) {
    if (error !== false) {
      console.error('保存资源失败:', error)
      message.error('保存资源失败')
    }
  }
}

function handleDelete(id) {
  deleteResourceApi(id).then(() => {
    message.success('删除成功')
    fetchResources()
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handleChangeAnonymous(row, value) {
  row.isAnonymous = value
  saveResourceApi(row).then(() => {
    message.success('修改成功')
  }).catch(err => {
    row.isAnonymous = value === 1 ? 0 : 1
    console.error('修改失败:', err)
    message.error('修改失败')
  })
}

onMounted(() => {
  fetchResources()
})
</script>