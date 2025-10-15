<template>
  <div class="public-albums-container">
    <div class="header">
      <h1>公共相册</h1>
      <div class="search-box">
        <input 
          v-model="searchKeyword" 
          type="text" 
          placeholder="搜索相册..." 
          @input="handleSearch"
        />
      </div>
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
            <span>创建者: {{ album.ownerName }}</span>
            <span>{{ album.mediaCount }} 张照片</span>
          </div>
        </div>
      </div>
    </div>
    
    <div v-if="albums.length === 0" class="empty-state">
      <p>暂无公共相册</p>
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
  coverUrl: string | null
  ownerName: string
  mediaCount: number
  createdAt: string
}

const router = useRouter()
const searchKeyword = ref('')
const albums = ref<Album[]>([])

// 模拟相册数据
const mockAlbums: Album[] = [
  {
    id: 1,
    name: '风景照片',
    description: '美丽的自然风景照片集',
    coverUrl: null,
    ownerName: '张三',
    mediaCount: 24,
    createdAt: '2025-10-01T10:00:00Z'
  },
  {
    id: 2,
    name: '聚会照片',
    description: '朋友聚会的欢乐时光',
    coverUrl: null,
    ownerName: '李四',
    mediaCount: 15,
    createdAt: '2025-10-05T14:30:00Z'
  }
]

onMounted(() => {
  // 模拟加载相册数据
  albums.value = mockAlbums
})

const handleSearch = () => {
  // TODO: 实现搜索功能
  console.log('Search keyword:', searchKeyword.value)
}

const goToAlbumDetail = (albumId: number) => {
  router.push(`/albums/${albumId}`)
}
</script>

<style scoped>
.public-albums-container {
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

.search-box input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
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
</style>