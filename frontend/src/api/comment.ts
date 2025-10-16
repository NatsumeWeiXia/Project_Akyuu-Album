import apiClient from './client'
import type { Comment, PageResponse } from '@/types/api'

export const commentApi = {
  // 创建评论
  async createComment(mediaId: number, content: string, parentId?: number) {
    const data = { content, parentId }
    const response = await apiClient.post<Comment>(`/media/${mediaId}/comments`, data)
    return response.data!
  },

  // 获取媒体评论列表
  async getMediaComments(mediaId: number) {
    const response = await apiClient.get<Comment[]>(`/media/${mediaId}/comments`)
    return response.data!
  },

  // 更新评论
  async updateComment(id: number, content: string) {
    const response = await apiClient.patch<Comment>(`/comments/${id}`, { content })
    return response.data!
  },

  // 删除评论
  async deleteComment(id: number) {
    const response = await apiClient.delete<void>(`/comments/${id}`)
    return response.data!
  },

  // 获取评论数量
  async getCommentCount(mediaId: number) {
    const response = await apiClient.get<number>(`/media/${mediaId}/comments/count`)
    return response.data!
  },

  // 获取用户评论
  async getUserComments(userId: number) {
    const response = await apiClient.get<Comment[]>(`/users/${userId}/comments`)
    return response.data!
  }
}