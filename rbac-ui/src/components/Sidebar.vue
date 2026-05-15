<template>
  <div class="sidebar">
    <div class="sidebar-logo">RBAC</div>
    <el-menu
      :default-active="route.path"
      router
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
    >
      <el-menu-item index="/dashboard">
        <el-icon><HomeFilled /></el-icon>
        <span>仪表盘</span>
      </el-menu-item>
      <el-sub-menu index="/system">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/system/user">用户管理</el-menu-item>
        <el-menu-item index="/system/role">角色管理</el-menu-item>
        <el-menu-item index="/system/permission">权限管理</el-menu-item>
      </el-sub-menu>
    </el-menu>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { HomeFilled, Setting } from '@element-plus/icons-vue'

const route = useRoute()
</script>

<style scoped>
.sidebar {
  width: 220px;
  background: #304156;
  overflow-y: auto;
}
.sidebar-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.sidebar .el-menu {
  border-right: none;
}
</style>