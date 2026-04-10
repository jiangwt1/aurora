<template>
  <div class="layout-container">
    <!-- 侧边栏 - 固定定位 -->
    <aside class="sidebar" :class="{ collapsed: appStore.collapse }">
      <Sidebar />
    </aside>

    <!-- 主内容区 - 带左边距 -->
    <main class="main-content" :class="{ collapsed: appStore.collapse }">
      <Navbar />
      <AppMain />
    </main>
  </div>
</template>

<script setup>
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'
import AppMain from './components/AppMain.vue'
import { useAppStore } from '@/store/modules/app'
import { onMounted } from 'vue'

const appStore = useAppStore()

onMounted(() => {
  appStore.collapse = false
})
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 210px;
  z-index: 100;
  transition: width 0.3s;
  overflow: visible;
}

.sidebar.collapsed {
  width: 64px;
}

.main-content {
  margin-left: 210px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f5f6fb;
  transition: margin-left 0.3s;
}

.main-content.collapsed {
  margin-left: 64px;
}
</style>
