<template>
  <div class="media-detail">
    <div class="media-header">
      <el-button @click="$router.back()" :icon="ArrowLeft" circle />
      <div class="media-info">
        <h1>{{ mediaStore.currentMedia?.filename }}</h1>
        <div class="media-meta">
          <span class="file-size">
            <el-icon><Document /></el-icon>
            {{ formatFileSize(mediaStore.currentMedia?.sizeBytes) }}
          </span>
          <span class="upload-date">
            <el-icon><Calendar /></el-icon>
            {{ formatDate(mediaStore.currentMedia?.createdAt) }}
          </span>
          <span class="uploader">
            <el-icon><User /></el-icon>
            {{ mediaStore.currentMedia?.uploader?.nickname || mediaStore.currentMedia?.uploader?.username }}
          </span>
        </div>
      </div>
      
      <div class="media-actions">
        <el-button 
          type="primary" 
          @click="handleDownload"
          :icon="Download"
        >
          下载
        </el-button>
        <el-button 
          v-if="canDelete"
          type="danger" 
          @click="handleDelete"
          :icon="Delete"
        >
          删除
        </el-button>
      </div>
    </div>

    <!-- 媒体内容显示 -->
    <div class="media-content">
      <div v-if="mediaStore.loading" class="loading">
        <el-loading text="加载中..." />
      </div>
      
      <div v-else-if="!mediaStore.currentMedia" class="error-state">
        <el-empty description="媒体文件不存在" />
      </div>
      
      <div v-else class="media-viewer">
        <div v-if="mediaStore.currentMedia.type === 'image'" class="image-viewer">
          <el-image
            :src="mediaStore.currentMedia.url"
            :preview-src-list="[mediaStore.currentMedia.url]"
            fit="contain"
            class="main-image"
          >
            <template #error>
              <div class="image-error">
                <el-icon size="64"><Picture /></el-icon>
                <p>图片加载失败</p>
              </div>
            </template>
          </el-image>
        </div>
        
        <div v-else-if="mediaStore.currentMedia.type === 'video'" class="video-viewer">
          <video
            :src="mediaStore.currentMedia.url"
            controls
            class="main-video"
            preload="metadata"
          >
            您的浏览器不支持视频播放
          </video>
        </div>
      </div>
    </div>

    <!-- 评论区域 -->
    <div class="comments-section">
      <div class="comments-header">
        <h3>评论</h3>
        <span class="comment-count">{{ commentStore.commentCounts[mediaId] || 0 }} 条评论</span>
      </div>
      
      <!-- 发表评论 -->
      <div v-if="userStore.isLoggedIn" class="comment-form">
        <el-avatar :size="40" :src="userStore.user?.avatar">
          {{ userStore.user?.nickname?.[0] || userStore.user?.username?.[0] || '?' }}
        </el-avatar>
        <div class="comment-input">
          <el-input
            v-model="newComment"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
            maxlength="500"
            show-word-limit
          />
          <div class="comment-actions">
            <el-button type="primary" @click="handleSubmitComment" :loading="commentStore.loading">
              发表评论
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 评论列表 -->
      <div class="comments-list">
        <div v-if="commentStore.loading" class="comments-loading">
          <el-loading text="加载评论中..." />
        </div>
        
        <div v-else-if="comments.length === 0" class="no-comments">
          <el-empty description="暂无评论，来发表第一条评论吧" />
        </div>
        
        <div v-else class="comment-item" v-for="comment in comments" :key="comment.id">
          <el-avatar :size="40" :src="comment.author?.avatar">
            {{ comment.author?.nickname?.[0] || comment.author?.username?.[0] || '?' }}
          </el-avatar>
          <div class="comment-content">
            <div class="comment-header">
              <span class="author-name">{{ comment.author?.nickname || comment.author?.username }}</span>
              <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
              <el-dropdown v-if="canEditComment(comment)" trigger="click">
                <el-button type="text" :icon="MoreFilled" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="startEditComment(comment)">
                      编辑
                    </el-dropdown-item>
                    <el-dropdown-item @click="handleDeleteComment(comment)" divided>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <div class="comment-text">
              <div v-if="editingComment?.id === comment.id" class="edit-form">
                <el-input
                  v-model="editCommentText"
                  type="textarea"
                  :rows="2"
                  maxlength="500"
                  show-word-limit
                />
                <div class="edit-actions">
                  <el-button size="small" @click="cancelEditComment">取消</el-button>
                  <el-button size="small" type="primary" @click="handleUpdateComment">保存</el-button>
                </div>
              </div>
              <p v-else>{{ comment.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useMediaStore } from '@/stores/media'
import { useCommentStore } from '@/stores/comment'
import { useUserStore } from '@/stores/user'
import { mediaApi } from '@/api/media'
import type { Comment } from '@/types/api'
import {
  ArrowLeft,
  Download,
  Delete,
  Document,
  Calendar,
  User,
  Picture,
  MoreFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const mediaStore = useMediaStore()
const commentStore = useCommentStore()
const userStore = useUserStore()

const mediaId = computed(() => Number(route.params.id as string))
const newComment = ref('')
const editingComment = ref<Comment | null>(null)
const editCommentText = ref('')

const canDelete = computed(() => {
  if (!mediaStore.currentMedia) return false
  if (mediaStore.currentMedia.uploaderId === userStore.user?.id) return true
  return userStore.isAdmin
})

const canEditComment = (comment: Readonly<Comment>) => {
  return comment.author?.id === userStore.user?.id || userStore.isAdmin
}

const comments = computed(() => {
  return commentStore.mediaComments[mediaId.value] || []
})

const formatFileSize = (bytes?: number) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateString?: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString('zh-CN')
}

const handleDownload = async () => {
  if (!mediaStore.currentMedia) return
  
  try {
    const blob = await mediaApi.downloadMedia(mediaStore.currentMedia.id)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = mediaStore.currentMedia.filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (error) {
    ElMessage.error('下载失败')
  }
}

const handleDelete = async () => {
  if (!mediaStore.currentMedia) return
  
  await ElMessageBox.confirm(
    '确定要删除这个媒体文件吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  
  try {
    await mediaStore.deleteMedia(mediaStore.currentMedia.id)
    ElMessage.success('媒体文件删除成功')
  } catch  {
    ElMessage.error('媒体文件删除失败')
  }
}

const handleSubmitComment = async () => {
  if (!newComment.value.trim()) return
  
  try {
    await commentStore.createComment(mediaId.value, newComment.value.trim())
    newComment.value = ''
    ElMessage.success('评论发表成功')
  } catch  {
    ElMessage.error('评论发表失败')
  }
}

const startEditComment = (comment: Readonly<Comment>) => {
  editingComment.value = comment
  editCommentText.value = comment.content
}

const cancelEditComment = () => {
  editingComment.value = null
  editCommentText.value = ''
}

const handleUpdateComment = async () => {
  if (!editingComment.value || !editCommentText.value.trim()) return
  
  try {
    await commentStore.updateComment(editingComment.value.id, editCommentText.value.trim(), Number(mediaId.value))
    cancelEditComment()
    ElMessage.success('评论更新成功')
  } catch  {
    ElMessage.error('评论更新失败')
  }
}

const handleDeleteComment = async (comment: Readonly<Comment>) => {
  await ElMessageBox.confirm(
    '确定要删除这条评论吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  
  try {
    await commentStore.deleteComment(comment.id, Number(mediaId.value))
    ElMessage.success('评论删除成功')
  } catch  {
    ElMessage.error('评论删除失败')
  }
}

onMounted(async () => {
  await mediaStore.fetchMedia(mediaId.value)
  await commentStore.fetchMediaComments(mediaId.value)
})

watch(
  () => mediaId.value,
  async (newMediaId) => {
    if (newMediaId) {
      await mediaStore.fetchMedia(newMediaId)
      await commentStore.fetchMediaComments(newMediaId)
    }
  }
)
</script>

<style scoped>
.media-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.media-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.media-info {
  flex: 1;
}

.media-info h1 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
  word-break: break-all;
}

.media-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #909399;
}

.media-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.media-actions {
  display: flex;
  gap: 10px;
}

.media-content {
  margin-bottom: 30px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

.error-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

.media-viewer {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 500px;
  background: #f5f5f5;
}

.image-viewer {
  width: 100%;
  max-width: 800px;
}

.main-image {
  width: 100%;
  max-height: 600px;
}

.video-viewer {
  width: 100%;
  max-width: 800px;
}

.main-video {
  width: 100%;
  max-height: 600px;
  border-radius: 4px;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #909399;
}

.image-error p {
  margin-top: 10px;
}

.comments-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.comments-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.comment-count {
  color: #909399;
  font-size: 14px;
}

.comment-form {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-input {
  flex: 1;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.comments-list {
  min-height: 200px;
}

.comments-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.no-comments {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.author-name {
  font-weight: 500;
  color: #303133;
}

.comment-date {
  color: #909399;
  font-size: 12px;
}

.comment-text p {
  margin: 0;
  line-height: 1.6;
  color: #606266;
}

.edit-form {
  margin-bottom: 10px;
}

.edit-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

@media (max-width: 768px) {
  .media-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .media-meta {
    flex-direction: column;
    gap: 10px;
  }
  
  .media-actions {
    justify-content: center;
  }
  
  .comment-form {
    flex-direction: column;
  }
}
</style>