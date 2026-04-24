<template>
  <div class="referee-assignment">
    <div class="page-header">
      <h2>裁判分配管理</h2>
      <p>为比赛分配裁判，确保比赛顺利进行</p>
    </div>
    
    <!-- 赛程类型选择 -->
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-radio-group v-model="scheduleType" @change="loadSchedulesByType">
            <el-radio-button label="upcoming">即将开始的比赛</el-radio-button>
            <el-radio-button label="ongoing">进行中的比赛</el-radio-button>
            <el-radio-button label="completed">已完成的比赛</el-radio-button>
          </el-radio-group>
        </el-col>
        <el-col :span="8">
          <el-select v-model="selectedProject" placeholder="选择项目" clearable @change="filterSchedules">
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" @click="loadSchedulesByType">刷新</el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 赛程列表 -->
    <el-card class="schedule-card">
      <template #header>
        <span>{{ getScheduleTypeText() }}列表</span>
      </template>
      
      <el-table :data="filteredSchedules" style="width: 100%">
        <el-table-column prop="projectName" label="比赛项目" />
        <el-table-column prop="venue" label="场地" />
        <el-table-column prop="scheduleDate" label="比赛日期" />
        <el-table-column prop="scheduleTime" label="比赛时间" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="裁判分配" width="200">
          <template #default="scope">
            <div v-if="scope.row.referees && scope.row.referees.length > 0">
              <el-tag 
                v-for="referee in scope.row.referees" 
                :key="referee.id"
                style="margin-right: 5px; margin-bottom: 5px;"
              >
                {{ referee.realName }}
              </el-tag>
            </div>
            <span v-else style="color: #999;">未分配</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" type="primary" @click="assignReferee(scope.row)">
              分配裁判
            </el-button>
            <el-button 
              v-if="scheduleType === 'completed' && (!scope.row.referees || scope.row.referees.length === 0)"
              size="small" 
              type="success" 
              @click="assignRefereeForCompleted(scope.row)"
            >
              分配录入裁判
            </el-button>
            <el-button size="small" type="info" @click="viewDetails(scope.row)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div v-if="filteredSchedules.length === 0" class="no-data">
        <el-empty :description="`暂无${getScheduleTypeText()}`" />
      </div>
    </el-card>
    
    <!-- 分配裁判对话框 -->
    <el-dialog v-model="assignDialogVisible" title="分配裁判" width="600px">
      <el-form :model="assignForm" label-width="100px">
        <el-form-item label="比赛项目">
          <el-input v-model="assignForm.projectName" disabled />
        </el-form-item>
        <el-form-item label="比赛时间">
          <el-input v-model="assignForm.scheduleTime" disabled />
        </el-form-item>
        <el-form-item label="选择裁判" required>
          <el-select 
            v-model="assignForm.refereeIds" 
            multiple 
            placeholder="请选择裁判"
            style="width: 100%"
          >
            <el-option
              v-for="referee in availableReferees"
              :key="referee.id"
              :label="referee.realName || referee.username || `裁判-${referee.id}`"
              :value="referee.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分配原因">
          <el-input
            v-model="assignForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入分配原因（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAssign">确认分配</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="赛程详情" width="800px">
      <el-descriptions :column="2" border v-if="currentSchedule">
        <el-descriptions-item label="比赛项目">{{ currentSchedule.projectName }}</el-descriptions-item>
        <el-descriptions-item label="比赛场地">{{ currentSchedule.venue }}</el-descriptions-item>
        <el-descriptions-item label="比赛日期">{{ currentSchedule.scheduleDate }}</el-descriptions-item>
        <el-descriptions-item label="比赛时间">{{ currentSchedule.scheduleTime }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="getStatusType(currentSchedule.status)">
            {{ getStatusText(currentSchedule.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报名人数">{{ currentSchedule.participantCount || 0 }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 裁判列表 -->
      <div style="margin-top: 20px;">
        <h4>已分配裁判</h4>
        <el-table :data="currentSchedule?.referees || []" style="width: 100%">
          <el-table-column prop="realName" label="裁判姓名" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="taskStatus" label="任务状态">
            <template #default="scope">
              <el-tag :type="getTaskStatusType(scope.row.taskStatus)">
                {{ getTaskStatusText(scope.row.taskStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button size="small" type="danger" @click="removeReferee(scope.row)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { scheduleApi, taskApi, userApi } from '@/utils/api'

// 响应式数据
const scheduleType = ref('upcoming')
const selectedProject = ref('')
const schedules = ref([])
const projects = ref([])
const availableReferees = ref([])
const assignDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentSchedule = ref(null)

const assignForm = reactive({
  scheduleId: '',
  projectName: '',
  scheduleTime: '',
  refereeIds: [],
  reason: ''
})

// 计算属性
const filteredSchedules = computed(() => {
  if (!selectedProject.value) {
    return schedules.value
  }
  return schedules.value.filter(schedule => schedule.projectId === selectedProject.value)
})

// 获取赛程类型文本
const getScheduleTypeText = () => {
  const textMap = {
    'upcoming': '即将开始',
    'ongoing': '进行中',
    'completed': '已完成'
  }
  return textMap[scheduleType.value] || ''
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '未开始': 'info',
    '进行中': 'warning',
    '已结束': 'success',
    'FINISHED': 'success',
    'COMPLETED': 'success'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  return status || '未知'
}

// 获取任务状态类型
const getTaskStatusType = (status) => {
  const typeMap = {
    'PENDING': 'warning',
    'ASSIGNED': 'info',
    'ONGOING': 'warning',
    'FINISHED': 'success'
  }
  return typeMap[status] || 'info'
}

// 获取任务状态文本
const getTaskStatusText = (status) => {
  const textMap = {
    'PENDING': '待处理',
    'ASSIGNED': '已分配',
    'ONGOING': '进行中',
    'FINISHED': '已完成'
  }
  return textMap[status] || status
}

// 加载赛程
const loadSchedulesByType = async () => {
  try {
    let response
    switch (scheduleType.value) {
      case 'upcoming':
        response = await scheduleApi.getUpcomingMatches()
        break
      case 'ongoing':
        response = await scheduleApi.getOngoingMatches()
        break
      case 'completed':
        response = await scheduleApi.getCompletedMatches()
        break
      default:
        response = await scheduleApi.getScheduleList()
    }
    
    if (response.code === 200) {
      schedules.value = (response.data || []).map(s => ({
        ...s,
        scheduleDate: s.scheduleDate || s.date || '',
        scheduleTime: s.scheduleTime || s.time || ''
      }))
      // 为每个赛程加载裁判信息
      await loadRefereesForSchedules()
    } else {
      ElMessage.error(response.message || '获取赛程失败')
    }
  } catch (error) {
    console.error('获取赛程失败:', error)
    ElMessage.error('获取赛程失败')
  }
}

// 加载项目列表
const loadProjects = async () => {
  try {
    const response = await scheduleApi.getProjectList()
    if (response.code === 200) {
      projects.value = response.data || []
    }
  } catch (error) {
    console.error('获取项目列表失败:', error)
  }
}

// 加载可用裁判
const loadAvailableReferees = async () => {
  try {
    const response = await taskApi.getAvailableReferees()
    if (response.code === 200) {
      availableReferees.value = response.data || []
    } else {
      // 如果API不可用，从用户列表获取裁判
      const userResponse = await userApi.getAllUsers()
      if (userResponse.code === 200) {
        availableReferees.value = userResponse.data.filter(user => 
          user.role === 'referee' || user.role === 'REFEREE'
        )
      }
    }
  } catch (error) {
    console.error('获取可用裁判失败:', error)
    // 从用户列表获取裁判作为备选
    try {
      const userResponse = await userApi.getAllUsers()
      if (userResponse.code === 200) {
        availableReferees.value = userResponse.data.filter(user => 
          user.role === 'referee' || user.role === 'REFEREE'
        )
      }
    } catch (userError) {
      console.error('获取用户列表失败:', userError)
    }
  }
}

// 为赛程加载裁判信息
const loadRefereesForSchedules = async () => {
  for (let schedule of schedules.value) {
    try {
      const response = await taskApi.getTasksByScheduleId(schedule.id)
      if (response.code === 200) {
        schedule.referees = response.data.map(task => ({
          id: task.refereeId,
          realName: task.refereeName,
          username: task.refereeUsername,
          taskStatus: task.status
        }))
      }
    } catch (error) {
      console.error(`获取赛程 ${schedule.id} 的裁判信息失败:`, error)
      schedule.referees = []
    }
  }
}

// 分配裁判
const assignReferee = (schedule) => {
  assignForm.scheduleId = schedule.id
  assignForm.projectName = schedule.projectName
  assignForm.scheduleTime = `${schedule.scheduleDate} ${schedule.scheduleTime}`
  assignForm.refereeIds = []
  assignForm.reason = ''
  assignDialogVisible.value = true
}

// 为已完成比赛分配录入裁判
const assignRefereeForCompleted = async (schedule) => {
  try {
    await ElMessageBox.confirm(
      `确定要为 ${schedule.projectName} 分配成绩录入裁判吗？`,
      '分配录入裁判',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    // 这里可以调用API为已完成比赛分配裁判
    ElMessage.success('分配成功')
    await loadSchedulesByType()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('分配录入裁判失败:', error)
      ElMessage.error('分配失败')
    }
  }
}

// 确认分配
const confirmAssign = async () => {
  if (assignForm.refereeIds.length === 0) {
    ElMessage.warning('请选择至少一名裁判')
    return
  }
  
  try {
    // 这里是按赛程分配裁判，应调用 apply-referee 创建赛程-裁判任务
    const promises = assignForm.refereeIds.map(refereeId =>
      taskApi.applyReferee(assignForm.scheduleId, refereeId, assignForm.reason || '管理员分配')
    )
    
    await Promise.all(promises)
    ElMessage.success('裁判分配成功')
    assignDialogVisible.value = false
    await loadSchedulesByType()
  } catch (error) {
    console.error('分配裁判失败:', error)
    ElMessage.error('分配失败')
  }
}

// 查看详情
const viewDetails = (schedule) => {
  currentSchedule.value = schedule
  detailDialogVisible.value = true
}

// 移除裁判
const removeReferee = async (referee) => {
  try {
    await ElMessageBox.confirm(
      `确定要移除裁判 ${referee.realName} 吗？`,
      '移除裁判',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里可以调用API移除裁判
    ElMessage.success('移除成功')
    await loadSchedulesByType()
    if (detailDialogVisible.value) {
      detailDialogVisible.value = false
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('移除裁判失败:', error)
      ElMessage.error('移除失败')
    }
  }
}

// 筛选赛程
const filterSchedules = () => {
  // 筛选逻辑已在computed中实现
}

onMounted(() => {
  loadProjects()
  loadAvailableReferees()
  loadSchedulesByType()
})
</script>

<style scoped>
.referee-assignment {
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

.filter-card {
  margin-bottom: 20px;
}

.schedule-card {
  margin-bottom: 20px;
}

.no-data {
  padding: 40px;
  text-align: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}
</style> 