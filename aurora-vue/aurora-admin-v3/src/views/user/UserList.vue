<template>
  <AppPage>
    <n-card title="用户管理" rounded-10>
      <n-space vertical size="large">
        <!-- 操作栏 -->
        <n-space justify="end">
          <!-- 搜索栏 -->
          <n-space>
            <n-select
              v-model:value="searchForm.loginType"
              :options="loginTypeOptions"
              placeholder="请选择登录方式"
              clearable
              style="width: 150px"
            />
            <n-input
              v-model:value="searchForm.keywords"
              placeholder="请输入昵称"
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
          :data="userList"
          :loading="loading"
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

    <!-- 编辑对话框 -->
    <n-modal v-model:show="showEditDialog" preset="dialog" title="修改用户角色">
      <n-form ref="formRef" :model="userForm" label-placement="left" label-width="80px">
        <n-form-item label="昵称" path="nickname">
          <n-input v-model:value="userForm.nickname" placeholder="请输入昵称" />
        </n-form-item>
        <n-form-item label="角色" path="roleIds">
          <n-checkbox-group v-model:value="userForm.roleIds">
            <n-space vertical>
              <n-checkbox v-for="role in userRoles" :key="role.id" :value="role.id">
                {{ role.roleName }}
              </n-checkbox>
            </n-space>
          </n-checkbox-group>
        </n-form-item>
      </n-form>
      <template #action>
        <n-space>
          <n-button @click="showEditDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSaveUser">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </AppPage>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NSpace, NTag, NAvatar, NSwitch, NCheckbox, NCheckboxGroup, NPagination, useMessage } from 'naive-ui'
import { getUsersApi, updateUserDisableApi, updateUserRoleApi, getUserRolesApi } from '@/api/user'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const message = useMessage()

const loading = ref(false)
const userList = ref([])
const userRoles = ref([])
const showEditDialog = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  keywords: '',
  loginType: null
})

const userForm = reactive({
  userInfoId: null,
  nickname: '',
  roleIds: []
})

const loginTypeOptions = [
  { label: '邮箱', value: 1 },
  { label: 'QQ', value: 2 }
]

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20]
})

const columns = [
  {
    title: '头像',
    key: 'avatar',
    width: 80,
    render: (row) => {
      return h(NAvatar, {
        size: 40,
        src: row.avatar
      })
    }
  },
  {
    title: '昵称',
    key: 'nickname',
    width: 120
  },
  {
    title: '登录方式',
    key: 'loginType',
    width: 90,
    render: (row) => {
      const loginTypeMap = {
        1: { text: '邮箱', type: 'success' },
        2: { text: 'QQ', type: 'default' }
      }
      const type = loginTypeMap[row.loginType] || { text: '未知', type: 'default' }
      return h(NTag, { type: type.type }, { default: () => type.text })
    }
  },
  {
    title: '用户角色',
    key: 'roles',
    width: 150,
    render: (row) => {
      if (!row.roles || row.roles.length === 0) {
        return h('span', { style: { color: '#999' } }, '无')
      }
      return h(NSpace, { size: 'small' }, {
        default: () => row.roles.map(role =>
          h(NTag, { key: role.id }, { default: () => role.roleName })
        )
      })
    }
  },
  {
    title: '禁用',
    key: 'isDisable',
    width: 80,
    render: (row) => {
      return h(NSwitch, {
        value: row.isDisable === 1,
        onUpdateValue: (value) => handleDisableChange(row, value)
      })
    }
  },
  {
    title: '登录ip',
    key: 'ipAddress',
    width: 120
  },
  {
    title: '登录地址',
    key: 'ipSource',
    width: 120,
    ellipsis: {
      tooltip: true
    }
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180,
    render: (row) => formatDateTime(row.createTime)
  },
  {
    title: '上次登录时间',
    key: 'lastLoginTime',
    width: 180,
    render: (row) => formatDateTime(row.lastLoginTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 80,
    render: (row) => {
      return h(NButton, {
        size: 'small',
        type: 'primary',
        onClick: () => handleEdit(row)
      }, { default: () => '编辑' })
    }
  }
]

function fetchUsers() {
  loading.value = true
  getUsersApi({
    current: pagination.page,
    size: pagination.pageSize,
    keywords: searchForm.keywords,
    loginType: searchForm.loginType
  }).then(res => {
    userList.value = res.data.records || []
    pagination.itemCount = res.data.count || 0
  }).catch(err => {
    console.error('获取用户列表失败:', err)
    message.error('获取用户列表失败')
  }).finally(() => {
    loading.value = false
  })
}

function fetchUserRoles() {
  getUserRolesApi().then(res => {
    userRoles.value = res.data || []
  }).catch(err => {
    console.error('获取角色列表失败:', err)
  })
}

function handleEdit(row) {
  Object.assign(userForm, {
    userInfoId: row.userInfoId,
    nickname: row.nickname,
    roleIds: row.roles ? row.roles.map(r => r.id) : []
  })
  showEditDialog.value = true
}

function handleDisableChange(row, value) {
  updateUserDisableApi({
    id: row.userInfoId,
    isDisable: value ? 1 : 0
  }).then(res => {
    message.success(value ? '已禁用用户' : '已启用用户')
    fetchUsers()
  }).catch(err => {
    console.error('更新用户状态失败:', err)
    message.error('更新用户状态失败')
  })
}

function handleSaveUser() {
  if (!userForm.nickname.trim()) {
    message.error('请输入昵称')
    return
  }

  updateUserRoleApi(userForm).then(res => {
    message.success('修改成功')
    showEditDialog.value = false
    fetchUsers()
  }).catch(err => {
    console.error('修改用户失败:', err)
    message.error('修改用户失败')
  })
}

function handleSearch() {
  pagination.page = 1
  fetchUsers()
}

function handlePageChange(page) {
  pagination.page = page
  fetchUsers()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchUsers()
}

onMounted(() => {
  fetchUsers()
  fetchUserRoles()
})
</script>
