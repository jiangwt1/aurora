<template>
  <AppPage>
    <n-card title="角色管理" rounded-10>
      <n-space vertical size="large">
        <!-- 操作栏 -->
        <n-space justify="space-between">
          <n-space>
            <n-button type="primary" @click="handleAddRole">
              <template #icon><n-icon><AddOutline /></n-icon></template>
              新增
            </n-button>
            <n-button
              type="error"
              :disabled="selectedIds.length === 0"
              @click="isDeleteDialog = true"
            >
              <template #icon><n-icon><TrashOutline /></n-icon></template>
              批量删除
            </n-button>
          </n-space>

          <!-- 搜索栏 -->
          <n-space>
            <n-input
              v-model:value="keywords"
              placeholder="请输入角色名"
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
          :data="roleList"
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

    <!-- 菜单权限对话框 -->
    <n-modal v-model:show="showMenuDialog" preset="card" title="修改角色" style="width: 500px">
      <n-form label-placement="left" label-width="80px">
        <n-form-item label="角色名">
          <n-input v-model:value="roleForm.roleName" />
        </n-form-item>
        <n-form-item label="菜单权限">
          <div style="width: 100%; padding-right: 10px">
            <n-tree
              ref="menuTreeRef"
              :data="menuTree"
              v-model:checked-keys="roleForm.menuIds"
              checkable
              cascade
              key-field="id"
              children-field="children"
              label-field="label"
              style="max-height: 400px; overflow-y: auto; padding-right: 5px"
            />
          </div>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showMenuDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSaveMenu">确定</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 资源权限对话框 -->
    <n-modal v-model:show="showResourceDialog" preset="card" title="修改资源权限" style="width: 500px">
      <n-form label-placement="left" label-width="80px">
        <n-form-item label="角色名">
          <n-input v-model:value="roleForm.roleName" />
        </n-form-item>
        <n-form-item label="资源权限">
          <div style="width: 100%; padding-right: 10px">
            <n-tree
              ref="resourceTreeRef"
              :data="resourceTree"
              v-model:checked-keys="roleForm.resourceIds"
              checkable
              cascade
              key-field="id"
              children-field="children"
              label-field="label"
              style="max-height: 400px; overflow-y: auto; padding-right: 5px"
            />
          </div>
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="showResourceDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSaveResource">确定</n-button>
        </n-space>
      </template>
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
  </AppPage>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NSpace, NTag, NTree, NPagination, useMessage, useDialog } from 'naive-ui'
import { AddOutline, TrashOutline } from '@vicons/ionicons5'
import { getRolesApi, getAllMenusApi, getAllResourcesApi, saveRoleApi, batchDeleteRolesApi } from '@/api/role'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const message = useMessage()
const dialog = useDialog()

const loading = ref(false)
const roleList = ref([])
const menuTree = ref([])
const resourceTree = ref([])
const selectedIds = ref([])
const keywords = ref('')

const showMenuDialog = ref(false)
const showResourceDialog = ref(false)
const isDeleteDialog = ref(false)
const menuTreeRef = ref(null)
const resourceTreeRef = ref(null)

const roleForm = reactive({
  id: null,
  roleName: '',
  roleLabel: '',
  menuIds: [],
  resourceIds: []
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20]
})

const columns = [
  { type: 'selection' },
  {
    title: '角色名',
    key: 'roleName',
    width: 150
  },
  {
    title: '权限标签',
    key: 'roleLabel',
    width: 150,
    render: (row) => {
      return h(NTag, {}, { default: () => row.roleName })
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
    render: (row) => {
      return h(NSpace, null, {
        default: () => [
          h(NButton, {
            size: 'small',
            onClick: () => handleOpenMenuModel(row)
          }, { default: () => '菜单权限' }),
          h(NButton, {
            size: 'small',
            onClick: () => handleOpenResourceModel(row)
          }, { default: () => '资源权限' }),
          h(NButton, {
            size: 'small',
            type: 'error',
            disabled: row.id === 1,
            onClick: () => handleDeleteRole(row.id)
          }, { default: () => '删除' })
        ]
      })
    }
  }
]

function fetchRoles() {
  loading.value = true
  getRolesApi({
    current: pagination.page,
    size: pagination.pageSize,
    keywords: keywords.value
  }).then(res => {
    roleList.value = res.data.records || []
    pagination.itemCount = res.data.count || 0
  }).catch(err => {
    console.error('获取角色列表失败:', err)
    message.error('获取角色列表失败')
  }).finally(() => {
    loading.value = false
  })
}

function fetchMenus() {
  getAllMenusApi().then(res => {
    menuTree.value = res.data || []
  }).catch(err => {
    console.error('获取菜单列表失败:', err)
  })
}

function fetchResources() {
  getAllResourcesApi().then(res => {
    resourceTree.value = res.data || []
  }).catch(err => {
    console.error('获取资源列表失败:', err)
  })
}

function handleAddRole() {
  Object.assign(roleForm, {
    id: null,
    roleName: '',
    roleLabel: '',
    menuIds: [],
    resourceIds: []
  })
  showMenuDialog.value = true
}

function handleOpenMenuModel(role) {
  if (role) {
    Object.assign(roleForm, {
      ...role,
      menuIds: role.menuIds || []
    })
  } else {
    Object.assign(roleForm, {
      id: null,
      roleName: '',
      roleLabel: '',
      menuIds: []
    })
  }

  showMenuDialog.value = true
}

function handleOpenResourceModel(role) {
  Object.assign(roleForm, {
    ...role,
    resourceIds: role.resourceIds || []
  })

  showResourceDialog.value = true
}

function handleSaveMenu() {
  roleForm.resourceIds = null
  // menuIds 已经由 v-model:checked-keys 自动更新

  saveRoleApi(roleForm).then(res => {
    message.success('保存成功')
    showMenuDialog.value = false
    fetchRoles()
  }).catch(err => {
    console.error('保存失败:', err)
    message.error('保存失败')
  })
}

function handleSaveResource() {
  roleForm.menuIds = null
  // resourceIds 已经由 v-model:checked-keys 自动更新

  saveRoleApi(roleForm).then(res => {
    message.success('保存成功')
    showResourceDialog.value = false
    fetchRoles()
  }).catch(err => {
    console.error('保存失败:', err)
    message.error('保存失败')
  })
}

function handleDeleteRole(id) {
  dialog.warning({
    title: '确认删除',
    content: '确定删除该角色吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      batchDeleteRolesApi([id]).then(() => {
        message.success('删除成功')
        fetchRoles()
      }).catch(err => {
        console.error('删除失败:', err)
        message.error('删除失败')
      })
    }
  })
}

function handleBatchDelete() {
  if (selectedIds.value.length === 0) {
    message.warning('请选择要删除的角色')
    return
  }

  batchDeleteRolesApi(selectedIds.value).then(() => {
    message.success('删除成功')
    selectedIds.value = []
    isDeleteDialog.value = false
    fetchRoles()
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handleSearch() {
  pagination.page = 1
  fetchRoles()
}

function handlePageChange(page) {
  pagination.page = page
  fetchRoles()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchRoles()
}

onMounted(() => {
  fetchRoles()
  fetchMenus()
  fetchResources()
})
</script>
