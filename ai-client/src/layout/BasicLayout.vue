<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { logout } from '../api/auth'
import { authState, clearAuth } from '../store/auth'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '')
const displayName = computed(
  () => authState.user?.realName || authState.user?.username || '未登录',
)

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  } catch {
    return
  }
  try {
    await logout()
  } catch {
    // 登出接口异常不影响本地清理
  }
  clearAuth()
  ElMessage.success('已退出登录')
  router.replace({ name: 'login' })
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="210px" class="layout-aside">
      <div class="layout-logo">学生信息管理系统</div>
      <el-menu :default-active="activeMenu" router class="layout-menu">
        <el-menu-item index="/students">
          <el-icon><User /></el-icon>
          <span>学生信息管理</span>
        </el-menu-item>
        <el-menu-item index="/classes">
          <el-icon><OfficeBuilding /></el-icon>
          <span>班级信息管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <span class="layout-title">{{ pageTitle }}</span>
        <div class="layout-user">
          <el-icon><Avatar /></el-icon>
          <span class="layout-username">{{ displayName }}</span>
          <el-button link type="primary" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  overflow-x: hidden;
}

.layout-logo {
  height: 60px;
  line-height: 60px;
  padding: 0 16px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  background-color: #263445;
}

.layout-menu {
  border-right: none;
  background-color: #304156;
}

.layout-menu :deep(.el-menu-item) {
  color: #bfcbd9;
}

.layout-menu :deep(.el-menu-item:hover) {
  background-color: #263445;
  color: #fff;
}

.layout-menu :deep(.el-menu-item.is-active) {
  background-color: #263445;
  color: #409eff;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
}

.layout-title {
  font-size: 16px;
  font-weight: 600;
}

.layout-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.layout-username {
  color: #606266;
}

.layout-main {
  background-color: #f5f7fa;
}
</style>
