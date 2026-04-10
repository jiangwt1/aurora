<template>
  <AppPage>
    <n-card title="菜单管理" rounded-10>
      <n-space vertical size="large">
        <!-- 操作栏 -->
        <n-space justify="space-between">
          <n-button type="primary" @click="handleAdd(null, 1)">
            <template #icon><n-icon><AddOutline /></n-icon></template>
            新增菜单
          </n-button>

          <!-- 搜索栏 -->
          <n-space>
            <n-input
              v-model:value="keywords"
              placeholder="请输入菜单名"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
            <n-button type="primary" @click="handleSearch">搜索</n-button>
          </n-space>
        </n-space>

        <!-- 表格 -->
        <n-data-table
          :columns="columns"
          :data="menuList"
          :loading="loading"
          :pagination="false"
          :row-key="(row) => row.id"
          children-field="children"
        />
      </n-space>
    </n-card>

    <!-- 添加/编辑对话框 -->
    <n-modal v-model:show="showDialog" preset="card" :title="dialogTitle" style="width: 500px">
      <n-form ref="formRef" :model="menuForm" :rules="rules" label-placement="left" label-width="100px">
        <n-form-item v-if="showMenuType" label="菜单类型" path="isCatalog">
          <n-radio-group v-model:value="isCatalog">
            <n-radio :value="true">目录</n-radio>
            <n-radio :value="false">一级菜单</n-radio>
          </n-radio-group>
        </n-form-item>
        <n-form-item label="菜单名称" path="name">
          <n-input v-model:value="menuForm.name" placeholder="请输入菜单名称" />
        </n-form-item>
        <n-form-item label="菜单图标" path="icon">
          <n-input v-model:value="menuForm.icon" placeholder="请输入图标类名" />
        </n-form-item>
        <n-form-item v-if="!isCatalog" label="组件路径" path="component">
          <n-input v-model:value="menuForm.component" placeholder="请输入组件路径" />
        </n-form-item>
        <n-form-item label="访问路径" path="path">
          <n-input v-model:value="menuForm.path" placeholder="请输入访问路径" />
        </n-form-item>
        <n-form-item label="显示排序" path="orderNum">
          <n-input-number v-model:value="menuForm.orderNum" :min="1" :max="10" style="width: 100%" />
        </n-form-item>
        <n-form-item label="显示状态" path="isHidden">
          <n-radio-group v-model:value="menuForm.isHidden">
            <n-radio :value="0">显示</n-radio>
            <n-radio :value="1">隐藏</n-radio>
          </n-radio-group>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSave">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </AppPage>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NSpace, NSwitch, NIcon, NTag, useMessage } from 'naive-ui'
import { AddOutline } from '@vicons/ionicons5'
import { getMenusApi, saveMenuApi, deleteMenuApi, updateMenuHiddenApi } from '@/api/menu'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const message = useMessage()

const loading = ref(false)
const menuList = ref([])
const keywords = ref('')
const showDialog = ref(false)
const showMenuType = ref(false)
const isCatalog = ref(false)
const dialogTitle = ref('新增菜单')
const formRef = ref(null)

const menuForm = reactive({
  id: null,
  name: '',
  icon: '',
  component: '',
  path: '',
  orderNum: 1,
  isHidden: 0,
  parentId: null
})

const rules = {
  name: { required: true, message: '请输入菜单名称', trigger: 'blur' },
  path: { required: true, message: '请输入访问路径', trigger: 'blur' }
}

const columns = [
  {
    title: '菜单名称',
    key: 'name',
    width: 220
  },
  {
    title: '图标',
    key: 'icon',
    width: 120,
    render: (row) => {
      return row.icon ? h('i', { class: `iconfont ${row.icon}`, style: { fontSize: '18px' } }) : '-'
    }
  },
  {
    title: '排序',
    key: 'orderNum',
    width: 100,
    align: 'center'
  },
  {
    title: '访问路径',
    key: 'path',
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '组件路径',
    key: 'component',
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '隐藏',
    key: 'isHidden',
    width: 100,
    align: 'center',
    render: (row) => {
      return h(NSwitch, {
        value: row.isHidden === 1,
        onUpdateValue: (value) => handleChangeHidden(row, value)
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
    width: 220,
    fixed: 'right',
    render: (row) => {
      const actions = []

      if (row.children && row.children.length > 0) {
        actions.push(
          h(NButton, {
            size: 'small',
            onClick: () => handleAdd(row, 1)
          }, { default: () => '新增' })
        )
      } else {
        actions.push(
          h('div', { style: 'width: 40px; display: inline-block' })
        )
      }

      actions.push(
        h(NButton, {
          size: 'small',
          onClick: () => handleEdit(row)
        }, { default: () => '修改' })
      )

      actions.push(
        h(NButton, {
          size: 'small',
          type: 'error',
          onClick: () => handleDelete(row.id)
        }, { default: () => '删除' })
      )

      return h(NSpace, { size: 'small' }, {
        default: () => actions
      })
    }
  }
]

function fetchMenus() {
  loading.value = true
  getMenusApi({
    keywords: keywords.value
  }).then(res => {
    menuList.value = res.data || []
  }).catch(err => {
    console.error('获取菜单列表失败:', err)
    message.error('获取菜单列表失败')
  }).finally(() => {
    loading.value = false
  })
}

function handleAdd(row, type) {
  showMenuType.value = type === 1
  dialogTitle.value = type === 1 ? '新增菜单' : '修改菜单'
  isCatalog.value = false

  Object.assign(menuForm, {
    id: null,
    name: '',
    icon: '',
    component: '',
    path: '',
    orderNum: 1,
    isHidden: 0,
    parentId: row ? row.id : null
  })

  showDialog.value = true
}

function handleEdit(row) {
  showMenuType.value = false
  dialogTitle.value = '修改菜单'

  Object.assign(menuForm, {
    id: row.id,
    name: row.name,
    icon: row.icon || '',
    component: row.component || '',
    path: row.path || '',
    orderNum: row.orderNum,
    isHidden: row.isHidden || 0,
    parentId: row.parentId
  })

  showDialog.value = true
}

function handleSave() {
  formRef.value?.validate((errors) => {
    if (!errors) {
      saveMenuApi(menuForm).then(res => {
        message.success('保存成功')
        showDialog.value = false
        fetchMenus()
      }).catch(err => {
        console.error('保存失败:', err)
        message.error('保存失败')
      })
    }
  })
}

function handleDelete(id) {
  dialog.warning({
    title: '删除菜单',
    content: '确定要删除此菜单吗？删除后不可恢复。',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      deleteMenuApi([id]).then(() => {
        message.success('删除成功')
        fetchMenus()
      }).catch(err => {
        console.error('删除失败:', err)
        message.error('删除失败')
      })
    }
  })
}

function handleChangeHidden(row, value) {
  updateMenuHiddenApi({
    id: row.id,
    isHidden: value ? 1 : 0
  }).then(res => {
    message.success('更新成功')
    fetchMenus()
  }).catch(err => {
    console.error('更新失败:', err)
    message.error('更新失败')
  })
}

function handleSearch() {
  fetchMenus()
}

onMounted(() => {
  fetchMenus()
})
</script>

<style scoped>
:deep(.iconfont) {
  font-size: 18px;
}
</style>
