<template>
  <div class="sidebar-container">
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="210"
      :collapsed="appStore.collapse"
      show-trigger="arrow-circle"
      @collapse="toggleCollapse"
      @expand="toggleCollapse"
    >
      <!-- Logo -->
      <div class="logo-container">
        <transition name="fade">
          <img v-if="websiteStore.getLogo" :src="websiteStore.getLogo" :class="appStore.collapse ? 'logo-image-mini' : 'logo-image'" alt="logo" />
          <h1 v-else-if="!appStore.collapse" class="logo-text">{{ websiteStore.getName }}</h1>
          <h1 v-else class="logo-text-mini">{{ websiteStore.getName.charAt(0) }}</h1>
        </transition>
      </div>

      <!-- 菜单 -->
      <n-menu
        v-model:value="activeKey"
        :collapsed="appStore.collapse"
        :collapsed-width="64"
        :collapsed-icon-size="22"
        :options="menuOptions"
        @update:value="handleMenuSelect"
      />
    </n-layout-sider>
  </div>
</template>

<script setup>
import { ref, computed, h, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NIcon } from 'naive-ui'
import { useMessage } from 'naive-ui'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import { useWebsiteStore } from '@/store/modules/website'
import { getUserMenusApi } from '@/api/menu'
import {
  HomeOutline,
  DocumentTextOutline,
  ChatbubblesOutline,
  FolderOutline,
  OptionsOutline,
  PeopleOutline,
  SettingsOutline,
  LogOutOutline
} from '@vicons/ionicons5'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()
const websiteStore = useWebsiteStore()
const message = useMessage()

const activeKey = ref(null)
const menuOptions = ref([])

// 图标映射
const iconMap = {
  home: () => h(NIcon, null, { default: () => h(HomeOutline) }),
  article: () => h(NIcon, null, { default: () => h(DocumentTextOutline) }),
  comment: () => h(NIcon, null, { default: () => h(ChatbubblesOutline) }),
  category: () => h(NIcon, null, { default: () => h(FolderOutline) }),
  tag: () => h(NIcon, null, { default: () => h(OptionsOutline) }),
  user: () => h(NIcon, null, { default: () => h(PeopleOutline) }),
  users: () => h(NIcon, null, { default: () => h(PeopleOutline) }),
  settings: () => h(NIcon, null, { default: () => h(SettingsOutline) }),
  system: () => h(NIcon, null, { default: () => h(SettingsOutline) }),
  logout: () => h(NIcon, null, { default: () => h(LogOutOutline) }),
  role: () => h(NIcon, null, { default: () => h(PeopleOutline) }),
  permission: () => h(NIcon, null, { default: () => h(PeopleOutline) }),
  menu: () => h(NIcon, null, { default: () => h(OptionsOutline) }),
  album: () => h(NIcon, null, { default: () => h(FolderOutline) }),
  log: () => h(NIcon, null, { default: () => h(DocumentTextOutline) }),
  talk: () => h(NIcon, null, { default: () => h(ChatbubblesOutline) }),
  message: () => h(NIcon, null, { default: () => h(ChatbubblesOutline) }),
  job: () => h(NIcon, null, { default: () => h(DocumentTextOutline) }),
  quartz: () => h(NIcon, null, { default: () => h(DocumentTextOutline) })
}

// 获取菜单数据
const fetchMenus = async () => {
  try {
    const res = await getUserMenusApi()

    // 扁平化处理菜单数据
    const flattenMenuItems = (items, parentPath = '') => {
      const result = []

      for (const item of items) {
        // 跳过隐藏菜单
        if (item.isHidden === 1) continue

        // 如果有子菜单，递归处理
        if (item.children && item.children.length > 0) {
          // 构建当前项的完整路径
          let currentPath = item.path || ''
          if (currentPath && !currentPath.startsWith('/')) {
            currentPath = parentPath + '/' + currentPath
          } else if (!currentPath) {
            currentPath = parentPath
          }

          const childItems = flattenMenuItems(item.children, currentPath)
          result.push(...childItems)
        } else if (item.name && item.name.trim() !== '') {
          // 只处理有名称的叶子节点
          let path = item.path || ''

          // 处理空路径，继承父路径
          if (!path) {
            path = parentPath
          }

          // 确保路径以 / 开头
          if (path && !path.startsWith('/')) {
            path = '/' + path
          }

          // 过滤掉通配符路由和动态路由
          if (path.includes(':') || path.includes('*')) {
            continue
          }

          // 过滤掉 submenu 结尾的路径
          if (path.endsWith('-submenu')) {
            continue
          }

          // 路径映射 - 将后端路径映射到前端路由
          const pathMapping = {
            '/talk-list': '/talks',
            '/online/users': '/online',
            '/links': '/friendlinks',
            '/photos/delete': '/albums/delete',
            '/operation/log': '/logs',
            '/exception/log': '/logs',
            '/quartz/log': '/logs',
            '/article-list': '/article-list',
            '/articles': '/articles',
            '/': '/home'  // 首页映射
          }

          // 使用映射后的路径
          const finalPath = pathMapping[path] || path

          result.push({
            label: item.name,
            key: finalPath,
            icon: getIcon(item.icon)
          })
        }
      }

      return result
    }

    // 处理菜单数据
    let menus = flattenMenuItems(res.data || [])

    // 去重：基于 key 去重
    const uniqueMenus = []
    const seenKeys = new Set()

    for (const menu of menus) {
      if (!seenKeys.has(menu.key)) {
        seenKeys.add(menu.key)
        uniqueMenus.push(menu)
      }
    }

    menus = uniqueMenus

    // 检查是否有首页
    const hasHomePage = menus.some(item =>
      item.key === '/' || item.key === '/home' || item.label === '首页' || item.label === '工作台'
    )

    // 如果没有首页，才添加
    if (!hasHomePage) {
      menus.unshift({
        label: '首页',
        key: '/home',
        icon: iconMap.home
      })
    }

    // 检查是否有文章列表和发布文章菜单
    const hasArticleList = menus.some(item =>
      item.key === '/article-list' || item.label === '文章列表' || item.label === '文章管理'
    )

    const hasArticleEdit = menus.some(item =>
      item.key === '/articles' || item.label === '发布文章' || item.label === '文章'
    )

    // 如果有文章列表但没有发布文章，在文章列表后面添加发布文章
    if (hasArticleList && !hasArticleEdit) {
      const articleListIndex = menus.findIndex(item =>
        item.key === '/article-list' || item.label === '文章列表' || item.label === '文章管理'
      )
      if (articleListIndex > -1) {
        menus.splice(articleListIndex, 0, {
          label: '发布文章',
          key: '/articles',
          icon: iconMap.article
        })
      }
    }

    // 如果只有发布文章没有文章列表，添加文章列表
    if (hasArticleEdit && !hasArticleList) {
      const articleEditIndex = menus.findIndex(item =>
        item.key === '/articles' || item.label === '发布文章'
      )
      if (articleEditIndex > -1) {
        menus.splice(articleEditIndex + 1, 0, {
          label: '文章列表',
          key: '/article-list',
          icon: iconMap.article
        })
      }
    }

    // 添加退出登录
    menus.push({
      label: '退出登录',
      key: 'logout',
      icon: iconMap.logout
    })

    menuOptions.value = menus

  } catch (error) {
    console.error('获取菜单失败:', error)

    // 使用默认菜单
    menuOptions.value = [
      {
        label: '首页',
        key: '/home',
        icon: iconMap.home
      },
      {
        label: '发布文章',
        key: '/articles',
        icon: iconMap.article
      },
      {
        label: '文章列表',
        key: '/article-list',
        icon: iconMap.article
      },
      {
        label: '分类管理',
        key: '/categories',
        icon: iconMap.category
      },
      {
        label: '标签管理',
        key: '/tags',
        icon: iconMap.tag
      },
      {
        label: '评论管理',
        key: '/comments',
        icon: iconMap.comment
      },
      {
        label: '用户管理',
        key: '/users',
        icon: iconMap.user
      },
      {
        label: '角色管理',
        key: '/roles',
        icon: iconMap.role
      },
      {
        label: '退出登录',
        key: 'logout',
        icon: iconMap.logout
      }
    ]
  }
}

// 获取图标函数
const getIcon = (iconName) => {
  if (!iconName) return iconMap.article

  // 移除 el-icon- 前缀
  const cleanIconName = iconName.replace('el-icon-my', '').replace('el-icon-', '')

  // 简单的图标映射逻辑
  const iconMapping = {
    'shouye': 'home',
    'wenzhang': 'article',
    'fabiaowenzhang': 'article',
    'wenzhangliebiao': 'article',
    'fenlei': 'category',
    'icontag': 'tag',
    'xiaoxi': 'comment',
    'pinglun': 'comment',
    'pinglunzu': 'comment',
    'yonghuliebiao': 'user',
    'jiaoseliebiao': 'role',
    'shezhi': 'settings',
    'xitong': 'system',
    'caidan': 'menu',
    'zhaopian': 'album',
    'guanyuwo': 'user',
    'user': 'user',
    'setting': 'settings'
  }

  const mappedIcon = iconMapping[cleanIconName] || cleanIconName
  return iconMap[mappedIcon] || iconMap.article
}

// 菜单选择
const handleMenuSelect = (key) => {
  if (key === 'logout') {
    handleLogout()
    return
  }

  // 处理根路径，重定向到首页
  if (key === '/') {
    key = '/home'
  }

  activeKey.value = key

  // 添加标签页
  const menu = menuOptions.value.find(item => item.key === key)
  if (menu) {
    appStore.addTab({
      path: key,
      title: menu.label
    })
    appStore.setActiveTab(key)
  }

  // 调试：打印路由跳转
  router.push(key).catch(err => {
    console.error('路由跳转失败:', err)
    message.error('页面不存在: ' + key)
  })
}

// 退出登录
const handleLogout = async () => {
  const userStore = useUserStore()
  await userStore.logout()
  message.success('已退出登录')
  // 使用 window.location.href 直接跳转，避免路由守卫和其他请求
  window.location.href = '/login'
}

// 切换折叠状态
const toggleCollapse = () => {
  appStore.toggleCollapse()
}

// 获取网站配置
const fetchWebsiteConfig = async () => {
  try {
    await websiteStore.fetchWebsiteConfig()
  } catch (error) {
    // 对于40001（未登录）错误，静默处理
    if (error.code !== 40001) {
      console.error('获取网站配置失败:', error)
    }
  }
}

// 更新当前激活菜单
const updateActiveMenu = () => {
  activeKey.value = route.path
}

onMounted(() => {
  fetchMenus()
  updateActiveMenu()
  fetchWebsiteConfig()
})

// 监听路由变化
router.afterEach(() => {
  updateActiveMenu()
})
</script>

<style scoped>
.sidebar-container {
  height: 100%;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.sidebar-container :deep(.n-layout-sider) {
  height: 100%;
}

.sidebar-container :deep(.n-layout-sider-scroll-container) {
  overflow-y: auto;
  overflow-x: hidden;
  /* 隐藏滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}

.sidebar-container :deep(.n-layout-sider-scroll-container::-webkit-scrollbar) {
  display: none; /* Chrome/Safari/Opera */
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.logo-image {
  max-width: 150px;
  max-height: 50px;
  object-fit: contain;
}

.logo-image-mini {
  max-width: 32px;
  max-height: 32px;
  object-fit: contain;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin: 0;
}

.logo-text-mini {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin: 0;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
