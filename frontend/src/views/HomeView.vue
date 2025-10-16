<template>
  <div class="home-container">
    <div class="hero-section">
      <h1>Project Akyuu</h1>
      <p>在线相册系统 - 分享你的美好时光</p>
      <div class="hero-actions">
        <el-button 
          v-if="!userStore.isLoggedIn" 
          type="primary" 
          size="large" 
          @click="$router.push('/login')"
        >
          立即登录
        </el-button>
        <el-button 
          v-if="!userStore.isLoggedIn" 
          size="large" 
          @click="$router.push('/register')"
        >
          注册账号
        </el-button>
        <el-button 
          v-if="userStore.isLoggedIn" 
          type="primary" 
          size="large" 
          @click="$router.push('/albums/public')"
        >
          浏览公共相册
        </el-button>
        <el-button 
          v-if="userStore.isLoggedIn" 
          size="large" 
          @click="$router.push('/albums/mine')"
        >
          我的相册
        </el-button>
      </div>
    </div>
    
    <div class="features-section">
      <h2>功能特色</h2>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="feature-card">
            <el-icon size="48" color="#409EFF">
              <Picture />
            </el-icon>
            <h3>图片管理</h3>
            <p>轻松上传、管理和分享您的照片</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="feature-card">
            <el-icon size="48" color="#67C23A">
              <VideoPlay />
            </el-icon>
            <h3>视频支持</h3>
            <p>支持视频文件上传和在线预览</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="feature-card">
            <el-icon size="48" color="#E6A23C">
              <User />
            </el-icon>
            <h3>相册共享</h3>
            <p>创建私有相册，邀请好友共享</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="feature-card">
            <el-icon size="48" color="#F56C6C">
              <ChatLineRound />
            </el-icon>
            <h3>评论互动</h3>
            <p>对照片进行评论和回复互动</p>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
</script>

<style scoped>
.home-container {
  padding: 0;
}

.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  padding: 100px 20px;
}

.hero-section h1 {
  font-size: 48px;
  margin-bottom: 20px;
  font-weight: bold;
}

.hero-section p {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.hero-actions {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
}

.features-section {
  padding: 80px 20px;
  background: #f5f7fa;
}

.features-section h2 {
  text-align: center;
  font-size: 36px;
  margin-bottom: 60px;
  color: #303133;
}

.feature-card {
  background: white;
  padding: 40px 20px;
  text-align: center;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card h3 {
  margin: 20px 0 10px;
  font-size: 20px;
  color: #303133;
}

.feature-card p {
  color: #606266;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 60px 20px;
  }
  
  .hero-section h1 {
    font-size: 36px;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
