<template>
  <AppPage>
    <n-card title="说说管理" rounded-10>
      <n-space vertical size="large">
        <!-- 操作栏 -->
        <n-space justify="space-between">
          <n-space>
            <n-button type="primary" @click="handleAdd">
              <template #icon><n-icon><AddOutline /></n-icon></template>
              发布说说
            </n-button>
            <n-button @click="fetchTalks">
              <template #icon><n-icon><RefreshOutline /></n-icon></template>
              刷新
            </n-button>
          </n-space>

          <!-- 状态筛选 -->
          <n-space>
            <span>状态：</span>
            <n-button
              :type="searchForm.status === null ? 'primary' : 'default'"
              size="small"
              @click="changeStatus(null)"
            >
              全部
            </n-button>
            <n-button
              :type="searchForm.status === 1 ? 'primary' : 'default'"
              size="small"
              @click="changeStatus(1)"
            >
              公开
            </n-button>
            <n-button
              :type="searchForm.status === 2 ? 'primary' : 'default'"
              size="small"
              @click="changeStatus(2)"
            >
              私密
            </n-button>
          </n-space>
        </n-space>

        <!-- 说说列表 -->
        <n-spin :show="loading">
          <n-empty v-if="!loading && talkList.length === 0" description="暂无说说" />
          <div v-else class="talk-list">
            <div v-for="item in talkList" :key="item.id" class="talk-item">
              <div class="user-info-wrapper">
                <n-avatar :src="item.avatar" :size="36" />
                <div class="user-detail-wrapper">
                  <div class="user-nickname">
                    <span>{{ item.nickname }}</span>
                    <n-dropdown trigger="click" :options="getActionOptions(item)" @select="(key) => handleAction(key, item.id)">
                      <n-button text>
                        <template #icon>
                          <n-icon><EllipsisVerticalOutline /></n-icon>
                        </template>
                      </n-button>
                    </n-dropdown>
                  </div>
                  <div class="time">
                    <span>{{ formatDateTime(item.createTime) }}</span>
                    <span v-if="item.isTop === 1" class="top">
                      <n-icon><ArrowUpOutline /></n-icon> 置顶
                    </span>
                    <span v-if="item.status === 2" class="secret">
                      <n-icon><LockClosedOutline /></n-icon> 私密
                    </span>
                  </div>
                  <div class="talk-content" v-html="item.content" />
                  <n-grid v-if="item.imgs && item.imgs.length > 0" :x-gap="4" :cols="3" class="talk-images">
                    <n-gi v-for="(img, index) in item.imgs" :key="index">
                      <n-image
                        class="image-item"
                        :src="img"
                        :preview-src-list="item.imgs"
                        :initial-index="index"
                      />
                    </n-gi>
                  </n-grid>
                </div>
              </div>
            </div>
          </div>
        </n-spin>

        <!-- 分页 -->
        <n-pagination
          v-if="pagination.itemCount > 0"
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { NButton, NSpace, NTag, NPopconfirm, NAvatar, NImage, NDropdown, NPagination, useMessage, useDialog } from 'naive-ui'
import { AddOutline, RefreshOutline, ArrowUpOutline, LockClosedOutline, CreateOutline, TrashOutline, EllipsisVerticalOutline } from '@vicons/ionicons5'
import { h } from 'vue'
import { getTalksApi, deleteTalkApi } from '@/api/talk'
import dayjs from 'dayjs'

const router = useRouter()
const message = useMessage()
const dialog = useDialog()

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const loading = ref(false)
const talkList = ref([])
const talkId = ref(null)

const searchForm = reactive({
  status: null
})

const pagination = reactive({
  page: 1,
  pageSize: 5,
  itemCount: 0
})

// 下拉操作选项
function getActionOptions(item) {
  return [
    {
      label: '编辑',
      key: 'edit',
      icon: () => h(CreateOutline)
    },
    {
      label: '删除',
      key: 'delete',
      icon: () => h(TrashOutline)
    }
  ]
}

function fetchTalks() {
  loading.value = true
  getTalksApi({
    current: pagination.page,
    size: pagination.pageSize,
    status: searchForm.status
  }).then(res => {
    talkList.value = res.data.records || []
    pagination.itemCount = res.data.count || 0
  }).catch(err => {
    console.error('获取说说列表失败:', err)
    message.error('获取说说列表失败')
  }).finally(() => {
    loading.value = false
  })
}

function handleAction(key, id) {
  talkId.value = id
  if (key === 'edit') {
    handleEdit(id)
  } else if (key === 'delete') {
    handleDeleteConfirm(id)
  }
}

function handleAdd() {
  router.push('/talks/new')
}

function handleEdit(id) {
  router.push(`/talks/${id}`)
}

function handleDeleteConfirm(id) {
  dialog.warning({
    title: '确认删除',
    content: '确定删除该说说吗？',
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: () => {
      deleteTalkApi([id]).then(() => {
        message.success('删除成功')
        fetchTalks()
      }).catch(err => {
        console.error('删除失败:', err)
        message.error('删除失败')
      })
    }
  })
}

function changeStatus(status) {
  searchForm.status = status
  pagination.page = 1
  fetchTalks()
}

function handlePageChange(page) {
  pagination.page = page
  fetchTalks()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchTalks()
}

onMounted(() => {
  fetchTalks()
})
</script>

<style scoped>
.talk-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.talk-item {
  padding: 16px 20px;
  border-radius: 10px;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.talk-item:hover {
  box-shadow: 0 5px 10px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-3px);
}

.user-info-wrapper {
  display: flex;
  width: 100%;
}

.user-detail-wrapper {
  margin-left: 10px;
  width: 100%;
}

.user-nickname {
  font-size: 15px;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time {
  color: #999;
  margin-top: 2px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.top {
  color: #ff7242;
  display: flex;
  align-items: center;
  gap: 4px;
}

.secret {
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.talk-content {
  margin-top: 8px;
  font-size: 14px;
  line-height: 26px;
  white-space: pre-line;
  word-wrap: break-word;
  word-break: break-all;
}

.talk-images {
  margin-top: 8px;
}

.image-item {
  width: 100%;
  height: 200px;
  border-radius: 4px;
  cursor: pointer;
}
</style>
