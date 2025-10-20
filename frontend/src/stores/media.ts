import { defineStore } from 'pinia'
import { ref, readonly } from 'vue'
import type { Media } from '@/types/api'
import { mediaApi } from '@/api'

export const useMediaStore = defineStore('media', () => {
  const albumMedia = ref<Media[] | null>(null)
  const currentMedia = ref<Media | null>(null)
  const loading = ref(false)
  const uploadProgress = ref(0)
  const isUploading = ref(false)

  // 获取相册媒体列表
  const fetchAlbumMedia = async (
    albumId: number,
    type?: 'image' | 'video',
    page = 0,
    size = 20,
    sort = 'createdAt,desc'
  ) => {
    loading.value = true
    try {
      const response = await mediaApi.getAlbumMedia(albumId, type, page, size, sort)
      albumMedia.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 获取媒体详情
  const fetchMedia = async (id: number) => {
    loading.value = true
    try {
      const response = await mediaApi.getMedia(id)
      currentMedia.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 上传媒体文件
  const uploadMedia = async (albumId: number, file: File, title?: string) => {
    isUploading.value = true
    uploadProgress.value = 0
    try {
      const media = await mediaApi.uploadMedia(albumId, file, title)
      // 刷新当前相册的媒体列表
      if (albumMedia.value) {
        await fetchAlbumMedia(albumId)
      }
      return media
    } finally {
      isUploading.value = false
      uploadProgress.value = 0
    }
  }

  // 删除媒体文件
  const deleteMedia = async (id: number) => {
    await mediaApi.deleteMedia(id)
    if (currentMedia.value?.id === id) {
      currentMedia.value = null
    }
    // 刷新媒体列表
    if (albumMedia.value) {
      const albumId = albumMedia.value[0]?.albumId
      if (albumId) {
        await fetchAlbumMedia(albumId)
      }
    }
  }

  // 下载媒体文件
  const downloadMedia = async (id: number, filename: string) => {
    try {
      const blob = await mediaApi.downloadMedia(id)
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    } catch (error) {
      console.error('下载失败:', error)
      throw error
    }
  }

  return {
    albumMedia: readonly(albumMedia),
    currentMedia: readonly(currentMedia),
    loading: readonly(loading),
    uploadProgress: readonly(uploadProgress),
    isUploading: readonly(isUploading),
    fetchAlbumMedia,
    fetchMedia,
    uploadMedia,
    deleteMedia,
    downloadMedia
  }
})