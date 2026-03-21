<template>
  <div class="app-main-container">
    <n-tabs
      v-model:value="activeTab"
      type="card"
      closable
      @close="handleClose"
      @update:value="handleTabChange"
    >
      <n-tab
        v-for="tab in appStore.tabList"
        :key="tab.path"
        :name="tab.path"
        @contextmenu.prevent="handleContextMenu($event, tab)"
      >
        {{ tab.title }}
      </n-tab>
    </n-tabs>

    <!-- 右键菜单 -->
    <n-dropdown
      placement="bottom-start"
      trigger="manual"
      :x="contextMenuX"
      :y="contextMenuY"
      :options="contextMenuOptions"
      :show="showContextMenu"
      :on-clickoutside="() => showContextMenu = false"
      @select="handleContextMenuSelect"
    />

    <div class="content-container">
      <router-view v-slot="{ Component }">
        <transition name="fade-transform" mode="out-in">
          <keep-alive>
            <component :is="Component" :key="route.path" v-if="!refreshing" />
          </keep-alive>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup>
import { watch, onMounted, ref, computed, h, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '@/store/modules/app'
import { NIcon } from 'naive-ui'
import {
  RefreshOutline,
  CloseOutline,
  CloseCircleOutline
} from '@vicons/ionicons5'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()

const activeTab = computed({
  get: () => appStore.activeTab,
  set: (val) => {
    appStore.setActiveTab(val)
  }
})

// 右键菜单相关
const showContextMenu = ref(false)
const contextMenuX = ref(0)
const contextMenuY = ref(0)
const currentContextTab = ref(null)
const refreshing = ref(false)

// 右键菜单选项
const contextMenuOptions = [
  {
    label: '刷新',
    key: 'refresh',
    icon: () => h(NIcon, null, { default: () => h(RefreshOutline) })
  },
  {
    label: '关闭',
    key: 'close',
    icon: () => h(NIcon, null, { default: () => h(CloseOutline) })
  },
  {
    label: '关闭其他',
    key: 'closeOthers',
    icon: () => h(NIcon, null, { default: () => h(CloseCircleOutline) })
  },
  {
    label: '关闭所有',
    key: 'closeAll',
    icon: () => h(NIcon, null, { default: () => h(CloseCircleOutline) })
  }
]

// 右键菜单事件
const handleContextMenu = (e, tab) => {
  e.preventDefault()
  currentContextTab.value = tab
  contextMenuX.value = e.clientX
  contextMenuY.value = e.clientY
  showContextMenu.value = true
}

// 右键菜单选择
const handleContextMenuSelect = (key) => {
  showContextMenu.value = false
  const tab = currentContextTab.value
  if (!tab) return

  switch (key) {
    case 'refresh':
      refreshPage(tab.path)
      break
    case 'close':
      handleClose(tab.path)
      break
    case 'closeOthers':
      closeOtherTabs(tab.path)
      break
    case 'closeAll':
      closeAllTabs()
      break
  }
}

// 刷新页面
const refreshPage = (path) => {
  if (path === route.path) {
    refreshing.value = true
    nextTick(() => {
      refreshing.value = false
    })
  }
}

// 关闭其他标签
const closeOtherTabs = (path) => {
  appStore.tabList = appStore.tabList.filter(tab => tab.path === path)
  appStore.setActiveTab(path)
  if (route.path !== path) {
    router.push(path)
  }
}

// 关闭所有标签
const closeAllTabs = () => {
  appStore.clearTabs()
  router.push('/home')
  appStore.addTab({
    path: '/home',
    title: '首页'
  })
  appStore.setActiveTab('/home')
}

// 关闭标签
const handleClose = (path) => {
  appStore.removeTab(path)

  // 如果关闭的是当前标签，跳转到前一个或后一个
  if (path === route.path) {
    const tabs = appStore.tabList
    if (tabs.length > 0) {
      router.push(tabs[tabs.length - 1].path)
    } else {
      router.push('/')
    }
  }
}

// 标签切换
const handleTabChange = (path) => {
  router.push(path)
}

// 监听路由变化，添加标签
watch(
  () => route.path,
  (path) => {
    if (route.meta?.title) {
      appStore.addTab({
        path,
        title: route.meta.title
      })
      appStore.setActiveTab(path)
    }
  },
  { immediate: true }
)

onMounted(() => {
  // 初始化时添加当前路由到标签
  if (route.meta?.title) {
    appStore.addTab({
      path: route.path,
      title: route.meta.title
    })
    appStore.setActiveTab(route.path)
  }
})
</script>

<style scoped>
.app-main-container {
  display: flex;
  flex-direction: column;
  background: #f5f6fb;
}

.app-main-container :deep(.n-tabs) {
  padding: 8px 16px 0;
  background: #fff;
}

.content-container {
  padding: 16px;
}

/* 页面切换动画 */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
