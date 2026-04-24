<template>
  <div class="registration-review">
    <div class="page-header">
      <h2>报名审核</h2>
      <p>审核用户的比赛报名申请</p>
    </div>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="searchForm.projectName" 
            placeholder="项目名称" 
            clearable 
            @clear="handleSearch"
          />
        </el-col>
        <el-col :span="6">
          <el-select 
            v-model="searchForm.status" 
            placeholder="审核状态" 
            clearable 
            @change="handleSearch"
          >
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleSearch"
          />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 报名列表 -->
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>报名申请列表</span>
          <div class="header-actions">
            <el-button type="success" @click="handleBatchApprove" :disabled="selectedRegistrations.length === 0">
              <el-icon><Check /></el-icon>
              批量通过
            </el-button>
            <el-button type="danger" @click="handleBatchReject" :disabled="selectedRegistrations.length === 0">
              <el-icon><Close /></el-icon>
              批量拒绝
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table 
        :data="registrations" 
        v-loading="loading" 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
<!--        <el-table-column prop="id" label="申请ID" width="80" />-->
        <el-table-column label="申请人信息" width="200">
          <template #default="{ row }">
            <div class="user-info">
              <div class="user-name">{{ row.username }}</div>
              <div class="user-detail">{{ row.phone || '手机号未知' }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="比赛项目" width="150" />
        <el-table-column label="比赛时间" width="180">
          <template #default="{ row }">
            <div>{{ formatDate(row.scheduleDate) }}</div>
            <div>{{ formatTime(row.scheduleTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="venue" label="比赛场地" width="120" />
        <el-table-column label="报名时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.registerTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="success" 
              @click="handleApprove(row)"
              v-if="row.status === 'PENDING'"
            >
              通过
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleReject(row)"
              v-if="row.status === 'PENDING'"
            >
              拒绝
            </el-button>
            <el-button 
              size="small" 
              type="info" 
              @click="handleDetail(row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="报名详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRegistration">
        <el-descriptions-item label="申请ID">{{ currentRegistration.id }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentRegistration.username }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentRegistration.phone || '未知' }}</el-descriptions-item>
        <el-descriptions-item label="比赛项目">{{ currentRegistration.projectName }}</el-descriptions-item>
        <el-descriptions-item label="比赛日期">{{ formatDate(currentRegistration.scheduleDate) }}</el-descriptions-item>
        <el-descriptions-item label="比赛时间">{{ formatTime(currentRegistration.scheduleTime) }}</el-descriptions-item>
        <el-descriptions-item label="比赛场地">{{ currentRegistration.venue }}</el-descriptions-item>
        <el-descriptions-item label="报名时间">{{ formatDateTime(currentRegistration.registerTime) }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="getStatusType(currentRegistration.status)">
            {{ getStatusText(currentRegistration.status) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            type="success" 
            @click="handleApprove(currentRegistration)"
            v-if="currentRegistration && currentRegistration.status === 'PENDING'"
          >
            通过申请
          </el-button>
          <el-button 
            type="danger" 
            @click="handleReject(currentRegistration)"
            v-if="currentRegistration && currentRegistration.status === 'PENDING'"
          >
            拒绝申请
          </el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 拒绝原因对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="500px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因" required>
          <el-input 
            v-model="rejectForm.reason" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入拒绝原因..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { registrationApi, taskApi } from '@/utils/api'
import { getCurrentUser } from '@/utils/auth'
import { Check, Close } from '@element-plus/icons-vue'

// 数据
const loading = ref(false)
const registrations = ref([])
const allRegistrations = ref([])
const selectedRegistrations = ref([])
const detailDialogVisible = ref(false)
const rejectDialogVisible = ref(false)
const currentRegistration = ref(null)
const refereeScheduleIds = ref([])

// 搜索表单
const searchForm = reactive({
  projectName: '',
  status: '',
  dateRange: []
})

// 拒绝表单
const rejectForm = reactive({
  registrationId: null,
  reason: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return textMap[status] || status
}

// 获取当前裁判负责的赛程ID集合
const loadRefereeScheduleIds = async () => {
  const currentUser = getCurrentUser()
  if (!currentUser || !currentUser.id) {
    throw new Error('未获取到当前裁判信息，请重新登录')
  }

  const taskResponse = await taskApi.getTasksByRefereeId(currentUser.id)
  if (taskResponse.code !== 200) {
    throw new Error(taskResponse.message || '获取裁判任务失败')
  }

  const ids = (taskResponse.data || [])
    // 只要该任务绑定了赛程，就纳入可审核范围，避免状态值不一致导致数据被误过滤
    .filter(task => task && task.scheduleId != null)
    .map(task => task.scheduleId)

  refereeScheduleIds.value = [...new Set(ids)]
}

// 本地筛选（仅针对当前裁判负责赛程的数据）
const applySearchFilters = () => {
  let list = [...allRegistrations.value]

  if (searchForm.projectName) {
    const keyword = searchForm.projectName.trim()
    list = list.filter(item => (item.projectName || '').includes(keyword))
  }

  if (searchForm.status) {
    list = list.filter(item => item.status === searchForm.status)
  }

  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const start = new Date(searchForm.dateRange[0])
    const end = new Date(searchForm.dateRange[1])
    list = list.filter(item => {
      if (!item.scheduleDate) return false
      const d = new Date(item.scheduleDate)
      return d >= start && d <= end
    })
  }

  registrations.value = list
  pagination.total = list.length
}

// 加载报名数据（仅当前裁判负责赛程）
const loadRegistrations = async () => {
  loading.value = true
  try {
    await loadRefereeScheduleIds()

    if (refereeScheduleIds.value.length === 0) {
      allRegistrations.value = []
      registrations.value = []
      pagination.total = 0
      return
    }

    // 并行拉取当前裁判每个赛程的报名记录
    const responses = await Promise.all(
      refereeScheduleIds.value.map(scheduleId => registrationApi.getRegistrationsBySchedule(scheduleId))
    )

    const merged = []
    for (const res of responses) {
      if (res.code === 200 && Array.isArray(res.data)) {
        merged.push(...res.data)
      }
    }

    // 去重（避免异常情况下重复）
    const map = new Map()
    merged.forEach(item => {
      if (item && item.id != null) {
        map.set(item.id, item)
      }
    })
    allRegistrations.value = Array.from(map.values())
    applySearchFilters()
  } catch (error) {
    console.error('获取报名列表失败:', error)
    ElMessage.error(error.message || '获取报名列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  applySearchFilters()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = key === 'dateRange' ? [] : ''
  })
  handleSearch()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRegistrations.value = selection
}

// 查看详情
const handleDetail = (registration) => {
  currentRegistration.value = registration
  detailDialogVisible.value = true
}

// 通过申请
const handleApprove = async (registration) => {
  try {
    await ElMessageBox.confirm(
      `确定要通过 ${registration.username} 的报名申请吗？`,
      '确认通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success',
      }
    )
    
    const response = await registrationApi.updateRegistrationStatus(registration.id, 'APPROVED')
    if (response.code === 200) {
      ElMessage.success('申请已通过')
      loadRegistrations()
      if (detailDialogVisible.value) {
        detailDialogVisible.value = false
      }
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('通过申请失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 拒绝申请
const handleReject = (registration) => {
  rejectForm.registrationId = registration.id
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

// 确认拒绝
const confirmReject = async () => {
  if (!rejectForm.reason.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  
  try {
    const response = await registrationApi.updateRegistrationStatus(
      rejectForm.registrationId, 
      'REJECTED'
    )
    if (response.code === 200) {
      ElMessage.success('申请已拒绝')
      rejectDialogVisible.value = false
      loadRegistrations()
      if (detailDialogVisible.value) {
        detailDialogVisible.value = false
      }
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    console.error('拒绝申请失败:', error)
    ElMessage.error('操作失败')
  }
}

// 批量通过
const handleBatchApprove = async () => {
  if (selectedRegistrations.value.length === 0) {
    ElMessage.warning('请选择要操作的申请')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要通过选中的 ${selectedRegistrations.value.length} 个申请吗？`,
      '批量通过',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success',
      }
    )
    
    const promises = selectedRegistrations.value.map(registration =>
      registrationApi.updateRegistrationStatus(registration.id, 'APPROVED')
    )
    
    await Promise.all(promises)
    ElMessage.success('批量通过成功')
    loadRegistrations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量通过失败:', error)
      ElMessage.error('批量操作失败')
    }
  }
}

// 批量拒绝
const handleBatchReject = async () => {
  if (selectedRegistrations.value.length === 0) {
    ElMessage.warning('请选择要操作的申请')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要拒绝选中的 ${selectedRegistrations.value.length} 个申请吗？`,
      '批量拒绝',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const promises = selectedRegistrations.value.map(registration =>
      registrationApi.updateRegistrationStatus(registration.id, 'REJECTED')
    )
    
    await Promise.all(promises)
    ElMessage.success('批量拒绝成功')
    loadRegistrations()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量拒绝失败:', error)
      ElMessage.error('批量操作失败')
    }
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  applySearchFilters()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  applySearchFilters()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  if (typeof date === 'string') {
    return date.split('T')[0]
  }
  return date
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  if (typeof time === 'string') {
    return time.split('T')[1]?.substring(0, 5) || time
  }
  return time
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  if (typeof dateTime === 'string') {
    return dateTime.replace('T', ' ').substring(0, 19)
  }
  return dateTime
}

onMounted(() => {
  loadRegistrations()
})
</script>

<style scoped>
.registration-review {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #333;
}

.page-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.search-card {
  margin-bottom: 20px;
}

.list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.user-detail {
  font-size: 12px;
  color: #666;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 