<template>
  <div class="perm-page">
    <div class="page-header">
      <h3>权限管理</h3>
      <el-button type="primary" @click="openForm(null)">新增权限</el-button>
    </div>
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <el-tree :data="treeData" :props="treeProps" node-key="id" default-expand-all highlight-current
                   @node-click="handleNodeClick" />
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card v-if="selectedPerm">
          <template #header>
            <span>权限详情</span>
            <div style="float:right">
              <el-button size="small" @click="openForm(selectedPerm)">编辑</el-button>
              <el-popconfirm title="确认删除?" @confirm="handleDelete(selectedPerm)">
                <template #reference>
                  <el-button size="small" type="danger">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="名称">{{ selectedPerm.name }}</el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag :type="typeTag(selectedPerm.type)" size="small">
                {{ typeLabel(selectedPerm.type) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="权限标识">{{ selectedPerm.perms || '-' }}</el-descriptions-item>
            <el-descriptions-item label="路由路径">{{ selectedPerm.path || '-' }}</el-descriptions-item>
            <el-descriptions-item label="组件">{{ selectedPerm.component || '-' }}</el-descriptions-item>
            <el-descriptions-item label="图标">{{ selectedPerm.icon || '-' }}</el-descriptions-item>
            <el-descriptions-item label="排序">{{ selectedPerm.sort }}</el-descriptions-item>
            <el-descriptions-item label="显示">
              <el-tag :type="selectedPerm.visible === 1 ? 'success' : 'info'" size="small">
                {{ selectedPerm.visible === 1 ? '显示' : '隐藏' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-card v-else>
          <p style="color:#909399;text-align:center">请在左侧选择一个权限节点</p>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="500px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="权限类型" prop="type">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="目录" :value="0" />
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级权限">
          <el-tree-select v-model="form.parentId" :data="treeData" :props="{ label: 'name', value: 'id' }"
                          placeholder="顶级" clearable check-strictly />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.perms" placeholder="如 sys:user:add" />
        </el-form-item>
        <el-form-item label="路由路径" v-if="form.type === 1">
          <el-input v-model="form.path" placeholder="如 /system/user" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.type === 1">
          <el-input v-model="form.component" placeholder="如 system/user/UserList" />
        </el-form-item>
        <el-form-item label="图标" v-if="form.type === 0 || form.type === 1">
          <el-input v-model="form.icon" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="是否显示" v-if="form.type === 0 || form.type === 1">
          <el-radio-group v-model="form.visible">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPermissionTree, createPermission, updatePermission, deletePermission } from '../../../api/permission'

const treeData = ref([])
const selectedPerm = ref(null)
const formVisible = ref(false)
const formRef = ref(null)
const submitting = ref(false)
const isEdit = computed(() => !!selectedPerm.value?.id)

const treeProps = { label: 'name', children: 'children' }

const form = ref({
  name: '', type: 1, parentId: null, path: '', component: '', perms: '', icon: '', sort: 0, visible: 1
})

const formRules = {
  name: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

function typeTag(type) {
  return ['', 'primary', 'success'][type] || 'info'
}

function typeLabel(type) {
  return ['目录', '菜单', '按钮'][type] || '未知'
}

async function loadTree() {
  const res = await getPermissionTree()
  treeData.value = res.data
}

function handleNodeClick(data) {
  selectedPerm.value = data
}

function openForm(perm) {
  if (perm) {
    form.value = { ...perm }
  } else {
    form.value = { name: '', type: 1, parentId: selectedPerm.value?.id || null, path: '', component: '', perms: '', icon: '', sort: 0, visible: 1 }
  }
  formVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (selectedPerm.value?.id && isEdit.value) {
      await updatePermission(selectedPerm.value.id, form.value)
      ElMessage.success('修改成功')
    } else {
      await createPermission(form.value)
      ElMessage.success('新增成功')
    }
    formVisible.value = false
    loadTree()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(perm) {
  await deletePermission(perm.id)
  ElMessage.success('删除成功')
  selectedPerm.value = null
  loadTree()
}

onMounted(() => {
  loadTree()
})
</script>

<style scoped>
.perm-page {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>