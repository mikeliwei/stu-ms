<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'
import { setAuth } from '../store/auth'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const loading = ref(false)

// 演示账号，取自 02_data.sql
const form = reactive({
  username: 'admin',
  password: '123456',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const data = await login({ ...form })
    setAuth(data)
    ElMessage.success('登录成功')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/students'
    router.replace(redirect)
  } catch {
    // 错误提示由 axios 拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <el-card class="login-card" shadow="always">
      <h2 class="login-title">学生信息管理系统</h2>
      <p class="login-subtitle">用户登录</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleSubmit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" clearable>
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password>
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" class="login-button" :loading="loading" @click="handleSubmit">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <el-alert type="info" :closable="false" show-icon title="演示账号：admin / 123456" />
    </el-card>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #304156 0%, #409eff 100%);
}

.login-card {
  width: 400px;
  padding: 8px 12px;
}

.login-title {
  margin: 0 0 4px;
  text-align: center;
  font-size: 20px;
}

.login-subtitle {
  margin: 0 0 24px;
  text-align: center;
  color: #909399;
}

.login-button {
  width: 100%;
}
</style>
