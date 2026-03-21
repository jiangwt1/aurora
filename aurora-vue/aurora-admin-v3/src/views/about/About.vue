<template>
  <AppPage>
    <n-card title="关于我" rounded-10>
      <n-space vertical size="large">
        <n-alert v-if="showImageAlert" type="warning" closable @close="showImageAlert = false">
          检测到内容中有图片加载失败，建议检查并更新图片链接
        </n-alert>

        <div class="markdown-editor-container">
          <MdEditor
            v-model="aboutContent"
            :theme="theme"
            :preview-theme="previewTheme"
            :code-theme="codeTheme"
            @onUploadImg="handleUploadImg"
            style="height: 100%"
            @onHtmlChanged="handleHtmlChanged"
          />
        </div>

        <n-space justify="end">
          <n-button type="primary" @click="updateAbout">
            修改
          </n-button>
        </n-space>
      </n-space>
    </n-card>
  </AppPage>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useMessage } from 'naive-ui'
import { request } from '@/utils/http'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const message = useMessage()

const aboutContent = ref('')
const showImageAlert = ref(false)

// 主题配置
const theme = ref('light')
const previewTheme = ref('default')
const codeTheme = ref('atom')

function getAbout() {
  request.get('/api/about').then(res => {
    aboutContent.value = res.data.content || ''
    // 检查内容中的图片链接
    nextTick(() => {
      checkImageErrors()
    })
  }).catch(err => {
    console.error('获取关于我内容失败:', err)
    message.error('获取关于我内容失败')
  })
}

function updateAbout() {
  request.put('/api/admin/about', {
    content: aboutContent.value
  }).then(res => {
    message.success('修改成功')
    showImageAlert.value = false
  }).catch(err => {
    console.error('修改失败:', err)
    message.error('修改失败')
  })
}

// 上传图片
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
    const validUrls = urls.filter(url => url !== null)
    callback(validUrls)
  } catch (error) {
    console.error('上传图片失败:', error)
  }
}

// 检查Markdown预览中的图片错误
function checkImageErrors() {
  const previewElement = document.querySelector('.md-editor-preview')
  if (!previewElement) return

  const images = previewElement.querySelectorAll('img')
  let hasError = false

  images.forEach(img => {
    // 检查图片是否已经加载失败
    if (img.naturalWidth === 0 && !img.complete) {
      hasError = true
    }

    // 监听图片加载错误
    img.addEventListener('error', function() {
      hasError = true
      showImageAlert.value = true
      console.warn('图片加载失败:', this.src)
    })
  })

  if (hasError) {
    showImageAlert.value = true
  }
}

// 当HTML内容变化时检查图片
function handleHtmlChanged() {
  nextTick(() => {
    checkImageErrors()
  })
}

onMounted(() => {
  getAbout()
})
</script>

<style scoped>
.markdown-editor-container {
  height: calc(100vh - 200px);
  min-height: 600px;
}

/* 为加载失败的图片添加占位符样式 */
.markdown-editor-container :deep(.md-editor-preview img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  transition: all 0.3s ease;
}

/* 图片加载失败时的样式 */
.markdown-editor-container :deep(.md-editor-preview img[alt*="错误"]),
.markdown-editor-container :deep(.md-editor-preview img[alt*="加载失败"]) {
  opacity: 0.5;
  border: 2px dashed #ff4d4f;
  padding: 20px;
  background-color: #fff1f0;
}
</style>

