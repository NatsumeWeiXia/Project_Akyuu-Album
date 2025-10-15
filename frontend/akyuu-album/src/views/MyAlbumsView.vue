<template>
  <div class="my-albums-container">
    <div class="header">
      <h1>我的相册</h1>
      <button class="create-album-btn" @click="showCreateModal = true">
        创建相册
      </button>
    </div>
    
    <div class="album-list">
      <div 
        v-for="album in albums" 
        :key="album.id" 
        class="album-card"
        @click="goToAlbumDetail(album.id)"
      >
        <div class="album-cover">
          <img 
            v-if="album.coverUrl" 
            :src="album.coverUrl" 
            :alt="album.name"
          />
          <div v-else class="placeholder-cover">
            <span>相册封面</span>
          </div>
        </div>
        <div class="album-info">
          <h3>{{ album.name }}</h3>
          <p class="description">{{ album.description || '暂无描述' }}</p>
          <div class="album-meta">
            <span>{{ album.isPublic ? '公开' : '私有' }}</span>
            <span>{{ album.mediaCount }} 张照片</span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="albums.length === 0" class="empty-state">
      <p>暂无相册</p>
      <button @click="showCreateModal = true">创建第一个相册</button>
    </div>
    
    <!-- 创建相册模态框 -->
    <div v-if="showCreateModal" class="modal-overlay" @click="showCreateModal = false">
      <div class="modal-content" @click.stop>
        <h2>创建新相册</h2>
        <form @submit.prevent="handleCreateAlbum">
          <div class="form-group">
            <label for="albumName">相册名称</label>
            <input 
              id="albumName" 
              v-model="newAlbum.name" 
              type="text" 
              required 
              placeholder="请输入相册名称"
            />
          </div>
          
          <div class="form-group">
            <label for="albumDescription">相册描述</label>
            <textarea 
              id="albumDescription" 
              v-model="newAlbum.description" 
              placeholder="请输入相册描述（可选）"
            />
          </div>
          
          <div class="form-group checkbox-group">
            <label>
              <input 
                v-model="newAlbum.isPublic" 
                type="checkbox"
              /> 公开相册
            </label>
            <p class="help-text">公开相册对所有用户可见</p>
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="showCreateModal = false">取消</button>
            <button type="submit" :disabled="creating">
              {{ creating ? '创建中...' : '创建' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

interface Album {
  id: number
  name: string
  description: string | null
  isPublic: boolean
  coverUrl: string | null
  mediaCount: number
  createdAt: string
}

interface NewAlbum {
  name: string
  description: string
  isPublic: boolean
}

const router = useRouter()
const showCreateModal = ref(false)
const creating = ref(false)
const albums = ref<Album[]>([])

const newAlbum = ref<NewAlbum>({
  name: '',
  description: '',
  isPublic: true
})

// 模拟相册数据
const mockAlbums: Album[] = [
  {
    id: 1,
    name: '我的旅行',
    description: '个人旅行照片集',
    isPublic: true,
    coverUrl: null,
    mediaCount: 32,
    createdAt: '2025-09-15T10:00:00Z'
  },
  {
    id: 2,
    name: '家庭聚会',
    description: '家庭私人聚会',
    isPublic: false,
    coverUrl: null,
    mediaCount: 18,
    createdAt: '2025-10-01T18:30:00Z'
  }
]

onMounted(() => {
  // 模拟加载相册数据
  albums.value = mockAlbums
})

const handleCreateAlbum = async () => {
  creating.value = true
  try {
    // TODO: 调用创建相册API
    console.log('Create album:', newAlbum.value)
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 重置表单并关闭模态框
    newAlbum.value = {
      name: '',
      description: '',
      isPublic: true
    }
    showCreateModal.value = false
    
    // 重新加载相册列表
    albums.value = [...mockAlbums, {
      id: Date.now(),
      ...newAlbum.value,
      coverUrl: null,
      mediaCount: 0,
      createdAt: new Date().toISOString()
    }]
  } catch (error) {
    console.error('Create album failed:', error)
  } finally {
    creating.value = false
  }
}

const goToAlbumDetail = (albumId: number) => {
  router.push(`/albums/${albumId}`)
}
</script>

<style scoped>
.my-albums-container {
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header h1 {
  margin: 0;
  color: #333;
}

.create-album-btn {
  padding: 0.5rem 1rem;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.create-album-btn:hover {
  background-color: #337ecc;
}

.album-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

.album-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.album-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.album-cover {
  height: 180px;
  overflow: hidden;
}

.album-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-cover {
  width: 100%;
  height: 100%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
}

.album-info {
  padding: 1rem;
}

.album-info h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
}

.description {
  margin: 0.5rem 0;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
}

.album-meta {
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

.empty-state button {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
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

.checkbox-group label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-group input {
  width: auto;
  margin-right: 0.5rem;
}

.help-text {
  font-size: 0.8rem;
  color: #999;
  margin: 0.25rem 0 0 1.5rem;
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
</style>