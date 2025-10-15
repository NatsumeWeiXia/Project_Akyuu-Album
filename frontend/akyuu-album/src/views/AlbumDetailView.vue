<template>
  <div class="album-detail-container">
    <div class="album-header">
      <div class="album-info">
        <h1>{{ album?.name }}</h1>
        <p v-if="album?.description" class="description">{{ album.description }}</p>
        <div class="album-meta">
          <span>创建者: {{ album?.ownerName }}</span>
          <span>创建时间: {{ formatDate(album?.createdAt) }}</span>
          <span>{{ album?.isPublic ? '公开相册' : '私有相册' }}</span>
        </div>
      </div>
      
      <div class="album-actions">
        <button 
          v-if="canUpload" 
          class="upload-btn"
          @click="showUploadModal = true"
        >
          上传照片
        </button>
        <button 
          v-if="isOwner" 
          class="manage-btn"
          @click="showMemberModal = true"
        >
          成员管理
        </button>
      </div>
    </div>
    
    <div class="media-grid">
      <div 
        v-for="media in mediaList" 
        :key="media.id" 
        class="media-card"
        @click="viewMedia(media.id)"
      >
        <div class="media-preview">
          <img 
            v-if="media.thumbnailUrl" 
            :src="media.thumbnailUrl" 
            :alt="media.title"
          />
          <div v-else class="placeholder-media">
            <span>{{ media.contentType.startsWith('video/') ? '视频' : '图片' }}</span>
          </div>
        </div>
        <div class="media-info">
          <h3>{{ media.title || media.filename }}</h3>
          <div class="media-meta">
            <span>{{ formatFileSize(media.sizeBytes) }}</span>
            <span>{{ formatDate(media.createdAt) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="mediaList.length === 0" class="empty-state">
      <p>该相册暂无媒体文件</p>
    </div>
    
    <!-- 上传媒体模态框 -->
    <div v-if="showUploadModal" class="modal-overlay" @click="showUploadModal = false">
      <div class="modal-content" @click.stop>
        <h2>上传媒体文件</h2>
        <form @submit.prevent="handleUpload">
          <div class="form-group">
            <label for="mediaFile">选择文件</label>
            <input 
              id="mediaFile" 
              type="file" 
              accept="image/*,video/*"
              @change="handleFileSelect"
            />
          </div>
          
          <div class="form-group">
            <label for="mediaTitle">标题（可选）</label>
            <input 
              id="mediaTitle" 
              v-model="uploadMedia.title" 
              type="text" 
              placeholder="请输入媒体标题"
            />
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="showUploadModal = false">取消</button>
            <button 
              type="submit" 
              :disabled="uploading || !selectedFile"
            >
              {{ uploading ? '上传中...' : '上传' }}
            </button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 成员管理模态框 -->
    <div v-if="showMemberModal" class="modal-overlay" @click="showMemberModal = false">
      <div class="modal-content member-modal" @click.stop>
        <h2>成员管理</h2>
        
        <div class="add-member">
          <input 
            v-model="newMemberUsername" 
            type="text" 
            placeholder="输入用户名邀请成员"
          />
          <button 
            :disabled="inviting || !newMemberUsername"
            @click="inviteMember"
          >
            {{ inviting ? '邀请中...' : '邀请' }}
          </button>
        </div>
        
        <div class="member-list">
          <div 
            v-for="member in members" 
            :key="member.id" 
            class="member-item"
          >
            <span>{{ member.username }} ({{ member.nickname }})</span>
            <span class="member-role">{{ member.role }}</span>
            <button 
              v-if="member.role !== 'OWNER' && isOwner"
              class="remove-btn"
              @click="removeMember(member.id)"
            >
              移除
            </button>
          </div>
        </div>
        
        <div class="modal-actions">
          <button @click="showMemberModal = false">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface Album {
  id: number
  name: string
  description: string | null
  isPublic: boolean
  ownerId: number
  ownerName: string
  createdAt: string
}

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

interface Member {
  id: number
  userId: number
  username: string
  nickname: string
  role: string
  createdAt: string
}

interface UploadMedia {
  title: string
  file: File | null
}

const route = useRoute()
const router = useRouter()
const albumId = route.params.id as string

const album = ref<Album | null>(null)
const mediaList = ref<Media[]>([])
const members = ref<Member[]>([])

const showUploadModal = ref(false)
const showMemberModal = ref(false)
const uploading = ref(false)
const inviting = ref(false)

const selectedFile = ref<File | null>(null)
const newMemberUsername = ref('')
const uploadMedia = ref<UploadMedia>({
  title: '',
  file: null
})

// 模拟权限判断
const isOwner = ref(true)
const canUpload = ref(true)

// 模拟相册数据
const mockAlbum: Album = {
  id: parseInt(albumId),
  name: '风景照片集',
  description: '美丽的自然风景照片集',
  isPublic: true,
  ownerId: 1,
  ownerName: '张三',
  createdAt: '2025-10-01T10:00:00Z'
}

// 模拟媒体数据
const mockMediaList: Media[] = [
  {
    id: 1,
    albumId: parseInt(albumId),
    title: '日出',
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
  },
  {
    id: 2,
    albumId: parseInt(albumId),
    title: '山景',
    filename: 'mountain.jpg',
    contentType: 'image/jpeg',
    sizeBytes: 3072000,
    width: 2560,
    height: 1440,
    durationSeconds: null,
    storagePath: '/uploads/1/mountain.jpg',
    thumbnailUrl: null,
    uploaderId: 2,
    uploaderName: '李四',
    createdAt: '2025-10-02T14:30:00Z'
  }
]

// 模拟成员数据
const mockMembers: Member[] = [
  {
    id: 1,
    userId: 1,
    username: 'zhangsan',
    nickname: '张三',
    role: 'OWNER',
    createdAt: '2025-10-01T10:00:00Z'
  },
  {
    id: 2,
    userId: 2,
    username: 'lisi',
    nickname: '李四',
    role: 'MEMBER',
    createdAt: '2025-10-02T09:00:00Z'
  }
]

onMounted(() => {
  // 模拟加载相册数据
  album.value = mockAlbum
  mediaList.value = mockMediaList
  members.value = mockMembers
})

const formatDate = (dateString: string | undefined) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const formatFileSize = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(1) + ' GB'
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0]
  }
}

const handleUpload = async () => {
  if (!selectedFile.value) return
  
  uploading.value = true
  try {
    // TODO: 调用上传API
    console.log('Upload media:', uploadMedia.value, selectedFile.value)
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    // 重置表单并关闭模态框
    uploadMedia.value = {
      title: '',
      file: null
    }
    selectedFile.value = null
    showUploadModal.value = false
    
    // 重新加载媒体列表
    mediaList.value = [...mockMediaList, {
      id: Date.now(),
      albumId: parseInt(albumId),
      title: uploadMedia.value.title || selectedFile.value.name,
      filename: selectedFile.value.name,
      contentType: selectedFile.value.type,
      sizeBytes: selectedFile.value.size,
      width: null,
      height: null,
      durationSeconds: null,
      storagePath: `/uploads/${albumId}/${selectedFile.value.name}`,
      thumbnailUrl: null,
      uploaderId: 1,
      uploaderName: '当前用户',
      createdAt: new Date().toISOString()
    }]
  } catch (error) {
    console.error('Upload failed:', error)
  } finally {
    uploading.value = false
  }
}

const inviteMember = async () => {
  if (!newMemberUsername.value) return
  
  inviting.value = true
  try {
    // TODO: 调用邀请成员API
    console.log('Invite member:', newMemberUsername.value)
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 清空输入框
    newMemberUsername.value = ''
  } catch (error) {
    console.error('Invite member failed:', error)
  } finally {
    inviting.value = false
  }
}

const removeMember = async (memberId: number) => {
  try {
    // TODO: 调用移除成员API
    console.log('Remove member:', memberId)
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 更新成员列表
    members.value = members.value.filter(member => member.id !== memberId)
  } catch (error) {
    console.error('Remove member failed:', error)
  }
}

const viewMedia = (mediaId: number) => {
  router.push(`/media/${mediaId}`)
}
</script>

<style scoped>
.album-detail-container {
  padding: 2rem;
}

.album-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.album-info h1 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.description {
  color: #666;
  margin-bottom: 1rem;
}

.album-meta {
  display: flex;
  gap: 1.5rem;
  font-size: 0.9rem;
  color: #999;
}

.album-actions {
  display: flex;
  gap: 0.5rem;
}

.album-actions button {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.upload-btn {
  background-color: #409eff;
  color: white;
  border: 1px solid #409eff;
}

.manage-btn {
  background-color: #67c23a;
  color: white;
  border: 1px solid #67c23a;
}

.media-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
}

.media-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.media-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.media-preview {
  height: 150px;
  overflow: hidden;
}

.media-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-media {
  width: 100%;
  height: 100%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.media-info {
  padding: 0.75rem;
}

.media-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 0.9rem;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.media-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #999;
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
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content h2 {
  margin-top: 0;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #555;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-group textarea {
  min-height: 100px;
  resize: vertical;
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

.modal-actions button:last-child {
  background: #409eff;
  border: 1px solid #409eff;
  color: white;
}

.modal-actions button:hover:not(:disabled) {
  opacity: 0.9;
}

.modal-actions button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.member-modal .add-member {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.member-modal .add-member input {
  flex: 1;
}

.member-list {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 1.5rem;
}

.member-item {
  display: flex;
  align-items: center;
  padding: 0.75rem;
  border-bottom: 1px solid #eee;
}

.member-item:last-child {
  border-bottom: none;
}

.member-role {
  margin: 0 1rem;
  padding: 0.25rem 0.5rem;
  background-color: #ecf5ff;
  color: #409eff;
  border-radius: 4px;
  font-size: 0.8rem;
}

.remove-btn {
  margin-left: auto;
  padding: 0.25rem 0.5rem;
  background-color: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.8rem;
}
</style>