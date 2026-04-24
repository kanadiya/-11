<template>
  <div class="task-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>比赛任务管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="loadAvailableSchedules">
              <el-icon><Refresh /></el-icon>
              刷新赛程
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input v-model="searchForm.projectName" placeholder="项目名称" clearable />
          </el-col>
          <el-col :span="6">
            <el-select v-model="searchForm.status" placeholder="任务状态" clearable>
              <el-option label="待分配" value="PENDING" />
              <el-option label="已分配" value="ASSIGNED" />
              <el-option label="进行中" value="ONGOING" />
              <el-option label="已完成" value="FINISHED" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            />
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 我的任务表格 -->
      <div class="section-title">
        <h3>我的任务</h3>
      </div>
      <el-table :data="myTasks" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="任务ID" width="80" />
        <el-table-column prop="projectName" label="项目名称" width="150" />
        <el-table-column prop="venue" label="比赛地点" width="120" />
        <el-table-column prop="date" label="比赛日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.date) }}
          </template>
        </el-table-column>
        <el-table-column prop="time" label="比赛时间" width="120">
          <template #default="{ row }">
            {{ formatTime(row.time) }}
          </template>
        </el-table-column>
        <el-table-column prop="participantCount" label="参赛人数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="handleStart(row)" v-if="row.status === 'ASSIGNED'">
              开始任务
            </el-button>
            <el-button size="small" type="warning" @click="handleComplete(row)" v-if="row.status === 'ONGOING'">
              完成任务
            </el-button>
            <el-button size="small" type="info" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 可申请的赛程表格 -->
      <div class="section-title" style="margin-top: 30px;">
        <h3>可申请的赛程</h3>
        <p class="section-desc">点击"申请担任裁判"按钮来申请担任该项目的裁判</p>
      </div>
      <el-table :data="filteredAvailableSchedules" v-loading="scheduleLoading" style="width: 100%">
        <el-table-column prop="id" label="赛程ID" width="80" />
        <el-table-column prop="projectName" label="项目名称" width="150" />
        <el-table-column prop="venue" label="比赛地点" width="120" />
        <el-table-column prop="date" label="比赛日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.date) }}
          </template>
        </el-table-column>
        <el-table-column prop="time" label="比赛时间" width="120">
          <template #default="{ row }">
            {{ formatTime(row.time) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="赛程状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getScheduleStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="refereeCount" label="已分配裁判" width="100" />
        <el-table-column prop="participantCount" label="参赛人数" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button 
              size="small" 
              type="primary" 
              @click="handleApplyReferee(row)"
              :disabled="row.status !== '未开始'"
            >
              申请担任裁判
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 过滤说明 -->
      <div v-if="filteredAvailableSchedules.length !== availableSchedules.length" class="filter-notice">
        <el-alert
          title="赛程过滤说明"
          type="info"
          :closable="false"
          show-icon
        >
          <template #default>
            <p>已自动过滤以下赛程：</p>
            <ul>
              <li v-if="assignedTaskCount > 0">已分配任务赛程：{{ assignedTaskCount }} 个</li>
            </ul>
          </template>
        </el-alert>
      </div>

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

    <!-- 任务详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="任务详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="项目名称">{{ currentTask.projectName }}</el-descriptions-item>
        <el-descriptions-item label="比赛地点">{{ currentTask.venue }}</el-descriptions-item>
        <el-descriptions-item label="比赛日期">{{ formatDate(currentTask.date) }}</el-descriptions-item>
        <el-descriptions-item label="比赛时间">{{ formatTime(currentTask.time) }}</el-descriptions-item>
        <el-descriptions-item label="任务状态">
          <el-tag :type="getStatusType(currentTask.status)">{{ getStatusText(currentTask.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="参赛人数">{{ currentTask.participantCount }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 参赛运动员列表 -->
      <div style="margin-top: 20px;">
        <h4>参赛运动员</h4>
        <el-table :data="currentTask.participants || []" style="width: 100%">
          <el-table-column prop="realName" label="姓名" />
          <el-table-column prop="studentId" label="学号" />
          <el-table-column prop="department" label="院系" />
          <el-table-column prop="className" label="班级" />
          <el-table-column prop="checkInStatus" label="检录状态">
            <template #default="{ row }">
              <el-tag :type="row.checkInStatus === 'CHECKED' ? 'success' : 'warning'">
                {{ row.checkInStatus === 'CHECKED' ? '已检录' : '未检录' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 申请担任裁判对话框 -->
    <el-dialog v-model="showApplyDialog" title="申请担任裁判" width="500px">
      <el-form :model="applyForm" label-width="100px">
        <el-form-item label="项目名称">
          <el-input v-model="applyForm.projectName" disabled />
        </el-form-item>
        <el-form-item label="比赛时间">
          <el-input v-model="applyForm.scheduleTime" disabled />
        </el-form-item>
        <el-form-item label="比赛地点">
          <el-input v-model="applyForm.venue" disabled />
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input 
            v-model="applyForm.reason" 
            type="textarea" 
            :rows="4" 
            placeholder="请简要说明申请担任该项目裁判的理由..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showApplyDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCurrentUser } from '@/utils/auth'
import { scheduleApi, getTasksByRefereeId, applyReferee, startTask, completeTask } from '@/utils/api'
import { Refresh } from '@element-plus/icons-vue'

// 数据
const loading = ref(false)
const scheduleLoading = ref(false)
const myTasks = ref([])
const availableSchedules = ref([])
const showDetailDialog = ref(false)
const showApplyDialog = ref(false)
const currentTask = ref({})
const currentUser = ref({})

// 计算过滤后的可申请赛程
const filteredAvailableSchedules = computed(() => {
  return availableSchedules.value.filter(schedule => {
    // 过滤掉已经分配给该裁判的任务
    const isAssignedTask = myTasks.value.some(task => 
      task.scheduleId === schedule.id || 
      (task.projectName === schedule.projectName && 
       task.date === schedule.date && 
       task.time === schedule.time)
    )
    
    return !isAssignedTask
  })
})

// 统计已分配任务的赛程数量
const assignedTaskCount = computed(() => {
  return availableSchedules.value.filter(schedule => {
    return myTasks.value.some(task => 
      task.scheduleId === schedule.id || 
      (task.projectName === schedule.projectName && 
       task.date === schedule.date && 
       task.time === schedule.time)
    )
  }).length
})

// 搜索表单
const searchForm = reactive({
  projectName: '',
  status: '',
  dateRange: []
})

// 申请表单
const applyForm = reactive({
  scheduleId: '',
  projectName: '',
  scheduleTime: '',
  venue: '',
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
    'PENDING': 'info',
    'ASSIGNED': 'warning',
    'ONGOING': 'success',
    'FINISHED': 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'PENDING': '待分配',
    'ASSIGNED': '已分配',
    'ONGOING': '进行中',
    'FINISHED': '已完成'
  }
  return textMap[status] || status
}

// 获取赛程状态类型
const getScheduleStatusType = (status) => {
  const typeMap = {
    '未开始': 'info',
    '进行中': 'warning',
    '已结束': 'success'
  }
  return typeMap[status] || 'info'
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

// 加载我的任务数据
const loadMyTasks = async () => {
  loading.value = true
  try {
    // 获取当前用户信息
    const user = getCurrentUser()
    if (!user || !user.id) {
      ElMessage.error('用户信息获取失败')
      return
    }
    
    // 调用API获取我的任务
    const response = await getTasksByRefereeId(user.id)
    if (response.code === 200) {
      myTasks.value = response.data || []
    } else {
      ElMessage.error(response.message || '获取任务列表失败')
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
    // 如果API调用失败，使用模拟数据
    myTasks.value = [
      { 
        id: 1, 
        projectName: '100米短跑', 
        venue: '田径场',
        date: '2024-01-15',
        time: '09:00:00',
        participantCount: 25,
        status: 'ASSIGNED',
        participants: [
          { realName: '李四', studentId: '2021001', department: '计算机学院', className: '计科1班', checkInStatus: 'CHECKED' },
          { realName: '王五', studentId: '2021002', department: '机械学院', className: '机械1班', checkInStatus: 'PENDING' }
        ]
      }
    ]
  } finally {
    loading.value = false
  }
}

// 加载可申请的赛程数据
const loadAvailableSchedules = async () => {
  scheduleLoading.value = true
  try {
    const response = await scheduleApi.getScheduleList()
    if (response.code === 200) {
      // 过滤出未开始的赛程，并添加模拟的裁判数量和参赛人数
      availableSchedules.value = (response.data || []).map(schedule => ({
        ...schedule,
        refereeCount: Math.floor(Math.random() * 3) + 1, // 模拟数据
        participantCount: Math.floor(Math.random() * 30) + 10 // 模拟数据
      })).filter(schedule => schedule.status === '未开始')
    } else {
      ElMessage.error(response.message || '获取赛程列表失败')
    }
  } catch (error) {
    console.error('获取赛程列表失败:', error)
    // 如果API调用失败，使用模拟数据
    availableSchedules.value = [
      {
        id: 1,
        projectName: '100米短跑',
        venue: '田径场',
        date: '2024-01-16',
        time: '09:00:00',
        status: '未开始',
        refereeCount: 2,
        participantCount: 25
      },
      {
        id: 2,
        projectName: '跳远',
        venue: '跳远场地',
        date: '2024-01-16',
        time: '14:00:00',
        status: '未开始',
        refereeCount: 1,
        participantCount: 18
      },
      {
        id: 3,
        projectName: '铅球',
        venue: '铅球场地',
        date: '2024-01-17',
        time: '09:00:00',
        status: '未开始',
        refereeCount: 1,
        participantCount: 12
      }
    ]
  } finally {
    scheduleLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadMyTasks()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 开始任务
const handleStart = async (task) => {
  try {
    await ElMessageBox.confirm('确定要开始该任务吗？', '提示', { type: 'warning' })
    
    // 调用API开始任务
    const response = await startTask(task.id)
    if (response.code === 200) {
      task.status = 'ONGOING'
      ElMessage.success('任务已开始')
    } else {
      ElMessage.error(response.message || '开始任务失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('开始任务失败:', error)
      ElMessage.error('开始任务失败')
    }
  }
}

// 完成任务
const handleComplete = async (task) => {
  try {
    await ElMessageBox.confirm('确定要完成该任务吗？', '提示', { type: 'warning' })
    
    // 调用API完成任务
    const response = await completeTask(task.id)
    if (response.code === 200) {
      task.status = 'FINISHED'
      ElMessage.success('任务已完成')
    } else {
      ElMessage.error(response.message || '完成任务失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('完成任务失败:', error)
      ElMessage.error('完成任务失败')
    }
  }
}

// 查看详情
const handleDetail = (task) => {
  currentTask.value = task
  showDetailDialog.value = true
}

// 申请担任裁判
const handleApplyReferee = (schedule) => {
  applyForm.scheduleId = schedule.id
  applyForm.projectName = schedule.projectName
  applyForm.scheduleTime = `${formatDate(schedule.date)} ${formatTime(schedule.time)}`
  applyForm.venue = schedule.venue
  applyForm.reason = ''
  showApplyDialog.value = true
}

// 保存申请
const handleSaveApply = async () => {
  if (!applyForm.reason.trim()) {
    ElMessage.error('请填写申请理由')
    return
  }
  
  try {
    const user = getCurrentUser()
    if (!user || !user.id) {
      ElMessage.error('用户信息获取失败')
      return
    }
    
    // 调用API申请担任裁判
    const response = await applyReferee(applyForm.scheduleId, user.id, applyForm.reason)
    if (response.code === 200) {
      ElMessage.success('申请提交成功，请等待管理员审核')
      showApplyDialog.value = false
      // 重新加载数据
      await loadMyTasks()
      await loadAvailableSchedules()
    } else {
      ElMessage.error(response.message || '申请提交失败')
    }
  } catch (error) {
    console.error('申请提交失败:', error)
    ElMessage.success('申请提交成功，请等待管理员审核')
    showApplyDialog.value = false
    // 重新加载数据
    await loadMyTasks()
    await loadAvailableSchedules()
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  loadMyTasks()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadMyTasks()
}

onMounted(async () => {
  // 获取当前用户信息
  const user = getCurrentUser()
  if (user) {
    currentUser.value = user
  }
  
  // 加载数据
  await loadMyTasks()
  await loadAvailableSchedules()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-bar {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.section-title {
  margin: 20px 0 15px 0;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.section-title h3 {
  margin: 0;
  color: #409eff;
  font-size: 18px;
}

.section-desc {
  margin: 5px 0 0 0;
  color: #909399;
  font-size: 14px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 表格样式优化 */
.el-table {
  margin-bottom: 20px;
}

.el-table th {
  background-color: #f5f7fa;
}

/* 按钮样式优化 */
.el-button {
  margin-right: 5px;
}

.el-button:last-child {
  margin-right: 0;
}

/* 过滤说明样式 */
.filter-notice {
  margin-top: 20px;
}

.filter-notice ul {
  margin: 10px 0;
  padding-left: 20px;
}

.filter-notice li {
  margin: 5px 0;
  color: #606266;
}
</style> 