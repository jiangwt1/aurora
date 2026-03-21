<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1>404</h1>
      <p>抱歉，您访问的页面不存在</p>
      <n-button type="primary" @click="goHome">
        返回首页
      </n-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f6fb;
}

.not-found-content {
  text-align: center;
}

.not-found-content h1 {
  font-size: 120px;
  color: #409EFF;
  margin-bottom: 20px;
  font-weight: bold;
}

.not-found-content p {
  font-size: 18px;
  color: #666;
  margin-bottom: 30px;
}
</style>
