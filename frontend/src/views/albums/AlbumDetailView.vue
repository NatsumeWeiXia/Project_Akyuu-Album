<template>
  <div class="album-detail">
    <div class="album-header">
      <el-button @click="$router.back()" :icon="ArrowLeft" circle />
      <div class="album-info">
        <h1>{{ albumStore.currentAlbum?.name }}</h1>
        <p>{{ albumStore.currentAlbum?.description }}</p>
        <div class="album-meta">
          <span class="owner">
            <el-icon><UserIcon /></el-icon>
            {{ albumStore.currentAlbum?.owner?.nickname || albumStore.currentAlbum?.owner?.username }}
          </span>
          <el-tag 
            :type="albumStore.currentAlbum?.isPublic ? 'success' : 'warning'" 
            size="small"
          >
            {{ albumStore.currentAlbum?.isPublic ? '公开' : '私有' }}
          </el-tag>
          <span class="media-count">
            <el-icon><Picture /></el-icon>
            {{ albumStore.currentAlbum?.mediaCount || 0 }} 张图片
          </span>
        </div>
      </div>
      
      <div class="album-actions">
        <el-upload
          v-if="canUpload"
          ref="uploadRef"
          action="#"
          :auto-upload="false"
          :show-file-list="false"
          :accept="'image/*,video/*'"
          :on-change="handleFileChange"
          multiple
        >
          <el-button type="primary" :icon="Upload">
            上传图片/视频
          </el-button>
        </el-upload>
        
        <el-button 
          v-if="canEdit" 
          @click="showEditDialog = true"
          :icon="Edit"
        >
          编辑相册
        </el-button>
        
                  <el-button 
                    v-if="canEdit" 
                    @click="showMemberDialog = true"
                    :icon="UserIcon"
                  >          成员管理
        </el-button>
        
        <el-button 
          v-if="canDelete" 
          type="danger" 
          @click="handleDelete"
          :icon="Delete"
        >
          删除相册
        </el-button>
      </div>
    </div>

    <!-- 媒体文件列表 -->
    <div class="media-grid">
      <div v-if="mediaStore.loading" class="loading">
        <el-loading text="加载中..." />
      </div>
      
      <div v-else-if="mediaStore.albumMedia.length === 0" class="empty-state">
        <el-empty description="暂无媒体文件">
          <el-upload
            v-if="canUpload"
            ref="uploadRef"
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :accept="'image/*,video/*'"
            :on-change="handleFileChange"
            multiple
          >
            <el-button type="primary" :icon="Upload">
              上传第一个文件
            </el-button>
          </el-upload>
        </el-empty>
      </div>
      
      <el-row v-else :gutter="20">
        <el-col 
          v-for="media in mediaStore.albumMedia" 
          :key="media.id" 
          :xs="24" 
          :sm="12" 
          :md="8" 
          :lg="6"
        >
          <div class="media-item" @click="openMediaDetail(media)">
            <div class="media-preview">
              <el-image
                v-if="media.type === 'image'"
                :src="media.thumbnailUrl || media.url"
                fit="cover"
                class="media-image"
              >
                <template #error>
                  <div class="image-error">
                    <el-icon size="48"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div v-else class="video-preview">
                <el-icon size="48"><VideoPlay /></el-icon>
                <span class="video-duration">{{ formatDuration(media.durationSeconds) }}</span>
              </div>
              <div class="media-overlay">
                <div class="media-actions">
                  <el-button
                    v-if="canDeleteMedia(media)"
                    type="danger"
                    size="small"
                    circle
                    @click.stop="handleDeleteMedia(media)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
            <div class="media-info">
              <p class="media-title">{{ media.filename }}</p>
              <p class="media-date">{{ formatDate(media.createdAt) }}</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 编辑相册对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑相册"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="80px"
      >
        <el-form-item label="相册名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入相册名称" />
        </el-form-item>
        
        <el-form-item label="相册描述" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入相册描述（可选）"
          />
        </el-form-item>
        
        <el-form-item label="访问权限" prop="isPublic">
          <el-radio-group v-model="editForm.isPublic">
            <el-radio :value="true">公开</el-radio>
            <el-radio :value="false">私有</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" :loading="albumStore.loading" @click="handleEdit">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 成员管理对话框 -->
    <el-dialog
      v-model="showMemberDialog"
      title="成员管理"
      width="600px"
    >
      <div class="member-list">
        <div class="member-item" v-for="member in albumMembers" :key="member.id">
          <div class="member-info">
            <el-avatar :size="32" :src="member?.avatar">
              {{ member?.nickname?.[0] || member?.username?.[0] || '?' }}
            </el-avatar>
            <div class="member-details">
              <span class="member-name">{{ member?.nickname || member?.username }}</span>
              <span class="member-role">{{ member.role }}</span>
            </div>
          </div>
          <el-button
            v-if="canRemoveMember(member)"
            type="danger"
            size="small"
            @click="handleRemoveMember(member)"
          >
            移除
          </el-button>
        </div>
      </div>
      
      <div class="add-member">
        <el-input
          v-model="newMemberUsername"
          placeholder="输入用户名添加成员"
          @keyup.enter="handleAddMember"
        >
          <template #append>
            <el-button type="primary" @click="handleAddMember">
              添加
            </el-button>
          </template>
        </el-input>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useAlbumStore } from '@/stores/album'
import { useMediaStore } from '@/stores/media'
import { useUserStore } from '@/stores/user'
import type { Media, User } from '@/types/api'
import {
  ArrowLeft,
  Upload,
  Edit,
  User as UserIcon,
  Delete,
  Picture,
  VideoPlay
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const albumStore = useAlbumStore()
const mediaStore = useMediaStore()
const userStore = useUserStore()

const albumId = computed(() => route.params.id as string)

const showEditDialog = ref(false)
const showMemberDialog = ref(false)
const editFormRef = ref<FormInstance>()
const uploadRef = ref()
const newMemberUsername = ref('')

const editForm = reactive({
  name: '',
  description: '',
  isPublic: false
})

const editRules: FormRules = {
  name: [
    { required: true, message: '请输入相册名称', trigger: 'blur' },
    { min: 2, max: 50, message: '相册名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '相册描述不能超过 500 个字符', trigger: 'blur' }
  ]
}

const canUpload = computed(() => {
  if (!albumStore.currentAlbum) return false
  if (albumStore.currentAlbum.owner?.id === userStore.user?.id) return true
  return albumStore.currentAlbum.members?.some(
    member => member.userId === userStore.user?.id && member.canUpload
  )
})

const canEdit = computed(() => {
  return albumStore.currentAlbum?.owner?.id === userStore.user?.id
})

const canDelete = computed(() => {
  return albumStore.currentAlbum?.owner?.id === userStore.user?.id
})

const albumMembers = computed(() => {
  return albumStore.currentAlbum?.members?.map(m => m.user).filter((user): user is User => !!user) || []
})

const canRemoveMember = (member: User) => {
  return albumStore.currentAlbum?.owner?.id === userStore.user?.id && 
         member.id !== userStore.user?.id
}

const canDeleteMedia = (media: Readonly<Media>) => {
  if (albumStore.currentAlbum?.owner?.id === userStore.user?.id) return true
  return media.uploaderId === userStore.user?.id
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const formatDuration = (seconds?: number) => {
  if (!seconds) return ''
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

const handleFileChange = (file: File) => {
  // 处理文件上传逻辑
  console.log('Selected file:', file)
}

const openMediaDetail = (media: Readonly<Media>) => {
  router.push(`/media/${media.id}`)
}

const handleEdit = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await albumStore.updateAlbum(parseInt(albumId.value), editForm.name, editForm.description, editForm.isPublic)
        ElMessage.success('相册更新成功')
        showEditDialog.value = false
      } catch  {
        ElMessage.error('相册更新失败')
      }
    }
  })
}

const handleDelete = () => {
  ElMessageBox.confirm(
    '确定要删除这个相册吗？相册中的所有媒体文件也将被删除。',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await albumStore.deleteAlbum(parseInt(albumId.value))
      ElMessage.success('相册删除成功')
      router.push('/albums/mine')
    } catch  {
      ElMessage.error('相册删除失败')
    }
  })
}

const handleDeleteMedia = async (media: Readonly<Media>) => {
  ElMessageBox.confirm(
    '确定要删除这个媒体文件吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await mediaStore.deleteMedia(media.id)
      ElMessage.success('媒体文件删除成功')
    } catch  {
      ElMessage.error('媒体文件删除失败')
    }
  })
}

const handleAddMember = async () => {
  if (!newMemberUsername.value.trim()) return
  
  try {
    await albumStore.addAlbumMember(parseInt(albumId.value), newMemberUsername.value.trim())
    ElMessage.success('成员添加成功')
    newMemberUsername.value = ''
  } catch  {
    ElMessage.error('成员添加失败')
  }
}

const handleRemoveMember = async (member: User) => {
  ElMessageBox.confirm(
    `确定要将 ${member?.nickname || member?.username || '该用户'} 移出相册吗？`,
    '移除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      await albumStore.removeAlbumMember(parseInt(albumId.value), member.id)
      ElMessage.success('成员移除成功')
    } catch  {
      ElMessage.error('成员移除失败')
    }
  })
}

onMounted(async () => {
  await albumStore.fetchAlbum(parseInt(albumId.value))
  await mediaStore.fetchAlbumMedia(parseInt(albumId.value))
  
  if (albumStore.currentAlbum) {
    editForm.name = albumStore.currentAlbum.name
    editForm.description = albumStore.currentAlbum.description || ''
    editForm.isPublic = albumStore.currentAlbum.isPublic
  }
})
</script>

<style scoped>
.album-detail {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.album-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.album-info {
  flex: 1;
}

.album-info h1 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #303133;
}

.album-info p {
  margin: 0 0 15px 0;
  color: #606266;
  line-height: 1.6;
}

.album-meta {
  display: flex;
  gap: 20px;
  align-items: center;
  font-size: 14px;
  color: #909399;
}

.album-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.album-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.media-grid {
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

.media-item {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.media-item:hover {
  transform: translateY(-5px);
}

.media-preview {
  position: relative;
  height: 200px;
  background: #f5f7fa;
  overflow: hidden;
}

.media-image {
  width: 100%;
  height: 100%;
}

.video-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  position: relative;
}

.video-duration {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.media-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.media-item:hover .media-overlay {
  opacity: 1;
}

.media-actions {
  display: flex;
  gap: 5px;
}

.media-info {
  padding: 15px;
}

.media-title {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.media-date {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.image-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
}

.member-list {
  margin-bottom: 20px;
}

.member-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.member-item:last-child {
  border-bottom: none;
}

.member-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.member-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.member-name {
  font-weight: 500;
  color: #303133;
}

.member-role {
  font-size: 12px;
  color: #909399;
}

.add-member {
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .album-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .album-actions {
    justify-content: center;
  }
}
</style>