<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="角色编码" prop="code">
        <el-input v-model="form.code" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="父角色">
        <el-tree-select v-model="form.parentId" :data="roleTree" :props="{ label: 'name', value: 'id' }"
                        placeholder="无" clearable check-strictly />
      </el-form-item>
      <el-form-item label="排序">
        <el-input-number v-model="form.sort" :min="0" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createRole, updateRole, getRoleTree, getRole } from '../../../api/role'

const props = defineProps({ visible: Boolean, role: Object })
const emit = defineEmits(['update:visible', 'saved'])

const formRef = ref(null)
const submitting = ref(false)
const roleTree = ref([])
const isEdit = computed(() => !!props.role?.id)

const form = reactive({
  name: '',
  code: '',
  parentId: null,
  sort: 0,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const dialogVisible = computed({
  get: () => props.visible,
  set: v => emit('update:visible', v)
})

watch(() => props.visible, async (v) => {
  if (v) {
    const treeRes = await getRoleTree()
    roleTree.value = treeRes.data
    if (props.role?.id) {
      const res = await getRole(props.role.id)
      Object.assign(form, res.data)
    } else {
      form.name = ''
      form.code = ''
      form.parentId = props.role?.id || null
      form.sort = 0
      form.remark = ''
    }
  }
})

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateRole(props.role.id, form)
      ElMessage.success('修改成功')
    } else {
      await createRole(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    emit('saved')
  } finally {
    submitting.value = false
  }
}
</script>