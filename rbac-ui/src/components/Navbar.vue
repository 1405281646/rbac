<template>
  <div class="navbar">
    <span class="navbar-title">RBAC 权限管理系统</span>
    <div class="navbar-right">
      <span class="navbar-user">{{ authStore.userInfo?.realName || authStore.userInfo?.username }}</span>
      <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.navbar-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.navbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.navbar-user {
  font-size: 14px;
  color: #606266;
}
</style>