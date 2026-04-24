<template>
  <div class="project-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目管理</span>
          <el-button type="primary" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            添加项目
          </el-button>
        </div>
      </template>

      <!-- 项目表格 -->
      <el-table :data="projects" v-loading="loading" style="width: 100%">
<!--        <el-table-column prop="id" label="ID" width="80" />-->
        <el-table-column prop="name" label="项目名称" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加项目对话框 -->
    <el-dialog 
      v-model="showAddDialog" 
      title="添加项目"
      width="400px"
    >
      <el-form 
        ref="projectFormRef" 
        :model="projectForm" 
        :rules="projectRules" 
        label-width="80px"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { projectApi } from '@/utils/api'

// 数据
const loading = ref(false)
const projects = ref([])
const showAddDialog = ref(false)
const projectFormRef = ref()

// 项目表单
const projectForm = reactive({
  name: ''
})

// 表单验证规则
const projectRules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' }
  ]
}

// 加载项目数据
const loadProjects = async () => {
  loading.value = true
  try {
    const response = await projectApi.getAllProjects()
    if (response.code === 200) {
      projects.value = response.data
    } else {
      ElMessage.error(response.message || '加载项目列表失败')
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

// 删除项目
const handleDelete = async (project) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除项目 "${project.name}" 吗？`,
      '确认删除',
      { type: 'warning' }
    )
    
    const response = await projectApi.deleteProject(project.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      loadProjects()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除项目失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 保存项目
const handleSaveProject = async () => {
  if (!projectFormRef.value) return
  
  try {
    await projectFormRef.value.validate()
    
    const response = await projectApi.createProject(projectForm)
    if (response.code === 200) {
      ElMessage.success('添加成功')
      showAddDialog.value = false
      loadProjects()
      resetForm()
    } else {
      ElMessage.error(response.message || '添加失败')
    }
  } catch (error) {
    console.error('添加项目失败:', error)
    ElMessage.error('操作失败')
  }
}

// 重置表单
const resetForm = () => {
  projectForm.name = ''
}

onMounted(() => {
  loadProjects()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style> 