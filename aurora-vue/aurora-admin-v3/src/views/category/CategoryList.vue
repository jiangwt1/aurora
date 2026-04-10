<template>
  <AppPage>
    <n-card title="分类管理" rounded-10>
      <n-space vertical size="large">
        <!-- 操作栏 -->
        <n-space justify="space-between">
          <n-space>
            <n-button type="primary" @click="handleAdd">
              <template #icon><n-icon><AddOutline /></n-icon></template>
              添加分类
            </n-button>
            <n-popconfirm @positive-click="handleBatchDelete" :disabled="!selectedIds.length">
              <template #trigger>
                <n-button type="error" :disabled="!selectedIds.length">
                  <template #icon><n-icon><TrashOutline /></n-icon></template>
                  批量删除 ({{ selectedIds.length }})
                </n-button>
              </template>
              确定删除选中的分类吗？
            </n-popconfirm>
          </n-space>
          <n-space>
            <n-input
              v-model:value="keywords"
              placeholder="请输入分类名"
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
          :data="categoryList"
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

    <!-- 添加/编辑对话框 -->
    <n-modal v-model:show="showDialog" preset="dialog" :title="isEdit ? '编辑分类' : '添加分类'">
      <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" label-width="80px">
        <n-form-item label="分类名" path="categoryName">
          <n-input v-model:value="form.categoryName" placeholder="请输入分类名" />
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
import { AddOutline, TrashOutline } from '@vicons/ionicons5'
import { getCategoriesApi, saveCategoryApi, deleteCategoryApi } from '@/api/category'
import dayjs from 'dayjs'

function formatDateTime(dateStr) {
  if (!dateStr) return '-'
  return dayjs(dateStr).format('YYYY-MM-DD HH:mm:ss')
}

const message = useMessage()

const loading = ref(false)
const categoryList = ref([])
const selectedIds = ref([])
const showDialog = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const keywords = ref('')

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0
})

const form = reactive({
  id: null,
  categoryName: ''
})

const rules = {
  categoryName: { required: true, message: '请输入分类名', trigger: 'blur' }
}

const columns = [
  { type: 'selection' },
  {
    title: '分类名',
    key: 'categoryName',
    render: (row) => {
      return h(NTag, { type: 'info' }, { default: () => row.categoryName })
    }
  },
  {
    title: '文章量',
    key: 'articleCount',
    width: 100
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
    width: 160,
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
            default: () => '确定删除该分类吗？'
          })
        ]
      })
    }
  }
]

function fetchCategories() {
  loading.value = true
  getCategoriesApi({
    current: pagination.page,
    size: pagination.pageSize,
    keywords: keywords.value
  }).then(res => {
    categoryList.value = res.data.records || []
    pagination.itemCount = res.data.count || res.data.total || 0
  }).catch(err => {
    console.error('获取分类列表失败:', err)
    message.error('获取分类列表失败')
  }).finally(() => {
    loading.value = false
  })
}

function handleSearch() {
  pagination.page = 1
  fetchCategories()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    categoryName: ''
  })
  showDialog.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    categoryName: row.categoryName
  })
  showDialog.value = true
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
    await saveCategoryApi(form)
    message.success(isEdit.value ? '更新分类成功' : '添加分类成功')
    showDialog.value = false
    fetchCategories()
  } catch (error) {
    if (error !== false) {
      console.error('保存分类失败:', error)
      message.error('保存分类失败')
    }
  }
}

function handleDelete(id) {
  deleteCategoryApi([id]).then(() => {
    message.success('删除成功')
    fetchCategories()
  }).catch(err => {
    console.error('删除失败:', err)
    message.error('删除失败')
  })
}

function handleBatchDelete() {
  deleteCategoryApi(selectedIds.value).then(() => {
    message.success('批量删除成功')
    selectedIds.value = []
    fetchCategories()
  }).catch(err => {
    console.error('批量删除失败:', err)
    message.error('批量删除失败')
  })
}

function handlePageChange(page) {
  pagination.page = page
  fetchCategories()
}

function handlePageSizeChange(pageSize) {
  pagination.pageSize = pageSize
  pagination.page = 1
  fetchCategories()
}

onMounted(() => {
  fetchCategories()
})
</script>
