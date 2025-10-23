import apiClient from './client'
import type { User } from '@/types/api'

export const userApi = {
  // 获取用户信息
  async getUser(id: number) {
    const response = await apiClient.get<User>(`/users/${id}`)
    return response
  },

  // 更新当前用户信息
  async updateCurrentUser(data: { nickname?: string; avatarUrl?: string }) {
    const response = await apiClient.patch<User>('/users/me', data)
    return response
  },

  // 根据用户名获取用户信息
  async getUserByUsername(username: string) {
    const response = await apiClient.get<User>(`/users/username/${username}`)
    return response
  },
}