<template>
  <AppPage>
    <n-card title="标签管理" rounded-10>
      <n-space vertical size="large">
        <!-- 操作栏 -->
        <n-space>
          <n-button type="primary" @click="handleAdd">
            <template #icon><n-icon><AddOutline /></n-icon></template>
            添加标签
          </n-button>
          <n-button @click="fetchTags">
            <template #icon><n-icon><RefreshOutline /></n-icon></template>
            刷新
          </n-button>
        </n-space>

        <!-- 表格 -->
        <n-data-table
          :columns="columns"
          :data="tagList"
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

    <!-- 添加/编辑对话框 -->
    <n-modal v-model:show="showDialog" preset="dialog" :title="isEdit ? '编辑标签' : '添加标签'">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80px">
        <n-form-item label="标签名称" path="tagName">
          <n-input v-model:value="form.tagName" placeholder="请输入标签名称" />
        </n-form-item>
        <n-form-item label="颜色" path="color">
          <n-color-picker v-model:value="form.color" />
        </n-form-item>
      </n-form>
      <template #action>
        <n-space>
          <n-button @click="showDialog = false">取消</n-button>
          <n-button type="primary" @click="handleSubmit">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </AppPage>
</template>

<script setup>
import { ref, reactive, h, onMounted } from 'vue'
import { NButton, NSpace, NTag, NPopconfirm, NPagination, useMessage } from 'naive-ui'
import { AddOutline, RefreshOutline } from '@vicons/ionicons5'
import { getTagsApi, saveTagApi, deleteTagApi } from '@/api/tag'

const message = useMessage()

const loading = ref(false)
const tagList = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  tagName: '',
  color: '#18a058'
})

const rules = {
  tagName: { required: true, message: '请输入标签名称', trigger: 'blur' }
}

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50]
})

const columns = [
  {
    title: 'ID',
    key: 'id',
    width: 80
  },
  {
    title: '标签名称',
    key: 'tagName',
    render: (row) => {
      return h(NTag, {
        color: { color: row.color, borderColor: row.color }
      }, { default: () => row.tagName })
    }
  },
  {
    title: '文章数',
    key: 'articleCount',
    width: 100
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 180
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
            default: () => '确定删除该标签吗？'
          })
        ]
      })
    }
  }
]

function fetchTags() {
  loading.value = true
  getTagsApi({
    current: pagination.page,
    size: pagination.pageSize
  }).then(res => {
    tagList.value = res.data.records || []
    pagination.itemCount = res.data.count || res.data.total || 0
    loading.value = false
  }).catch(err => {
    console.error('获取标签列表失败:', err)
    message.error('获取标签列表失败')
    loading.value = false
  })
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    tagName: '',
    color: '#18a058'
  })
  showDialog.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  showDialog.value = true
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    await saveTagApi(form)
    message.success(isEdit.value ? '更新标签成功' : '添加标签成功')
    showDialog.value = false
    fetchTags()
  } catch (error) {
    if (error !== false) {
      console.error('保存标签失败:', error)
      message.error('保存标签失败')
    }
  }
}

function handleDelete(id) {
  deleteTagApi([id]).then(() => {
    message.success('删除成功')
    fetchTags()
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handlePageChange(page) {
  pagination.page = page
  fetchTags()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchTags()
}

onMounted(() => {
  fetchTags()
})
</script>
