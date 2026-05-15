<template>
  <div class="dashboard">
    <h2>欢迎使用 RBAC 权限管理系统</h2>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
const stats = [
  { label: '用户总数', value: '-' },
  { label: '角色总数', value: '-' },
  { label: '权限总数', value: '-' },
  { label: '系统版本', value: 'v1.0.0' }
]
</script>

<style scoped>
.dashboard h2 {
  color: #303133;
}
.stat-card {
  text-align: center;
  padding: 10px 0;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
</style>