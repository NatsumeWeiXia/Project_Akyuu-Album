<template>
  <div class="public-albums">
    <div class="page-header">
      <h1>公共相册</h1>
      <p>浏览所有公开的相册</p>
    </div>

    <div class="albums-container">
      <div v-if="albumStore.loading" class="loading">
        <el-loading text="加载中..." />
      </div>
      
      <div v-else-if="albumStore.publicAlbums.length === 0" class="empty-state">
        <el-empty description="暂无公共相册" />
      </div>
      
      <el-row v-else :gutter="20">
        <el-col 
          v-for="album in albumStore.publicAlbums" 
          :key="album.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6"
        >
          <el-card 
            class="album-card" 
            shadow="hover" 
            @click="$router.push(`/albums/${album.id}`)"
          >
            <div class="album-cover">
              <el-image
                v-if="album.coverImage"
                :src="album.coverImage"
                fit="cover"
                class="cover-image"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon size="48"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div v-else class="no-cover">
                <el-icon size="48"><Picture /></el-icon>
              </div>
            </div>
            
            <div class="album-info">
              <h3>{{ album.name }}</h3>
              <p class="album-description">{{ album.description || '暂无描述' }}</p>
              <div class="album-meta">
                <span class="media-count">
                  <el-icon><Picture /></el-icon>
                  {{ album.mediaCount || 0 }}
                </span>
                <span class="owner">
                  <el-icon><User /></el-icon>
                  {{ album.owner.nickname || album.owner.username }}
                </span>
              </div>
              <div class="album-date">
                <el-icon><Calendar /></el-icon>
                {{ formatDate(album.createdAt) }}
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAlbumStore } from '@/stores/album'

const albumStore = useAlbumStore()

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

onMounted(async () => {
  await albumStore.fetchPublicAlbums()
})
</script>

<style scoped>
.public-albums {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 36px;
  color: #303133;
  margin-bottom: 10px;
}

.page-header p {
  color: #909399;
  font-size: 16px;
}

.albums-container {
  min-height: 400px;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

.album-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.album-card:hover {
  transform: translateY(-5px);
}

.album-cover {
  height: 200px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: -20px -20px 15px -20px;
  border-radius: 4px 4px 0 0;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
}

.image-error,
.no-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.album-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.album-description {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 42px;
}

.album-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 14px;
  color: #909399;
}

.media-count,
.owner {
  display: flex;
  align-items: center;
  gap: 5px;
}

.album-date {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #c0c4cc;
}
</style>