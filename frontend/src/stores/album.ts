import { defineStore } from 'pinia'
import { ref, readonly } from 'vue'
import type { Album, PageResponse } from '@/types/api'
import { albumApi } from '@/api'

export const useAlbumStore = defineStore('album', () => {
  const publicAlbums = ref<PageResponse<Album> | null>(null)
  const myAlbums = ref<Album[]>([])
  const currentAlbum = ref<Album | null>(null)
  const loading = ref(false)

  // 获取公共相册
  const fetchPublicAlbums = async (keyword?: string, page = 0, size = 20) => {
    loading.value = true
    try {
      const response = await albumApi.getPublicAlbums(keyword, page, size)
      publicAlbums.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 获取我的相册
  const fetchMyAlbums = async () => {
    loading.value = true
    try {
      const response = await albumApi.getMyAlbums()
      myAlbums.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 获取相册详情
  const fetchAlbum = async (id: number) => {
    loading.value = true
    try {
      const response = await albumApi.getAlbum(id)
      currentAlbum.value = response
      return response
    } finally {
      loading.value = false
    }
  }

  // 创建相册
  const createAlbum = async (name: string, description?: string, isPublic = true) => {
    const response = await albumApi.createAlbum({ name, description, isPublic })
    if (!isPublic) {
      // 如果是私有相册，刷新我的相册列表
      await fetchMyAlbums()
    } else {
      // 如果是公共相册，刷新公共相册列表
      await fetchPublicAlbums()
    }
    return response
  }

  // 更新相册
  const updateAlbum = async (id: number, name: string, description?: string, isPublic?: boolean) => {
    const album = await albumApi.updateAlbum(id, { name, description, isPublic: isPublic ?? currentAlbum.value!.isPublic })
    if (currentAlbum.value?.id === id) {
      currentAlbum.value = album
    }
    // 刷新相关列表
    if (album.isPublic) {
      await fetchPublicAlbums()
    } else {
      await fetchMyAlbums()
    }
    return album
  }

  // 删除相册
  const deleteAlbum = async (id: number) => {
    await albumApi.deleteAlbum(id)
    if (currentAlbum.value?.id === id) {
      currentAlbum.value = null
    }
    // 刷新列表
    await fetchMyAlbums()
    await fetchPublicAlbums()
  }

  // 添加相册成员
  const addAlbumMember = async (albumId: number, userId: number) => {
    const member = await albumApi.addAlbumMember(albumId, userId)
    return member
  }

  // 移除相册成员
  const removeAlbumMember = async (albumId: number, userId: number) => {
    await albumApi.removeAlbumMember(albumId, userId)
  }

  // 获取相册成员
  const getAlbumMembers = async (albumId: number) => {
    const members = await albumApi.getAlbumMembers(albumId)
    return members
  }

  return {
    publicAlbums: readonly(publicAlbums),
    myAlbums: readonly(myAlbums),
    currentAlbum: readonly(currentAlbum),
    loading: readonly(loading),
    fetchPublicAlbums,
    fetchMyAlbums,
    fetchAlbum,
    createAlbum,
    updateAlbum,
    deleteAlbum,
    addAlbumMember,
    removeAlbumMember,
    getAlbumMembers
  }
})