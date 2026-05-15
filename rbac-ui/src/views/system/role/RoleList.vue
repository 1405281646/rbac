<template>
  <div class="role-page">
    <div class="page-header">
      <h3>角色管理</h3>
      <el-button type="primary" @click="openForm(null)">新增角色</el-button>
    </div>
    <el-card>
      <el-table :data="tableData" v-loading="loading" stripe row-key="id" default-expand-all :tree-props="{ children: 'children' }">
        <el-table-column prop="name" label="角色名称" width="180" />
        <el-table-column prop="code" label="角色编码" width="150" />
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column prop="builtIn" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.builtIn === 1 ? 'warning' : 'info'" size="small">
              {{ row.builtIn === 1 ? '内置' : '自定义' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="handlePermission(row)">权限</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small"
                       @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm title="确认删除?" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger" size="small" :disabled="row.builtIn === 1">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <RoleForm v-model:visible="formVisible" :role="currentRole" @saved="loadData" />
    <RolePermission v-model:visible="permVisible" :role="currentPermRole" @saved="loadData" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRoleTree, deleteRole, updateRoleStatus } from '../../../api/role'
import RoleForm from './RoleForm.vue'
import RolePermission from './RolePermission.vue'

const loading = ref(false)
const tableData = ref([])
const formVisible = ref(false)
const currentRole = ref(null)
const permVisible = ref(false)
const currentPermRole = ref(null)

async function loadData() {
  loading.value = true
  try {
    const res = await getRoleTree()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

function openForm(role) {
  currentRole.value = role
  formVisible.value = true
}

async function handleDelete(row) {
  await deleteRole(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await updateRoleStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadData()
}

function handlePermission(row) {
  currentPermRole.value = row
  permVisible.value = true
}

onMounted(loadData)
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.page-header h3 { margin: 0; }
</style>