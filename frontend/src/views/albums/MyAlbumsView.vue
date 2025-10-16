<template>
  <div class="my-albums">
    <div class="page-header">
      <h1>我的相册</h1>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        创建相册
      </el-button>
    </div>

    <div class="albums-container">
      <div v-if="albumStore.loading" class="loading">
        <el-loading text="加载中..." />
      </div>
      
      <div v-else-if="albumStore.myAlbums.length === 0" class="empty-state">
        <el-empty description="暂无相册">
          <el-button type="primary" @click="showCreateDialog = true">
            创建第一个相册
          </el-button>
        </el-empty>
      </div>
      
      <el-row v-else :gutter="20">
        <el-col 
          v-for="album in albumStore.myAlbums" 
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
                <el-tag 
                  :type="album.isPublic ? 'success' : 'warning'" 
                  size="small"
                >
                  {{ album.isPublic ? '公开' : '私有' }}
                </el-tag>
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

    <!-- 创建相册对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建相册"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="80px"
      >
        <el-form-item label="相册名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入相册名称" />
        </el-form-item>
        
        <el-form-item label="相册描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入相册描述（可选）"
          />
        </el-form-item>
        
        <el-form-item label="访问权限" prop="isPublic">
          <el-radio-group v-model="createForm.isPublic">
            <el-radio :value="true">公开</el-radio>
            <el-radio :value="false">私有</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" :loading="albumStore.loading" @click="handleCreate">
            创建
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useAlbumStore } from '@/stores/album'

const albumStore = useAlbumStore()
const showCreateDialog = ref(false)
const createFormRef = ref<FormInstance>()

const createForm = reactive({
  name: '',
  description: '',
  isPublic: false
})

const createRules: FormRules = {
  name: [
    { required: true, message: '请输入相册名称', trigger: 'blur' },
    { min: 2, max: 50, message: '相册名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '相册描述不能超过 500 个字符', trigger: 'blur' }
  ]
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleCreate = async () => {
  if (!createFormRef.value) return
  
  await createFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await albumStore.createAlbum(createForm)
        ElMessage.success('相册创建成功')
        showCreateDialog.value = false
        // 重置表单
        createForm.name = ''
        createForm.description = ''
        createForm.isPublic = false
      } catch (error) {
        ElMessage.error('相册创建失败')
      }
    }
  })
}

onMounted(async () => {
  await albumStore.fetchMyAlbums()
})
</script>

<style scoped>
.my-albums {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 36px;
  color: #303133;
  margin: 0;
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

.media-count {
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