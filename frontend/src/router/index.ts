import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/albums/public',
      name: 'public-albums',
      component: () => import('@/views/albums/PublicAlbumsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/albums/mine',
      name: 'my-albums',
      component: () => import('@/views/albums/MyAlbumsView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/albums/:id',
      name: 'album-detail',
      component: () => import('@/views/albums/AlbumDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/media/:id',
      name: 'media-detail',
      component: () => import('@/views/media/MediaDetailView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/profile/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/api-test',
      name: 'api-test',
      component: () => import('@/components/ApiTester.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 确保用户状态已初始化
  if (!userStore.user && localStorage.getItem('token')) {
    try {
      await userStore.fetchCurrentUser()
    } catch (error) {
      // 获取用户信息失败，清除token
      userStore.clearToken()
    }
  }

  // 需要认证的路由
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
    return
  }

  // 需要游客身份的路由（登录/注册页）
  if (to.meta.requiresGuest && userStore.isLoggedIn) {
    next('/')
    return
  }

  next()
})

export default router
