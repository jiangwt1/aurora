<template>
  <AppPage>
    <n-card title="在线用户" rounded-10>
      <n-space vertical size="large">
        <!-- 搜索栏 -->
        <n-form inline>
          <n-form-item label="关键词">
            <n-input v-model:value="keywords" placeholder="请输入用户昵称" clearable style="width: 200px" @keyup.enter="fetchOnlineUsers" />
          </n-form-item>
          <n-form-item>
            <n-button type="primary" @click="fetchOnlineUsers">搜索</n-button>
          </n-form-item>
        </n-form>

        <!-- 在线用户表格 -->
        <n-data-table
          :columns="columns"
          :data="onlineUsers"
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
  </AppPage>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NAvatar, NPopconfirm, NSpace, NPagination, useMessage } from 'naive-ui'
import { TrashOutline } from '@vicons/ionicons5'
import { getOnlineUsersApi, offlineUserApi } from '@/api/user'
import { getAvatarPlaceholder } from '@/utils/placeholder'
import { useUserStore } from '@/store/modules/user'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const message = useMessage()
const userStore = useUserStore()
const router = useRouter()

const loading = ref(false)
const onlineUsers = ref([])
const keywords = ref('')

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

const columns = [
  {
    title: '头像',
    key: 'avatar',
    width: 100,
    render: (row) => {
      return h(NAvatar, {
        size: 40,
        src: row.avatar,
        fallbackSrc: getAvatarPlaceholder(row.nickname || row.username || 'U')
      })
    }
  },
  {
    title: '昵称',
    key: 'nickname',
    width: 120
  },
  {
    title: 'IP地址',
    key: 'ipAddress',
    width: 140
  },
  {
    title: '登录地址',
    key: 'ipSource',
    width: 200,
    ellipsis: { tooltip: true }
  },
  {
    title: '浏览器',
    key: 'browser',
    width: 160,
    ellipsis: { tooltip: true }
  },
  {
    title: '操作系统',
    key: 'os',
    width: 120
  },
  {
    title: '登录时间',
    key: 'lastLoginTime',
    width: 180,
    render: (row) => formatDateTime(row.lastLoginTime)
  },
  {
    title: '操作',
    key: 'actions',
    width: 100,
    render: (row) => {
      return h(NSpace, null, {
        default: () => [
          h(NPopconfirm, {
            onPositiveClick: () => handleOffline(row)
          }, {
            trigger: () => h(NButton, {
              size: 'small',
              type: 'error'
            }, { default: () => '下线' }),
            default: () => '确定下线该用户吗？'
          })
        ]
      })
    }
  }
]

function fetchOnlineUsers() {
  loading.value = true
  getOnlineUsersApi({
    current: pagination.page,
    size: pagination.pageSize,
    keywords: keywords.value
  }).then(res => {
    onlineUsers.value = res.data.records || []
    pagination.itemCount = res.data.total || 0
    loading.value = false
  }).catch(err => {
    console.error('获取在线用户失败:', err)
    message.error('获取在线用户失败')
    loading.value = false
  })
}

function handleOffline(user) {
  offlineUserApi(user.userInfoId).then(() => {
    message.success('已下线用户：' + user.nickname)
    // 如果下线的是自己，跳转到登录页
    if (user.userInfoId === userStore.userInfo?.id) {
      sessionStorage.removeItem('token')
      router.push('/login')
    } else {
      fetchOnlineUsers()
    }
  }).catch(err => {
    console.error('下线失败:', err)
    message.error('下线失败')
  })
}

function handlePageChange(page) {
  pagination.page = page
  fetchOnlineUsers()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchOnlineUsers()
}

onMounted(() => {
  fetchOnlineUsers()
})
</script>