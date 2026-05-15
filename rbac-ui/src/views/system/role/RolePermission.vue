<template>
  <el-dialog v-model="dialogVisible" title="分配权限" width="450px">
    <el-tree
      ref="treeRef"
      :data="permissionTree"
      show-checkbox
      node-key="id"
      :props="{ label: 'name', children: 'children' }"
      default-expand-all
      check-strictly
    />
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getRolePermissions, assignRolePermissions } from '../../../api/role'
import { getPermissionTree } from '../../../api/permission'

const props = defineProps({ visible: Boolean, role: Object })
const emit = defineEmits(['update:visible', 'saved'])

const treeRef = ref(null)
const submitting = ref(false)
const permissionTree = ref([])

const dialogVisible = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v)
})

watch(() => props.visible, async (v) => {
  if (v && props.role) {
    const treeRes = await getPermissionTree()
    permissionTree.value = treeRes.data
    const permRes = await getRolePermissions(props.role.id)
    await nextTick()
    treeRef.value?.setCheckedKeys(permRes.data || [])
  }
})

async function handleSubmit() {
  submitting.value = true
  try {
    const checkedKeys = treeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = treeRef.value?.getHalfCheckedKeys() || []
    await assignRolePermissions(props.role.id, [...checkedKeys, ...halfCheckedKeys])
    ElMessage.success('权限分配成功')
    dialogVisible.value = false
    emit('saved')
  } finally {
    submitting.value = false
  }
}
</script>