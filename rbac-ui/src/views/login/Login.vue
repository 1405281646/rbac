<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">RBAC 权限管理系统</h2>
      <el-form ref="formRef" :model="loginForm" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="captchaCode" v-if="captchaEnabled">
          <div style="display:flex;gap:8px;width:100%">
            <el-input v-model="loginForm.captchaCode" placeholder="验证码" style="flex:1" />
            <div class="captcha-box" @click="loadCaptcha">{{ captchaText }}</div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { getCaptcha } from '../../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const captchaEnabled = ref(false)
const captchaText = ref('')
const captchaUuid = ref('')

const loginForm = reactive({
  username: '',
  password: '',
  uuid: '',
  captchaCode: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function loadCaptcha() {
  try {
    const res = await getCaptcha()
    captchaUuid.value = res.data.uuid
    captchaEnabled.value = res.data.captchaEnabled === 'true'
    if (captchaEnabled.value) {
      captchaText.value = '获取验证码'
      loginForm.uuid = captchaUuid.value
    }
  } catch (e) {
    captchaEnabled.value = false
  }
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await authStore.login({
      ...loginForm,
      uuid: captchaUuid.value
    })
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) {
    if (captchaEnabled.value) loadCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
  font-size: 24px;
}
.captcha-box {
  width: 120px;
  height: 40px;
  background: #f0f2f5;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  user-select: none;
  font-size: 14px;
  color: #606266;
}
</style>