<template>
  <div class="user-page">
    <div class="page-header">
      <h3>用户管理</h3>
      <el-button type="primary" @click="openForm(null)">新增用户</el-button>
    </div>
    <el-card>
      <el-form :inline="true" :model="queryParams" style="margin-bottom:16px">
        <el-form-item label="关键词">
          <el-input v-model="queryParams.keyword" placeholder="用户名/姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openForm(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="handleAssignRole(row)">角色</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" size="small"
                       @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm title="确认删除?" @confirm="handleDelete(row)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top:16px;justify-content:flex-end"
        @change="loadData"
      />
    </el-card>

    <UserForm v-model:visible="formVisible" :user="currentUser" @saved="loadData" />

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="400px">
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox v-for="role in roles" :key="role.id" :label="role.id" style="display:flex;margin-bottom:8px">
          {{ role.name }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserPage, deleteUser, updateUserStatus, getUserRoles, assignUserRoles } from '../../../api/user'
import { getRolePage } from '../../../api/role'
import UserForm from './UserForm.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const formVisible = ref(false)
const currentUser = ref(null)
const roleDialogVisible = ref(false)
const selectedRoleIds = ref([])
const roles = ref([])
const currentRoleUserId = ref(null)

const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: null
})

async function loadData() {
  loading.value = true
  try {
    const res = await getUserPage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryParams.keyword = ''
  queryParams.status = null
  queryParams.page = 1
  loadData()
}

function openForm(user) {
  currentUser.value = user
  formVisible.value = true
}

async function handleDelete(row) {
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await updateUserStatus(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已启用' : '已禁用')
  loadData()
}

async function handleAssignRole(row) {
  currentRoleUserId.value = row.id
  const res = await getRolePage({ page: 1, size: 999 })
  roles.value = res.data.records
  const roleRes = await getUserRoles(row.id)
  selectedRoleIds.value = roleRes.data || []
  roleDialogVisible.value = true
}

async function saveRole() {
  await assignUserRoles(currentRoleUserId.value, selectedRoleIds.value)
  ElMessage.success('角色分配成功')
  roleDialogVisible.value = false
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
.page-header h3 {
  margin: 0;
  color: #303133;
}
</style>