<template>
  <div class="result-application-management">
    <div class="page-header">
      <h2>成绩录入申请管理</h2>
      <el-button type="primary" @click="loadApplications">
        <el-icon><Refresh /></el-icon>
        刷新申请
      </el-button>
    </div>
    
    <!-- 筛选条件 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <span>筛选条件</span>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="filterForm.status" placeholder="申请状态" clearable>
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input v-model="filterForm.projectName" placeholder="项目名称" clearable />
        </el-col>
        <el-col :span="6">
          <el-input v-model="filterForm.refereeName" placeholder="裁判姓名" clearable />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleFilter">筛选</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 申请列表 -->
    <el-card>
      <template #header>
        <span>申请列表</span>
      </template>
      
      <el-table :data="applications" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="申请ID" width="80" />
        <el-table-column prop="projectName" label="比赛项目" />
        <el-table-column prop="scheduleDate" label="比赛日期" />
        <el-table-column prop="scheduleTime" label="比赛时间" />
        <el-table-column prop="refereeName" label="申请裁判" />
        <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
        <el-table-column prop="estimatedTime" label="预计完成时间" />
        <el-table-column prop="applyTime" label="申请时间" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 'PENDING'"
              size="small" 
              type="success" 
              @click="approveApplication(scope.row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="scope.row.status === 'PENDING'"
              size="small" 
              type="danger" 
              @click="rejectApplication(scope.row)"
            >
              拒绝
            </el-button>
            <el-button size="small" @click="viewApplicationDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div v-if="applications.length === 0" class="no-data">
        <el-empty description="暂无申请记录" />
      </div>
    </el-card>
    
    <!-- 申请详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="申请详情"
      width="600px"
    >
      <div v-if="selectedApplication">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请ID">{{ selectedApplication.id }}</el-descriptions-item>
          <el-descriptions-item label="比赛项目">{{ selectedApplication.projectName }}</el-descriptions-item>
          <el-descriptions-item label="比赛日期">{{ selectedApplication.scheduleDate }}</el-descriptions-item>
          <el-descriptions-item label="比赛时间">{{ selectedApplication.scheduleTime }}</el-descriptions-item>
          <el-descriptions-item label="申请裁判">{{ selectedApplication.refereeName }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ selectedApplication.applyTime }}</el-descriptions-item>
          <el-descriptions-item label="预计完成时间">{{ selectedApplication.estimatedTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedApplication.status)">
              {{ getStatusText(selectedApplication.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <div style="margin-top: 20px;">
          <h4>申请理由：</h4>
          <p style="background: #f5f7fa; padding: 15px; border-radius: 4px;">
            {{ selectedApplication.reason }}
          </p>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { resultApi } from '@/utils/api'

// 响应式数据
const applications = ref([])
const loading = ref(false)
const detailDialogVisible = ref(false)
const selectedApplication = ref(null)

const filterForm = reactive({
  status: '',
  projectName: '',
  refereeName: ''
})

// 加载申请列表
const loadApplications = async () => {
  loading.value = true
  try {
    const response = await resultApi.getAllApplications()
    if (response.code === 200) {
      applications.value = response.data
    } else {
      ElMessage.error(response.message || '获取申请列表失败')
    }
  } catch (error) {
    console.error('获取申请列表失败:', error)
    ElMessage.error('获取申请列表失败')
  } finally {
    loading.value = false
  }
}

// 筛选申请
const handleFilter = () => {
  // 这里可以实现前端筛选或调用后端筛选API
  console.log('筛选条件:', filterForm)
  ElMessage.info('筛选功能待实现')
}

// 重置筛选
const resetFilter = () => {
  Object.assign(filterForm, {
    status: '',
    projectName: '',
    refereeName: ''
  })
  loadApplications()
}

// 通过申请
const approveApplication = async (application) => {
  try {
    await ElMessageBox.confirm(
      `确定要通过裁判 ${application.refereeName} 对 ${application.projectName} 的成绩录入申请吗？`,
      '确认通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await resultApi.approveApplication(application.id)
    if (response.code === 200) {
      ElMessage.success('申请已通过')
      await loadApplications()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('通过申请失败:', error)
      ElMessage.error('通过申请失败')
    }
  }
}

// 拒绝申请
const rejectApplication = async (application) => {
  try {
    await ElMessageBox.confirm(
      `确定要拒绝裁判 ${application.refereeName} 对 ${application.projectName} 的成绩录入申请吗？`,
      '确认拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await resultApi.rejectApplication(application.id)
    if (response.code === 200) {
      ElMessage.success('申请已拒绝')
      await loadApplications()
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('拒绝申请失败:', error)
      ElMessage.error('拒绝申请失败')
    }
  }
}

// 查看申请详情
const viewApplicationDetail = (application) => {
  selectedApplication.value = application
  detailDialogVisible.value = true
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return texts[status] || status
}

// 页面加载时获取申请列表
onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
.result-application-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.no-data {
  padding: 40px;
  text-align: center;
}
</style> 