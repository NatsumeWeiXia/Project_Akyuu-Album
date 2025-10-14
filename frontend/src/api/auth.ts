import apiClient from './client'
import type { User, LoginRequest, RegisterRequest } from '@/types/api'

export const authApi = {
  // 用户登录
  async login(data: LoginRequest) {
    const response = await apiClient.post<{ token: string; user: User }>('/auth/login', data)
    return response.data!
  },

  // 用户注册
  async register(data: RegisterRequest) {
    const response = await apiClient.post<{ token: string; user: User }>('/auth/register', data)
    return response.data!
  },

  // 获取当前用户信息
  async getCurrentUser() {
    const response = await apiClient.get<User>('/auth/me')
    return response.data!
  }
}