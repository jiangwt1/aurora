<template>
  <AppPage>
    <n-card :title="article.id ? '编辑文章' : '发布文章'" rounded-10>
      <n-space vertical size="large">
        <!-- 文章标题和操作按钮 -->
        <div class="article-header">
          <div class="title-row">
            <n-input
              v-model:value="article.articleTitle"
              placeholder="请输入文章标题..."
              size="large"
              class="title-input"
            />
            <div class="button-group">
              <n-button
                v-if="!article.id || article.status === 3"
                size="large"
                @click="saveArticleDraft"
              >
                保存草稿
              </n-button>
              <n-button type="primary" size="large" @click="openPublishModal">
                发布文章
              </n-button>
            </div>
          </div>
        </div>

        <!-- Markdown编辑器 -->
        <div class="markdown-editor-container">
          <MdEditor
            v-model="article.articleContent"
            :theme="theme"
            :preview-theme="previewTheme"
            :code-theme="codeTheme"
            :toolbarsExclude="['github', 'save']"
            @onUploadImg="handleUploadImg"
            style="height: 100%"
          />
        </div>
      </n-space>
    </n-card>

    <!-- 发布文章对话框 -->
    <n-modal v-model:show="showPublishModal" preset="card" title="发布文章" style="width: 700px">
      <n-form ref="formRef" :model="article" label-width="100px" label-placement="left">
        <!-- 文章分类 -->
        <n-form-item path="categoryName">
          <template #label>
            <span class="required-label">文章分类</span>
          </template>
          <n-space v-if="article.categoryName" align="center">
            <n-tag type="success" closable @close="removeCategory">
              {{ article.categoryName }}
            </n-tag>
            <n-button size="small" @click="categoryName = ''; showCategorySelect = true">更改</n-button>
          </n-space>
          <n-popover v-else trigger="click" placement="bottom" style="width: 460px">
            <template #trigger>
              <n-button type="success" secondary size="small">添加分类</n-button>
            </template>
            <n-space vertical>
              <n-text strong>分类</n-text>
              <n-input
                v-model:value="categoryName"
                placeholder="请输入分类名搜索，enter可添加自定义分类"
                clearable
                @keyup.enter="saveCategory"
              />
              <n-divider style="margin: 8px 0" />
              <div class="popover-container">
                <div
                  v-for="item in filteredCategoryList"
                  :key="item.id"
                  class="category-item"
                  @click="addCategory(item)"
                >
                  {{ item.categoryName }}
                </div>
              </div>
            </n-space>
          </n-popover>
        </n-form-item>

        <!-- 文章标签 -->
        <n-form-item>
          <template #label>
            <span class="required-label">文章标签</span>
          </template>
          <n-space align="center">
            <n-space v-if="article.tagNames.length > 0">
              <n-tag
                v-for="(tag, index) in article.tagNames"
                :key="index"
                closable
                @close="removeTag(tag)"
              >
                {{ tag }}
              </n-tag>
            </n-space>
            <n-popover v-if="article.tagNames.length < 3" trigger="click" placement="bottom" style="width: 460px">
              <template #trigger>
                <n-button type="primary" secondary size="small">添加标签</n-button>
              </template>
              <n-space vertical>
                <n-text strong>标签</n-text>
                <n-input
                  v-model:value="tagName"
                  placeholder="请输入标签名搜索，enter可添加自定义标签"
                  clearable
                  @keyup.enter="saveTag"
                />
                <n-divider style="margin: 8px 0" />
                <n-text depth="3">添加标签</n-text>
                <n-space>
                  <n-tag
                    v-for="tag in filteredTagList"
                    :key="tag.id"
                    :type="isTagSelected(tag) ? 'primary' : 'default'"
                    checkable
                    :checked="isTagSelected(tag)"
                    @update:checked="(checked) => toggleTag(tag, checked)"
                  >
                    {{ tag.tagName }}
                  </n-tag>
                </n-space>
              </n-space>
            </n-popover>
          </n-space>
        </n-form-item>

        <!-- 文章类型 -->
        <n-form-item label="文章类型">
          <n-select v-model:value="article.type" :options="typeOptions" />
        </n-form-item>

        <!-- 原文地址 -->
        <n-form-item v-if="article.type !== 1" label="原文地址">
          <n-input v-model:value="article.originalUrl" placeholder="请填写原文链接" />
        </n-form-item>

        <!-- 上传封面 -->
        <n-form-item>
          <template #label>
            <span class="required-label">上传封面</span>
          </template>
          <template v-if="!article.articleCover">
            <n-upload
              :custom-request="uploadCover"
              :show-file-list="false"
              accept="image/*"
              :max="1"
            >
              <n-upload-dragger>
                <div style="margin-bottom: 12px">
                  <n-icon size="48" :depth="3">
                    <CloudUploadOutline />
                  </n-icon>
                </div>
                <n-text>点击或拖动文件到该区域来上传</n-text>
              </n-upload-dragger>
            </n-upload>
          </template>
          <div v-else class="cover-preview">
            <img :src="article.articleCover" alt="封面" />
            <n-button circle size="small" type="error" @click="article.articleCover = ''">
              <template #icon>
                <n-icon><CloseOutline /></n-icon>
              </template>
            </n-button>
          </div>
        </n-form-item>

        <!-- 置顶和推荐 -->
        <n-form-item label="置顶">
          <n-switch v-model:value="article.isTop" :checked-value="1" :unchecked-value="0" />
        </n-form-item>

        <n-form-item label="推荐">
          <n-switch v-model:value="article.isFeatured" :checked-value="1" :unchecked-value="0" />
        </n-form-item>

        <!-- 发布形式 -->
        <n-form-item label="发布形式">
          <n-radio-group v-model:value="article.status">
            <n-radio :value="1">公开</n-radio>
            <n-radio :value="2">密码</n-radio>
          </n-radio-group>
        </n-form-item>

        <!-- 访问密码 -->
        <n-form-item v-if="article.status === 2" label="访问密码">
          <n-input v-model:value="article.password" placeholder="请填写文章访问密码" />
        </n-form-item>

        <!-- 文章摘要 -->
        <n-form-item label="文章摘要">
          <n-input
            v-model:value="article.articleAbstract"
            type="textarea"
            placeholder="默认取文章前500个字符"
            :autosize="{ minRows: 3, maxRows: 6 }"
          />
        </n-form-item>
      </n-form>

      <template #footer>
        <n-space justify="end">
          <n-button @click="showPublishModal = false">取消</n-button>
          <n-button type="primary" @click="saveOrUpdateArticle">发表</n-button>
        </n-space>
      </template>
    </n-modal>
  </AppPage>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NIcon, useMessage, useDialog } from 'naive-ui'
import { CloudUploadOutline, CloseOutline, DocumentTextOutline, SaveOutline, SendOutline } from '@vicons/ionicons5'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { request } from '@/utils/http'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const dialog = useDialog()

const showPublishModal = ref(false)
const categoryName = ref('')
const tagName = ref('')
const categoryList = ref([])
const tagList = ref([])
const autoSave = ref(true)

// 过滤后的分类列表（根据输入内容过滤）
const filteredCategoryList = computed(() => {
  if (!categoryName.value) return categoryList.value
  return categoryList.value.filter(item =>
    item.categoryName.toLowerCase().includes(categoryName.value.toLowerCase())
  )
})

// 过滤后的标签列表（根据输入内容过滤）
const filteredTagList = computed(() => {
  if (!tagName.value) return tagList.value
  return tagList.value.filter(item =>
    item.tagName.toLowerCase().includes(tagName.value.toLowerCase())
  )
})

// 主题配置
const theme = ref('light')
const previewTheme = ref('default')
const codeTheme = ref('atom')

const article = reactive({
  id: null,
  articleTitle: dayjs().format('YYYY-MM-DD'),
  articleContent: '',
  articleAbstract: '',
  articleCover: '',
  categoryName: null,
  tagNames: [],
  isTop: 0,
  isFeatured: 0,
  type: 1,
  status: 1,
  originalUrl: '',
  password: ''
})

// 重置文章表单
function resetArticle() {
  article.id = null
  article.articleTitle = dayjs().format('YYYY-MM-DD')
  article.articleContent = ''
  article.articleAbstract = ''
  article.articleCover = ''
  article.categoryName = null
  article.tagNames = []
  article.isTop = 0
  article.isFeatured = 0
  article.type = 1
  article.status = 1
  article.originalUrl = ''
  article.password = ''
}

const typeOptions = [
  { label: '原创', value: 1 },
  { label: '转载', value: 2 },
  { label: '翻译', value: 3 }
]

// 获取文章详情
async function fetchArticle() {
  const articleId = route.params.id
  if (!articleId) {
    // 检查是否有草稿
    const savedArticle = sessionStorage.getItem('article')
    if (savedArticle) {
      try {
        const saved = JSON.parse(savedArticle)
        Object.assign(article, saved)
      } catch (e) {
        console.error('解析草稿失败:', e)
      }
    }
    return
  }

  try {
    const res = await request.get('/api/admin/articles/' + articleId)
    Object.assign(article, res.data)
  } catch (error) {
    message.error('获取文章失败')
  }
}

// 获取分类列表
async function fetchCategories() {
  try {
    const res = await request.get('/api/admin/categories/search')
    categoryList.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取标签列表
async function fetchTags() {
  try {
    const res = await request.get('/api/admin/tags/search')
    tagList.value = res.data || []
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

// 打开发布对话框
function openPublishModal() {
  if (!article.articleTitle?.trim()) {
    message.error('文章标题不能为空')
    return
  }
  if (!article.articleContent?.trim()) {
    message.error('文章内容不能为空')
    return
  }
  fetchCategories()
  fetchTags()
  showPublishModal.value = true
}

// 上传封面
function uploadCover({ file, onFinish, onError }) {
  const formData = new FormData()
  formData.append('file', file.file)

  request.post('/api/admin/articles/images', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }).then(res => {
    if (res.flag) {
      article.articleCover = res.data
      message.success('封面上传成功')
    } else {
      message.error(res.message || '封面上传失败')
    }
    onFinish()
  }).catch(err => {
    console.error('封面上传失败:', err)
    message.error('封面上传失败')
    onError()
  })
}

// 处理标题输入
function handleTitleInput() {
  // 可以在这里添加自动保存等逻辑
}

// 上传图片到编辑器
async function handleUploadImg(files, callback) {
  const uploadPromises = files.map(file => {
    const formData = new FormData()
    formData.append('file', file)

    return request.post('/api/admin/articles/images', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }).then(res => {
      if (res.flag) {
        return res.data
      } else {
        message.error(res.message || '图片上传失败')
        return null
      }
    }).catch(err => {
      console.error('图片上传失败:', err)
      message.error('图片上传失败')
      return null
    })
  })

  try {
    const urls = await Promise.all(uploadPromises)
    callback(urls.map(url => url || ''))
  } catch (error) {
    console.error('上传图片失败:', error)
  }
}

// 保存分类（回车时添加自定义分类）
function saveCategory() {
  const input = categoryName.value?.trim()
  if (!input) return

  // 直接使用用户输入的值
  article.categoryName = input
  categoryName.value = ''
}

function addCategory(item) {
  article.categoryName = item.categoryName
}

function removeCategory() {
  article.categoryName = null
}

// 保存标签（回车时添加自定义标签）
function saveTag() {
  const input = tagName.value?.trim()
  if (!input) return

  // 避免重复添加
  if (!article.tagNames.includes(input)) {
    article.tagNames.push(input)
  }
  tagName.value = ''
}

function addTag(item) {
  if (!article.tagNames.includes(item.tagName)) {
    article.tagNames.push(item.tagName)
  }
}

function removeTag(tag) {
  const index = article.tagNames.indexOf(tag)
  if (index > -1) {
    article.tagNames.splice(index, 1)
  }
}

function isTagSelected(tag) {
  return article.tagNames.includes(tag.tagName)
}

function toggleTag(tag, checked) {
  if (checked) {
    addTag(tag)
  } else {
    removeTag(tag.tagName)
  }
}

// 保存草稿
function saveArticleDraft() {
  if (!article.articleTitle?.trim()) {
    message.error('文章标题不能为空')
    return
  }
  if (!article.articleContent?.trim()) {
    message.error('文章内容不能为空')
    return
  }

  article.status = 3
  saveArticle()
}

// 发表文章
function saveOrUpdateArticle() {
  if (!article.articleTitle?.trim()) {
    message.error('文章标题不能为空')
    return
  }
  if (!article.articleContent?.trim()) {
    message.error('文章内容不能为空')
    return
  }
  if (!article.categoryName) {
    message.error('文章分类不能为空')
    return
  }
  if (article.tagNames.length === 0) {
    message.error('文章标签不能为空')
    return
  }
  if (!article.articleCover?.trim()) {
    message.error('文章封面不能为空')
    return
  }

  saveArticle()
  showPublishModal.value = false
}

// 保存文章
async function saveArticle() {
  try {
    const res = await request.post('/api/admin/articles', article)
    if (res.flag) {
      message.success(res.message || '保存成功')
      sessionStorage.removeItem('article')
      resetArticle()
      router.push('/article-list')
    } else {
      message.error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存文章失败:', error)
    message.error('保存失败')
  }
}

// 自动保存文章
function autoSaveArticle() {
  if (
    autoSave.value &&
    article.articleTitle?.trim() &&
    article.articleContent?.trim()
  ) {
    if (article.id) {
      // 已发布的文章，自动保存到服务器
      saveArticle()
    } else {
      // 新文章，保存到sessionStorage
      sessionStorage.setItem('article', JSON.stringify(article))
    }
  }
}

onMounted(() => {
  fetchArticle()
})

onBeforeUnmount(() => {
  autoSaveArticle()
})
</script>

<style scoped>
/* 文章标题栏样式 */
.article-header {
  background: #fafafa;
  border-radius: 8px;
  padding: 20px;
}

.title-row {
  display: flex;
  gap: 16px;
  align-items: center;
}

.title-input {
  flex: 1;
}

.title-input :deep(.n-input__input-el) {
  font-size: 16px;
  font-weight: 500;
}

.button-group {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.markdown-editor-container {
  height: calc(100vh - 200px);
  min-height: 600px;
}

.cover-preview {
  position: relative;
  display: inline-block;
}

.cover-preview img {
  max-width: 360px;
  max-height: 180px;
  border-radius: 4px;
}

.cover-preview .n-button {
  position: absolute;
  top: 8px;
  right: 8px;
}

.popover-container {
  max-height: 260px;
  overflow-y: auto;
}

.category-item {
  cursor: pointer;
  padding: 0.6rem 0.5rem;
}

.category-item:hover {
  background-color: #f0f9eb;
  color: #67c23a;
}

/* 降低markdown编辑器工具栏的层级，避免遮挡弹窗 */
:deep(.md-editor) {
  z-index: 1;
}

:deep(.md-editor-toolbar) {
  z-index: 1;
}

:deep(.md-editor-modal) {
  z-index: 2;
}

/* 必填项标签样式 */
.required-label::before {
  content: '* ';
  color: #d03050;
}
</style>
