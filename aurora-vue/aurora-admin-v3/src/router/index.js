import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置 NProgress
NProgress.configure({
  easing: 'ease',
  speed: 500,
  showSpinner: false,
  trickleSpeed: 200,
  minimum: 0.3
})

// 基础路由
const routes = [
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/home',
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: {
          title: '首页',
          requiresAuth: true
        }
      },
      {
        path: '/',
        redirect: '/home'
      },
      {
        path: '/articles',
        name: 'ArticleEdit',
        component: () => import('@/views/article/ArticleEdit.vue'),
        meta: {
          title: '发布文章',
          requiresAuth: true
        }
      },
      {
        path: '/articles/:id',
        name: 'ArticleEditWithId',
        component: () => import('@/views/article/ArticleEdit.vue'),
        meta: {
          title: '编辑文章',
          requiresAuth: true
        }
      },
      {
        path: '/article-list',
        name: 'ArticleList',
        component: () => import('@/views/article/ArticleList.vue'),
        meta: {
          title: '文章列表',
          requiresAuth: true
        }
      },
      {
        path: '/article-submenu',
        redirect: '/article-list'
      },
      {
        path: '/categories',
        name: 'CategoryList',
        component: () => import('@/views/category/CategoryList.vue'),
        meta: {
          title: '分类管理',
          requiresAuth: true
        }
      },
      {
        path: '/category',
        redirect: '/categories'
      },
      {
        path: '/category-submenu',
        redirect: '/categories'
      },
      {
        path: '/tags',
        name: 'TagList',
        component: () => import('@/views/tag/TagList.vue'),
        meta: {
          title: '标签管理',
          requiresAuth: true
        }
      },
      {
        path: '/tag',
        redirect: '/tags'
      },
      {
        path: '/tag-submenu',
        redirect: '/tags'
      },
      {
        path: '/comments',
        name: 'CommentList',
        component: () => import('@/views/comment/CommentList.vue'),
        meta: {
          title: '评论管理',
          requiresAuth: true
        }
      },
      {
        path: '/comment',
        redirect: '/comments'
      },
      {
        path: '/message-submenu',
        redirect: '/comments'
      },
      {
        path: '/users',
        name: 'UserList',
        component: () => import('@/views/user/UserList.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true
        }
      },
      {
        path: '/user',
        redirect: '/users'
      },
      {
        path: '/users-submenu',
        redirect: '/users'
      },
      {
        path: '/online',
        name: 'OnlineUsers',
        component: () => import('@/views/user/Online.vue'),
        meta: {
          title: '在线用户',
          requiresAuth: true
        }
      },
      {
        path: '/roles',
        name: 'RoleList',
        component: () => import('@/views/role/RoleList.vue'),
        meta: {
          title: '角色管理',
          requiresAuth: true
        }
      },
      {
        path: '/role',
        redirect: '/roles'
      },
      {
        path: '/role-submenu',
        redirect: '/roles'
      },
      {
        path: '/quartz',
        name: 'QuartzList',
        component: () => import('@/views/quartz/QuartzList.vue'),
        meta: {
          title: '定时任务管理',
          requiresAuth: true
        }
      },
      {
        path: '/jobs',
        redirect: '/quartz'
      },
      {
        path: '/menus',
        name: 'MenuList',
        component: () => import('@/views/menu/MenuList.vue'),
        meta: {
          title: '菜单管理',
          requiresAuth: true
        }
      },
      {
        path: '/menu',
        redirect: '/menus'
      },
      {
        path: '/menu-submenu',
        redirect: '/menus'
      },
      {
        path: '/friendlinks',
        name: 'FriendLinkList',
        component: () => import('@/views/friendLink/FriendLinkList.vue'),
        meta: {
          title: '友链管理',
          requiresAuth: true
        }
      },
      {
        path: '/links',
        redirect: '/friendlinks'
      },
      {
        path: '/website',
        name: 'Website',
        component: () => import('@/views/website/Website.vue'),
        meta: {
          title: '网站配置',
          requiresAuth: true
        }
      },
      {
        path: '/resources',
        name: 'ResourceList',
        component: () => import('@/views/resource/ResourceList.vue'),
        meta: {
          title: '资源权限管理',
          requiresAuth: true
        }
      },
      {
        path: '/settings',
        name: 'Setting',
        component: () => import('@/views/setting/Setting.vue'),
        meta: {
          title: '个人中心',
          requiresAuth: true
        }
      },
      {
        path: '/setting',
        redirect: '/settings'
      },
      {
        path: '/setting-submenu',
        redirect: '/settings'
      },
      {
        path: '/systems',
        redirect: '/settings'
      },
      {
        path: '/system-submenu',
        redirect: '/settings'
      },
      {
        path: '/logs',
        name: 'LogList',
        component: () => import('@/views/log/LogList.vue'),
        meta: {
          title: '日志管理',
          requiresAuth: true
        }
      },
      {
        path: '/log',
        redirect: '/logs'
      },
      {
        path: '/log-submenu',
        redirect: '/logs'
      },
      {
        path: '/exception-log',
        redirect: '/logs'
      },
      {
        path: '/operation-log',
        redirect: '/logs'
      },
      {
        path: '/quartz-log',
        redirect: '/logs'
      },
      {
        path: '/talks',
        name: 'TalkList',
        component: () => import('@/views/talk/TalkList.vue'),
        meta: {
          title: '说说管理',
          requiresAuth: true
        }
      },
      {
        path: '/talks/new',
        name: 'TalkNew',
        component: () => import('@/views/talk/TalkEdit.vue'),
        meta: {
          title: '发布说说',
          requiresAuth: true
        }
      },
      {
        path: '/talks/:id',
        name: 'TalkEdit',
        component: () => import('@/views/talk/TalkEdit.vue'),
        meta: {
          title: '编辑说说',
          requiresAuth: true
        }
      },
      {
        path: '/talk-submenu',
        redirect: '/talks'
      },
      {
        path: '/albums',
        name: 'AlbumList',
        component: () => import('@/views/album/AlbumList.vue'),
        meta: {
          title: '相册管理',
          requiresAuth: true
        }
      },
      {
        path: '/albums/:id',
        name: 'PhotoList',
        component: () => import('@/views/album/PhotoList.vue'),
        meta: {
          title: '照片列表',
          requiresAuth: true
        }
      },
      {
        path: '/album',
        redirect: '/albums'
      },
      {
        path: '/permissions',
        redirect: '/roles'
      },
      {
        path: '/permission-submenu',
        redirect: '/roles'
      },
      {
        path: '/userss',
        redirect: '/users'
      },
      // 缺失的路由重定向
      {
        path: '/talk-list',
        redirect: '/talks'
      },
      {
        path: '/online/users',
        redirect: '/online'
      },
      {
        path: '/links',
        redirect: '/friendlinks'
      },
      {
        path: '/photos/delete',
        redirect: '/albums'
      },
      {
        path: '/albums/delete',
        name: 'PhotoDelete',
        component: () => import('@/views/album/Delete.vue'),
        meta: {
          title: '照片回收站',
          requiresAuth: true
        }
      },
      {
        path: '/operation/log',
        redirect: '/logs'
      },
      {
        path: '/exception/log',
        redirect: '/logs'
      },
      {
        path: '/quartz/log/:quartzId',
        redirect: '/logs'
      },
      {
        path: '/about',
        name: 'About',
        component: () => import('@/views/about/About.vue'),
        meta: {
          title: '关于',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      requiresAuth: false
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404'
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory('/'),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  NProgress.start()

  // 登录页和404页不需要登录
  const publicPages = ['/login', '/404']
  if (publicPages.includes(to.path)) {
    next()
    return
  }

  // 检查登录状态
  const token = sessionStorage.getItem('token')
  if (!token) {
    next({ path: '/login' })
    return
  }

  next()
})

router.afterEach(() => {
  NProgress.done()
})

// 设置路由
export async function setupRouter(app) {
  app.use(router)
  return router
}

export default router
