import apiClient from './client'
import type { AlbumMember } from '@/types/api'

export interface MemberRequest {
  username: string
  role: string
}

export const albumMemberApi = {
  // 邀请成员加入相册
  async inviteMember(albumId: number, request: MemberRequest) {
    const response = await apiClient.post<AlbumMember>(`/albums/${albumId}/members`, request)
    return response
  },

  // 移除相册成员
  async removeMember(albumId: number, memberUserId: number) {
    await apiClient.delete(`/albums/${albumId}/members/${memberUserId}`)
  },

  // 获取相册成员列表
  async getAlbumMembers(albumId: number) {
    const response = await apiClient.get<AlbumMember[]>(`/albums/${albumId}/members`)
    return response
  }
}