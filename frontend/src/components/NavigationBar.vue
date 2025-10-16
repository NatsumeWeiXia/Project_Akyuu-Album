<template>
  <el-menu
    :default-active="activeIndex"
    class="nav-menu"
    mode="horizontal"
    background-color="#545c64"
    text-color="#fff"
    active-text-color="#ffd04b"
    router
  >
    <el-menu-item index="/">
      <el-icon><House /></el-icon>
      <span>首页</span>
    </el-menu-item>
    
    <template v-if="userStore.isLoggedIn">
      <el-menu-item index="/albums/public">
        <el-icon><Picture />
        </el-icon>
        <span>公共相册</span>
      </el-menu-item>
      
      <el-menu-item index="/albums/mine">
        <el-icon><Folder />
        </el-icon>
        <span>我的相册</span>
      </el-menu-item>
      
      <el-menu-item index="/api-test">
        <el-icon><Tools /></el-icon>
        <span>API测试</span>
      </el-menu-item>
      
      <el-sub-menu index="profile">
        <template #title>
          <el-icon><User /></el-icon>
          <span>{{ userStore.user?.nickname || userStore.user?.username }}</span>
        </template>
        <el-menu-item index="/profile">
          <el-icon><UserFilled /></el-icon>
          个人资料
        </el-menu-item>
        <el-menu-item @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-menu-item>
      </el-sub-menu>
    </template>
    
    <template v-else>
      <el-menu-item index="/login" style="margin-left: auto;">
        <el-icon><Key /></el-icon>
        <span>登录</span>
      </el-menu-item>
      
      <el-menu-item index="/register">
        <el-icon><User /></el-icon>
        <span>注册</span>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { Tools } from '@element-plus/icons-vue'

const route = useRoute()
const userStore = useUserStore()

const activeIndex = ref('/')

watch(
  () => route.path,
  (newPath) => {
    activeIndex.value = newPath
  },
  { immediate: true }
)

const handleLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    userStore.logout()
  })
}
</script>

<style scoped>
.nav-menu {
  border-bottom: none;
}
</style>