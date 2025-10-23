import apiClient from './client'
import type { Comment } from '@/types/api'

export const commentApi = {
  // 创建评论
  async createComment(mediaId: number, content: string, parentId?: number) {
    const data = { content, parentId }
    const response = await apiClient.post<Comment>(`/comments/media/${mediaId}`, data)
    return response
  },

  // 获取媒体评论列表
  async getMediaComments(mediaId: number) {
    const response = await apiClient.get<Comment[]>(`/comments/media/${mediaId}`)
    return response
  },

  // 更新评论
  async updateComment(id: number, content: string) {
    const response = await apiClient.put<Comment>(`/comments/${id}`, { content })
    return response
  },

  // 删除评论
  async deleteComment(id: number) {
    const response = await apiClient.delete<void>(`/comments/${id}`)
    return response
  },

  // 获取评论数量
  async getCommentCount(mediaId: number) {
    const response = await apiClient.get<number>(`/comments/media/${mediaId}/count`)
    return response
  },

  // 获取用户评论
  async getUserComments(userId: number) {
    const response = await apiClient.get<Comment[]>(`/users/${userId}/comments`)
    return response
  }
}