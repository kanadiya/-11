<template>
  <div class="test-page">
    <h1>功能测试页面</h1>
    
    <div class="test-section">
      <h2>后端API测试</h2>
      <el-button @click="testScheduleAPI">测试赛程API</el-button>
      <el-button @click="testProjectAPI">测试项目API</el-button>
      <el-button @click="testUserAPI">测试用户API</el-button>
    </div>
    
    <div class="test-section">
      <h2>前端功能测试</h2>
      <el-button @click="goToScheduleManagement">进入赛程管理</el-button>
      <el-button @click="goToProjectManagement">进入项目管理</el-button>
      <el-button @click="goToUserManagement">进入用户管理</el-button>
    </div>
    
    <div class="test-section">
      <h2>测试结果</h2>
      <pre>{{ testResults }}</pre>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { getScheduleList, getProjectList } from '@/utils/api'

const router = useRouter()
const testResults = ref('')

const testScheduleAPI = async () => {
  try {
    testResults.value = '正在测试赛程API...\n'
    const response = await getScheduleList()
    testResults.value += `赛程API测试成功: ${JSON.stringify(response, null, 2)}\n`
  } catch (error) {
    testResults.value += `赛程API测试失败: ${error.message}\n`
  }
}

const testProjectAPI = async () => {
  try {
    testResults.value += '正在测试项目API...\n'
    const response = await getProjectList()
    testResults.value += `项目API测试成功: ${JSON.stringify(response, null, 2)}\n`
  } catch (error) {
    testResults.value += `项目API测试失败: ${error.message}\n`
  }
}

const testUserAPI = async () => {
  testResults.value += '用户API测试功能待实现...\n'
}

const goToScheduleManagement = () => {
  router.push('/admin/schedules')
}

const goToProjectManagement = () => {
  router.push('/admin/projects')
}

const goToUserManagement = () => {
  router.push('/admin/users')
}
</script>

<style scoped>
.test-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.test-section h2 {
  margin-top: 0;
  color: #333;
}

.el-button {
  margin-right: 10px;
  margin-bottom: 10px;
}

pre {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 5px;
  white-space: pre-wrap;
  font-family: monospace;
  max-height: 400px;
  overflow-y: auto;
}
</style> 