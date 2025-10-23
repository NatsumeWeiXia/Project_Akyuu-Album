<template>
  <div class="profile">
    <div class="profile-header">
      <h1>个人资料</h1>
    </div>

    <el-card class="profile-card">
      <div class="profile-content">
        <div class="avatar-section">
          <el-avatar
            :size="120"
            :src="userStore.user?.avatar"
            class="user-avatar"
          >
            {{ userStore.user?.nickname?.[0] || userStore.user?.username?.[0] || '?' }}
          </el-avatar>
          <el-upload
            class="avatar-upload"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :accept="'image/*'"
            :on-change="handleAvatarChange"
          >
            <el-button type="primary" size="small" :icon="Camera">
              更换头像
            </el-button>
          </el-upload>
        </div>

        <div class="info-section">
          <el-form
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            
            <el-form-item label="个人简介" prop="bio">
              <el-input
                v-model="profileForm.bio"
                type="textarea"
                :rows="3"
                placeholder="介绍一下自己（可选）"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="handleUpdateProfile" :loading="userStore.loading">
                保存修改
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>

    <!-- 统计信息 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon size="32" color="#409EFF"><Picture /></el-icon>
            <div class="stat-info">
              <p class="stat-number">{{ userStats.totalAlbums }}</p>
              <p class="stat-label">相册数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon size="32" color="#67C23A"><Files /></el-icon>
            <div class="stat-info">
              <p class="stat-number">{{ userStats.totalMedia }}</p>
              <p class="stat-label">媒体文件</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-item">
            <el-icon size="32" color="#E6A23C"><ChatLineRound /></el-icon>
            <div class="stat-info">
              <p class="stat-number">{{ userStats.totalComments }}</p>
              <p class="stat-label">评论数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 我的相册列表 -->
    <el-card class="albums-card">
      <template #header>
        <div class="card-header">
          <span>我的相册</span>
          <el-button type="primary" size="small" @click="$router.push('/albums/mine')">
            管理相册
          </el-button>
        </div>
      </template>
      
      <div v-if="albumStore.loading" class="loading">
        <el-loading text="加载中..." />
      </div>
      
      <div v-else-if="albumStore.myAlbums.length === 0" class="empty-state">
        <el-empty description="暂无相册">
          <el-button type="primary" @click="$router.push('/albums/mine')">
            创建第一个相册
          </el-button>
        </el-empty>
      </div>
      
      <div v-else class="albums-list">
        <div 
          v-for="album in albumStore.myAlbums.slice(0, 6)" 
          :key="album.id"
          class="album-item"
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
                  <el-icon size="32"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div v-else class="no-cover">
              <el-icon size="32"><Picture /></el-icon>
            </div>
          </div>
          <div class="album-info">
            <h4>{{ album.name }}</h4>
            <p>{{ album.mediaCount || 0 }} 张图片</p>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useAlbumStore } from '@/stores/album'
import { Camera, Picture, Files, ChatLineRound } from '@element-plus/icons-vue'

const userStore = useUserStore()
const albumStore = useAlbumStore()
const profileFormRef = ref<FormInstance>()

const profileForm = reactive({
  username: userStore.user?.username || '',
  nickname: userStore.user?.nickname || '',
  email: userStore.user?.email || '',
  bio: userStore.user?.bio || ''
})

const profileRules: FormRules = {
  nickname: [
    { max: 50, message: '昵称不能超过 50 个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  bio: [
    { max: 200, message: '个人简介不能超过 200 个字符', trigger: 'blur' }
  ]
}

const userStats = reactive({
  totalAlbums: 0,
  totalMedia: 0,
  totalComments: 0
})

const handleAvatarChange = (file: File) => {
  // 处理头像上传逻辑
  console.log('Selected avatar file:', file)
}

const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userStore.updateCurrentUser({
          nickname: profileForm.nickname,
          email: profileForm.email,
          bio: profileForm.bio
        })
        ElMessage.success('个人资料更新成功')
      } catch  {
        ElMessage.error('个人资料更新失败')
      }
    }
  })
}

const resetForm = () => {
  profileForm.nickname = userStore.user?.nickname || ''
  profileForm.email = userStore.user?.email || ''
  profileForm.bio = userStore.user?.bio || ''
}

const loadUserStats = async () => {
  // 这里可以调用相应的API获取用户统计信息
  // 暂时使用模拟数据
  userStats.totalAlbums = albumStore.myAlbums.length
  userStats.totalMedia = albumStore.myAlbums.reduce((sum, album) => sum + (album.mediaCount || 0), 0)
  userStats.totalComments = 0 // 需要从评论API获取
}

onMounted(async () => {
  await albumStore.fetchMyAlbums()
  loadUserStats()
})
</script>

<style scoped>
.profile {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-header {
  margin-bottom: 30px;
}

.profile-header h1 {
  margin: 0;
  font-size: 32px;
  color: #303133;
}

.profile-card {
  margin-bottom: 20px;
}

.profile-content {
  display: flex;
  gap: 40px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  border: 3px solid #f0f0f0;
}

.info-section {
  flex: 1;
}

.profile-form {
  max-width: 500px;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  height: 100%;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.albums-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.albums-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.album-item {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.album-item:hover {
  transform: translateY(-5px);
}

.album-cover {
  height: 150px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
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

.album-info {
  padding: 15px;
}

.album-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.album-info p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
    align-items: center;
  }
  
  .albums-list {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>