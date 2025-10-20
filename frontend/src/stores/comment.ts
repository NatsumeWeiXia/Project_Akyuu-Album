import { defineStore } from 'pinia'
import { ref, readonly } from 'vue'
import type { Comment } from '@/types/api'
import { commentApi } from '@/api'

export const useCommentStore = defineStore('comment', () => {
  const mediaComments = ref<Comment[]>([])
  const commentCounts = ref<Record<number, number>>({})
  const loading = ref(false)

  // 获取媒体评论
  const fetchMediaComments = async (mediaId: number) => {
    loading.value = true
    try {
      const comments = await commentApi.getMediaComments(mediaId)
      mediaComments.value = comments
      return comments
    } finally {
      loading.value = false
    }
  }

  // 创建评论
  const createComment = async (mediaId: number, content: string, parentId?: number) => {
    const comment = await commentApi.createComment(mediaId, content, parentId)
    // 重新获取评论列表
    await fetchMediaComments(mediaId)
    // 更新评论数
    await fetchCommentCount(mediaId)
    return comment
  }

  // 更新评论
  const updateComment = async (id: number, content: string, mediaId: number) => {
    const comment = await commentApi.updateComment(id, content)
    // 重新获取评论列表
    await fetchMediaComments(mediaId)
    return comment
  }

  // 删除评论
  const deleteComment = async (id: number, mediaId: number) => {
    await commentApi.deleteComment(id)
    // 重新获取评论列表
    await fetchMediaComments(mediaId)
    // 更新评论数
    await fetchCommentCount(mediaId)
  }

  // 获取评论数量
  const fetchCommentCount = async (mediaId: number) => {
    const count = await commentApi.getCommentCount(mediaId)
    commentCounts.value[mediaId] = count
    return count
  }

  // 获取评论数量（从缓存）
  const getCommentCount = (mediaId: number) => {
    return commentCounts.value[mediaId] || 0
  }

  return {
    mediaComments: readonly(mediaComments),
    commentCounts: readonly(commentCounts),
    loading: readonly(loading),
    fetchMediaComments,
    createComment,
    updateComment,
    deleteComment,
    fetchCommentCount,
    getCommentCount
  }
})