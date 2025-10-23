import apiClient from './client'
import type { Media } from '@/types/api'

export const mediaApi = {
  // 上传媒体文件
  async uploadMedia(albumId: number, file: File, title?: string) {
    const formData = new FormData()
    formData.append('albumId', albumId.toString())
    formData.append('file', file)
    if (title) {
      formData.append('title', title)
    }
    
    const response = await apiClient.upload<Media>('/media/upload', formData)
    return response
  },

  // 获取媒体详情
  async getMedia(id: number) {
    const response = await apiClient.get<Media>(`/media/${id}`)
    return response
  },

  // 下载媒体文件
  async downloadMedia(id: number): Promise<Blob> {
    const response = await apiClient.get<Blob>(`/media/${id}/download`, {
      responseType: 'blob'
    })
    return response
  },

  // 预览媒体文件
  async previewMedia(id: number): Promise<Blob> {
    const response = await apiClient.get<Blob>(`/media/${id}/preview`, {
      responseType: 'blob'
    })
    return response
  },

  // 删除媒体文件
  async deleteMedia(id: number) {
    const response = await apiClient.delete<void>(`/media/${id}`)
    return response
  },

  // 获取相册中的媒体列表
  async getAlbumMedia(
    albumId: number, 
    type?: 'image' | 'video', 
    page = 0, 
    size = 20, 
    sort = 'createdAt,desc'
  ) {
    const params = new URLSearchParams()
    if (type) params.append('type', type)
    params.append('page', page.toString())
    params.append('size', size.toString())
    params.append('sort', sort)
    
    const response = await apiClient.get<Media[]>(`/media/album/${albumId}?${params}`)
    return response
  }
}