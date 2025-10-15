<template>
  <div class="media-detail-container">
    <div class="media-content">
      <div v-if="media" class="media-preview">
        <img 
          v-if="media.contentType.startsWith('image/')" 
          :src="media.storagePath" 
          :alt="media.title"
          class="media-image"
        />
        <video 
          v-else-if="media.contentType.startsWith('video/')" 
          :src="media.storagePath" 
          controls
          class="media-video"
        />
        <div v-else class="unsupported-media">
          <p>不支持的媒体类型: {{ media.contentType }}</p>
        </div>
      </div>
      
      <div v-if="media" class="media-info-panel">
        <div class="media-header">
          <h1>{{ media.title || media.filename }}</h1>
          <div class="media-actions">
            <button 
              class="download-btn"
              @click="downloadMedia"
            >
              下载
            </button>
            <button 
              v-if="canDelete"
              class="delete-btn"
              @click="showDeleteConfirm = true"
            >
              删除
            </button>
          </div>
        </div>
        
        <div class="media-details">
          <div class="detail-item">
            <span class="label">文件名:</span>
            <span>{{ media.filename }}</span>
          </div>
          <div class="detail-item">
            <span class="label">文件类型:</span>
            <span>{{ media.contentType }}</span>
          </div>
          <div class="detail-item">
            <span class="label">文件大小:</span>
            <span>{{ formatFileSize(media.sizeBytes) }}</span>
          </div>
          <div v-if="media.width && media.height" class="detail-item">
            <span class="label">尺寸:</span>
            <span>{{ media.width }} × {{ media.height }}</span>
          </div>
          <div v-if="media.durationSeconds" class="detail-item">
            <span class="label">时长:</span>
            <span>{{ formatDuration(media.durationSeconds) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">上传者:</span>
            <span>{{ media.uploaderName }}</span>
          </div>
          <div class="detail-item">
            <span class="label">上传时间:</span>
            <span>{{ formatDate(media.createdAt) }}</span>
          </div>
        </div>
        
        <div class="comments-section">
          <h2>评论 ({{ comments.length }})</h2>
          
          <div class="comment-form">
            <textarea 
              v-model="newComment.content" 
              placeholder="添加评论..."
              rows="3"
            />
            <button 
              :disabled="commenting || !newComment.content.trim()"
              @click="addComment"
            >
              {{ commenting ? '提交中...' : '评论' }}
            </button>
          </div>
          
          <div class="comments-list">
            <div 
              v-for="comment in comments" 
              :key="comment.id" 
              class="comment-item"
            >
              <div class="comment-header">
                <span class="comment-author">{{ comment.authorName }}</span>
                <span class="comment-date">{{ formatDate(comment.createdAt) }}</span>
                <button 
                  v-if="canDeleteComment(comment.authorId)"
                  class="delete-comment-btn"
                  @click="deleteComment(comment.id)"
                >
                  删除
                </button>
              </div>
              <div class="comment-content">
                {{ comment.content }}
              </div>
              
              <!-- 回复评论 -->
              <div 
                v-for="reply in comment.replies" 
                :key="reply.id" 
                class="reply-item"
              >
                <div class="comment-header">
                  <span class="comment-author">{{ reply.authorName }}</span>
                  <span class="comment-date">{{ formatDate(reply.createdAt) }}</span>
                  <button 
                    v-if="canDeleteComment(reply.authorId)"
                    class="delete-comment-btn"
                    @click="deleteComment(reply.id)"
                  >
                    删除
                  </button>
                </div>
                <div class="comment-content">
                  {{ reply.content }}
                </div>
              </div>
              
              <!-- 回复表单 -->
              <div class="reply-form">
                <textarea 
                  v-model="comment.replyContent" 
                  :placeholder="`回复 ${comment.authorName}...`"
                  rows="2"
                />
                <button 
                  :disabled="commenting || !comment.replyContent?.trim()"
                  @click="addReply(comment)"
                >
                  回复
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 删除确认对话框 -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click="showDeleteConfirm = false">
      <div class="modal-content confirm-modal" @click.stop>
        <h2>确认删除</h2>
        <p>确定要删除这个媒体文件吗？此操作不可恢复。</p>
        <div class="modal-actions">
          <button @click="showDeleteConfirm = false">取消</button>
          <button class="danger-btn" @click="deleteMedia">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface Media {
  id: number
  albumId: number
  title: string | null
  filename: string
  contentType: string
  sizeBytes: number
  width: number | null
  height: number | null
  durationSeconds: number | null
  storagePath: string
  thumbnailUrl: string | null
  uploaderId: number
  uploaderName: string
  createdAt: string
}

interface Comment {
  id: number
  mediaId: number
  authorId: number
  authorName: string
  parentId: number | null
  content: string
  replyContent?: string
  replies: Comment[]
  createdAt: string
}

interface NewComment {
  content: string
  parentId?: number
}

const route = useRoute()
const router = useRouter()
const mediaId = route.params.id as string

const media = ref<Media | null>(null)
const comments = ref<Comment[]>([])
const showDeleteConfirm = ref(false)
const commenting = ref(false)

const newComment = ref<NewComment>({
  content: ''
})

// 模拟权限判断
const canDelete = ref(true)

// 模拟媒体数据
const mockMedia: Media = {
  id: parseInt(mediaId),
  albumId: 1,
  title: '美丽的日出',
  filename: 'sunset.jpg',
  contentType: 'image/jpeg',
  sizeBytes: 2048000,
  width: 1920,
  height: 1080,
  durationSeconds: null,
  storagePath: '/uploads/1/sunset.jpg',
  thumbnailUrl: null,
  uploaderId: 1,
  uploaderName: '张三',
  createdAt: '2025-10-01T11:00:00Z'
}

// 模拟评论数据
const mockComments: Comment[] = [
  {
    id: 1,
    mediaId: parseInt(mediaId),
    authorId: 2,
    authorName: '李四',
    parentId: null,
    content: '这张照片真美！',
    replies: [
      {
        id: 3,
        mediaId: parseInt(mediaId),
        authorId: 1,
        authorName: '张三',
        parentId: 1,
        content: '谢谢！是我早上拍的。',
        replies: [],
        createdAt: '2025-10-01T12:30:00Z'
      }
    ],
    createdAt: '2025-10-01T12:00:00Z'
  },
  {
    id: 2,
    mediaId: parseInt(mediaId),
    authorId: 3,
    authorName: '王五',
    parentId: null,
    content: '在哪里拍的？',
    replies: [],
    createdAt: '2025-10-01T13:00:00Z'
  }
]

onMounted(() => {
  // 模拟加载媒体数据
  media.value = mockMedia
  comments.value = mockComments
})

const formatFileSize = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(1) + ' GB'
}

const formatDuration = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs < 10 ? '0' : ''}${secs}`
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const downloadMedia = () => {
  // TODO: 调用下载API
  console.log('Download media:', mediaId)
  alert('下载功能待实现')
}

const deleteMedia = async () => {
  try {
    // TODO: 调用删除API
    console.log('Delete media:', mediaId)
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 关闭确认对话框
    showDeleteConfirm.value = false
    
    // 返回到相册详情页
    router.push(`/albums/${media.value?.albumId}`)
  } catch (error) {
    console.error('Delete media failed:', error)
  }
}

const addComment = async () => {
  if (!newComment.value.content.trim()) return
  
  commenting.value = true
  try {
    // TODO: 调用添加评论API
    console.log('Add comment:', newComment.value)
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 清空评论内容
    newComment.value.content = ''
    
    // 更新评论列表
    comments.value = [{
      id: Date.now(),
      mediaId: parseInt(mediaId),
      authorId: 1,
      authorName: '当前用户',
      parentId: null,
      content: newComment.value.content,
      replies: [],
      createdAt: new Date().toISOString()
    }, ...comments.value]
  } catch (error) {
    console.error('Add comment failed:', error)
  } finally {
    commenting.value = false
  }
}

const addReply = async (parentComment: Comment) => {
  if (!parentComment.replyContent?.trim()) return
  
  commenting.value = true
  try {
    const replyData: NewComment = {
      content: parentComment.replyContent,
      parentId: parentComment.id
    }
    
    // TODO: 调用添加回复API
    console.log('Add reply:', replyData)
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 清空回复内容
    parentComment.replyContent = ''
    
    // 更新评论列表
    comments.value = comments.value.map(comment => {
      if (comment.id === parentComment.id) {
        return {
          ...comment,
          replies: [...comment.replies, {
            id: Date.now(),
            mediaId: parseInt(mediaId),
            authorId: 1,
            authorName: '当前用户',
            parentId: parentComment.id,
            content: replyData.content,
            replies: [],
            createdAt: new Date().toISOString()
          }]
        }
      }
      return comment
    })
  } catch (error) {
    console.error('Add reply failed:', error)
  } finally {
    commenting.value = false
  }
}

const deleteComment = async (commentId: number) => {
  try {
    // TODO: 调用删除评论API
    console.log('Delete comment:', commentId)
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 更新评论列表
    const deleteCommentRecursive = (commentList: Comment[]): Comment[] => {
      return commentList.filter(comment => {
        if (comment.id === commentId) {
          return false
        }
        comment.replies = deleteCommentRecursive(comment.replies)
        return true
      })
    }
    
    comments.value = deleteCommentRecursive(comments.value)
  } catch (error) {
    console.error('Delete comment failed:', error)
  }
}

const canDeleteComment = (authorId: number) => {
  // 模拟权限判断：评论作者、媒体上传者、相册所有者可以删除评论
  return authorId === 1 || media.value?.uploaderId === 1
}
</script>

<style scoped>
.media-detail-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.media-content {
  display: flex;
  gap: 2rem;
}

.media-preview {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.media-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.media-video {
  max-width: 100%;
  max-height: 70vh;
}

.unsupported-media {
  padding: 2rem;
  text-align: center;
  color: #999;
}

.media-info-panel {
  width: 350px;
  max-height: 70vh;
  overflow-y: auto;
}

.media-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.media-header h1 {
  margin: 0;
  color: #333;
  font-size: 1.3rem;
}

.media-actions {
  display: flex;
  gap: 0.5rem;
}

.media-actions button {
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.download-btn {
  background-color: #409eff;
  color: white;
  border: 1px solid #409eff;
}

.delete-btn {
  background-color: #f56c6c;
  color: white;
  border: 1px solid #f56c6c;
}

.media-details {
  margin-bottom: 2rem;
}

.detail-item {
  display: flex;
  margin-bottom: 0.75rem;
  font-size: 0.9rem;
}

.label {
  width: 80px;
  color: #999;
  flex-shrink: 0;
}

.comments-section h2 {
  margin: 0 0 1rem 0;
  color: #333;
  font-size: 1.2rem;
}

.comment-form {
  margin-bottom: 1.5rem;
}

.comment-form textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  margin-bottom: 0.5rem;
  box-sizing: border-box;
}

.comment-form button {
  padding: 0.5rem 1rem;
  background-color: #409eff;
  color: white;
  border: 1px solid #409eff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.comment-form button:disabled {
  background-color: #a0cfff;
  border-color: #a0cfff;
  cursor: not-allowed;
}

.comments-list {
  max-height: 300px;
  overflow-y: auto;
}

.comment-item {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.reply-item {
  padding: 0.75rem;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  margin-top: 0.75rem;
  margin-left: 1rem;
  background-color: #fafafa;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.comment-author {
  font-weight: bold;
  color: #333;
  margin-right: 0.5rem;
}

.comment-date {
  color: #999;
  margin-right: 0.5rem;
}

.delete-comment-btn {
  margin-left: auto;
  padding: 0.25rem 0.5rem;
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.reply-form {
  margin-top: 0.75rem;
}

.reply-form textarea {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
  box-sizing: border-box;
}

.reply-form button {
  padding: 0.25rem 0.5rem;
  background-color: #409eff;
  color: white;
  border: 1px solid #409eff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}

.reply-form button:disabled {
  background-color: #a0cfff;
  border-color: #a0cfff;
  cursor: not-allowed;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  width: 90%;
  max-width: 400px;
}

.confirm-modal p {
  margin: 1rem 0;
  color: #666;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1.5rem;
}

.modal-actions button {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.modal-actions button:first-child {
  background: #f5f5f5;
  border: 1px solid #ddd;
  color: #333;
}

.modal-actions .danger-btn {
  background: #f56c6c;
  border: 1px solid #f56c6c;
  color: white;
}
</style>