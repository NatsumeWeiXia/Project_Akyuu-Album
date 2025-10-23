import { defineStore } from 'pinia'
import { ref, computed, readonly } from 'vue'
import type { User } from '@/types/api'
import { authApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // 设置token
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 清除token
  const clearToken = () => {
    token.value = null
    localStorage.removeItem('token')
  }

  // 设置用户信息
  const setUser = (userData: User) => {
    user.value = userData
  }

  // 清除用户信息
  const clearUser = () => {
    user.value = null
  }

  // 登录
  const login = async (username: string, password: string) => {
    try {
      const response = await authApi.login({ username, password })
      setToken(response.token)
      setUser(response.user)
      return response
    } catch (error) {
      clearToken()
      clearUser()
      throw error
    }
  }

  // 注册
  const register = async (username: string, password: string, nickname: string, avatarUrl?: string) => {
    try {
      const response = await authApi.register({ username, password, nickname, avatarUrl })
      setToken(response.token)
      setUser(response.user)
      return response
    } catch (error) {
      clearToken()
      clearUser()
      throw error
    }
  }

  // 获取当前用户信息
  const fetchCurrentUser = async () => {
    try {
      const userData = await authApi.getCurrentUser()
      setUser(userData)
      return userData
    } catch (error) {
      clearToken()
      clearUser()
      throw error
    }
  }

  // 更新当前用户信息
  const updateCurrentUser = async (userData: Partial<User>) => {
    loading.value = true
    try {
      const response = await authApi.updateCurrentUser(userData)
      setUser(response.user)
      return response
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = () => {
    clearToken()
    clearUser()
  }

  // 初始化用户信息
  const initializeAuth = async () => {
    if (token.value) {
      try {
        await fetchCurrentUser()
      } catch  {
        // 如果获取用户信息失败，清除token
        clearToken()
      }
    }
  }

  return {
    user: readonly(user),
    token: readonly(token),
    loading: readonly(loading),
    isLoggedIn,
    isAdmin,
    setToken,
    clearToken,
    setUser,
    clearUser,
    login,
    register,
    fetchCurrentUser,
    updateCurrentUser,
    logout,
    initializeAuth
  }
})