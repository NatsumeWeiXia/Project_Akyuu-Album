import apiClient from './client'
import type { Album, AlbumRequest, PageResponse, AlbumMember } from '@/types/api'

export const albumApi = {
  // 创建相册
  async createAlbum(data: AlbumRequest) {
    const response = await apiClient.post<Album>('/albums', data)
    return response.data!
  },

  // 获取公共相册列表
  async getPublicAlbums(keyword?: string, page = 0, size = 20) {
    const params = new URLSearchParams()
    if (keyword) params.append('keyword', keyword)
    params.append('page', page.toString())
    params.append('size', size.toString())
    
    const response = await apiClient.get<PageResponse<Album>>(`/albums/public?${params}`)
    return response.data!
  },

  // 获取我的相册列表
  async getMyAlbums() {
    const response = await apiClient.get<Album[]>('/albums/mine')
    return response.data!
  },

  // 获取相册详情
  async getAlbum(id: number) {
    const response = await apiClient.get<Album>(`/albums/${id}`)
    return response.data!
  },

  // 更新相册
  async updateAlbum(id: number, data: AlbumRequest) {
    const response = await apiClient.patch<Album>(`/albums/${id}`, data)
    return response.data!
  },

  // 删除相册
  async deleteAlbum(id: number) {
    const response = await apiClient.delete<void>(`/albums/${id}`)
    return response.data!
  },

  // 添加相册成员
  async addAlbumMember(albumId: number, userId: number) {
    const response = await apiClient.post<AlbumMember>(`/albums/${albumId}/members`, { userId })
    return response.data!
  },

  // 移除相册成员
  async removeAlbumMember(albumId: number, userId: number) {
    const response = await apiClient.delete<void>(`/albums/${albumId}/members/${userId}`)
    return response.data!
  },

  // 获取相册成员列表
  async getAlbumMembers(albumId: number) {
    const response = await apiClient.get<AlbumMember[]>(`/albums/${albumId}/members`)
    return response.data!
  }
}