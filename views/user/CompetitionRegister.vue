<template>
  <div class="competition-register">
    <div class="page-header">
      <h2>比赛报名</h2>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>可报名赛程</span>
          </template>
          
          <el-table :data="filteredSchedules" style="width: 100%" v-loading="loading">
            <el-table-column prop="projectName" label="比赛项目" />
            <el-table-column prop="date" label="比赛日期">
              <template #default="scope">
                {{ formatDate(scope.row.date) }}
              </template>
            </el-table-column>
            <el-table-column prop="time" label="比赛时间">
              <template #default="scope">
                {{ formatTime(scope.row.time) }}
              </template>
            </el-table-column>
            <el-table-column prop="venue" label="比赛场地" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button 
                  size="small" 
                  type="primary" 
                  :disabled="!canRegister(scope.row)"
                  @click="registerSchedule(scope.row)"
                >
                  {{ getRegisterButtonText(scope.row) }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 过滤说明 -->
          <div v-if="filteredSchedules.length !== schedules.length" class="filter-notice">
            <el-alert
              title="赛程过滤说明"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                <p>已自动过滤以下赛程：</p>
                <ul>
                  <li v-if="refereeTaskCount > 0">裁判任务赛程：{{ refereeTaskCount }} 个</li>
                  <li v-if="approvedRegistrationCount > 0">已通过报名赛程：{{ approvedRegistrationCount }} 个</li>
                </ul>
              </template>
            </el-alert>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>我的报名</span>
          </template>
          
          <div v-if="myRegistrations.length === 0" class="empty-state">
            <el-empty description="暂无报名记录" />
          </div>
          
          <div v-else>
            <div 
              v-for="registration in myRegistrations" 
              :key="registration.id"
              class="registration-item"
            >
              <div class="registration-header">
                <h4>{{ registration.projectName }}</h4>
                <el-tag :type="getRegistrationStatusType(registration.status)">
                  {{ registration.status }}
                </el-tag>
              </div>
              <div class="registration-info">
                <p>报名时间：{{ formatDateTime(registration.registerTime) }}</p>
                <p>比赛时间：{{ registration.scheduleDate }} {{ registration.scheduleTime }}</p>
                <p>比赛场地：{{ registration.venue }}</p>
              </div>
<!--              <div class="registration-actions">-->
<!--                <el-button -->
<!--                  size="small" -->
<!--                  type="danger" -->
<!--                  @click="cancelRegistration(registration)"-->
<!--                  v-if="registration.status === 'PENDING' || registration.status === 'APPROVED'"-->
<!--                >-->
<!--                  取消报名111-->
<!--                </el-button>-->
<!--              </div>-->
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 报名确认对话框 -->
    <el-dialog
      v-model="registerDialogVisible"
      title="确认报名"
      width="400px"
    >
      <div v-if="selectedSchedule">
        <p>您确定要报名参加 <strong>{{ selectedSchedule.projectName }}</strong> 吗？</p>
        <p>比赛信息：</p>
        <ul>
          <li>比赛日期：{{ formatDate(selectedSchedule.date) }}</li>
          <li>比赛时间：{{ formatTime(selectedSchedule.time) }}</li>
          <li>比赛场地：{{ selectedSchedule.venue }}</li>
        </ul>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="registerDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmRegister" :loading="registerLoading">确认报名</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getScheduleList, registrationApi, taskApi } from '@/utils/api'
import { getCurrentUser } from '@/utils/auth'

export default {
  name: 'CompetitionRegister',
  setup() {
    const schedules = ref([])
    const myRegistrations = ref([])
    const refereeTasks = ref([])
    const loading = ref(false)
    const registerLoading = ref(false)
    
    const registerDialogVisible = ref(false)
    const selectedSchedule = ref(null)
    
    const currentUser = getCurrentUser()
    
    // 计算过滤后的赛程列表
    const filteredSchedules = computed(() => {
      return schedules.value.filter(schedule => {
        // 过滤掉裁判任务的赛程
        const isRefereeTask = refereeTasks.value.some(task => 
          task.scheduleId === schedule.id || 
          (task.projectName === schedule.projectName && 
           task.date === schedule.date && 
           task.time === schedule.time)
        )
        
        // 过滤掉已通过报名的赛程
        const hasApprovedRegistration = myRegistrations.value.some(reg => 
          reg.scheduleId === schedule.id && reg.status === 'APPROVED'
        )
        
        return !isRefereeTask && !hasApprovedRegistration
      })
    })
    
    // 统计过滤的赛程数量
    const refereeTaskCount = computed(() => {
      return schedules.value.filter(schedule => {
        return refereeTasks.value.some(task => 
          task.scheduleId === schedule.id || 
          (task.projectName === schedule.projectName && 
           task.date === schedule.date && 
           task.time === schedule.time)
        )
      }).length
    })
    
    const approvedRegistrationCount = computed(() => {
      return schedules.value.filter(schedule => {
        return myRegistrations.value.some(reg => 
          reg.scheduleId === schedule.id && reg.status === 'APPROVED'
        )
      }).length
    })
    
    const getStatusType = (status) => {
      const types = {
        '未开始': 'info',
        '进行中': 'warning',
        '已结束': 'success'
      }
      return types[status] || 'info'
    }
    
    const getRegistrationStatusType = (status) => {
      const types = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'CANCELLED': 'info'
      }
      return types[status] || 'info'
    }
    
    const canRegister = (schedule) => {
      if (schedule.status !== '未开始') return false
      
      const registration = myRegistrations.value.find(reg => reg.scheduleId === schedule.id)
      if (!registration) return true
      
      // 只有被拒绝或取消的报名才能重新报名
      return registration.status === 'REJECTED' || registration.status === 'CANCELLED'
    }
    
    const isRegistered = (schedule) => {
      return myRegistrations.value.some(reg => reg.scheduleId === schedule.id)
    }
    
    const getRegisterButtonText = (schedule) => {
      const registration = myRegistrations.value.find(reg => reg.scheduleId === schedule.id)
      if (!registration) return '报名'
      
      switch (registration.status) {
        case 'PENDING': return '审核中'
        case 'APPROVED': return '已通过'
        case 'REJECTED': return '已拒绝'
        case 'CANCELLED': return '已取消'
        default: return '报名'
      }
    }
    
    const registerSchedule = (schedule) => {
      selectedSchedule.value = schedule
      registerDialogVisible.value = true
    }
    
    const confirmRegister = async () => {
      if (!selectedSchedule.value || !currentUser) return
      
      registerLoading.value = true
      try {
                 const response = await registrationApi.registerForMatch({
           userId: currentUser.id,
           scheduleId: selectedSchedule.value.id
         })
        
        if (response.code === 200) {
          ElMessage.success('报名成功！')
          registerDialogVisible.value = false
          await loadMyRegistrations() // 重新加载我的报名记录
        } else {
          ElMessage.error(response.message || '报名失败')
        }
      } catch (error) {
        console.error('报名失败:', error)
        ElMessage.error('报名失败')
      } finally {
        registerLoading.value = false
      }
    }
    
    const cancelRegistration = async (registration) => {
      try {
        await ElMessageBox.confirm(
          `确定要取消报名 ${registration.projectName} 吗？`,
          '确认取消',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        // 用户侧取消报名（不删除记录）
        const response = await registrationApi.cancelRegistration({
          userId: currentUser.id,
          scheduleId: registration.scheduleId
        })
        
        if (response.code === 200) {
          ElMessage.success('取消报名成功')
          await loadMyRegistrations() // 重新加载我的报名记录
        } else {
          ElMessage.error(response.message || '取消报名失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('取消报名失败:', error)
          ElMessage.error('取消报名失败')
        }
      }
    }
    
    // 加载赛程列表
    const loadSchedules = async () => {
      loading.value = true
      try {
        const response = await getScheduleList()
        if (response.code === 200) {
          schedules.value = response.data
        } else {
          ElMessage.error(response.message || '获取赛程列表失败')
        }
      } catch (error) {
        console.error('获取赛程列表失败:', error)
        ElMessage.error('获取赛程列表失败')
      } finally {
        loading.value = false
      }
    }
    
    // 加载我的报名记录
    const loadMyRegistrations = async () => {
      if (!currentUser) return
      
      try {
        const response = await registrationApi.getRegistrationsByUserId(currentUser.id)
        if (response.code === 200) {
          myRegistrations.value = response.data
        } else {
          ElMessage.error(response.message || '获取报名记录失败')
        }
      } catch (error) {
        console.error('获取报名记录失败:', error)
        ElMessage.error('获取报名记录失败')
      }
    }
    
    // 加载裁判任务
    const loadRefereeTasks = async () => {
      if (!currentUser || currentUser.role !== 'REFEREE') return
      
      try {
        const response = await taskApi.getTasksByRefereeId(currentUser.id)
        if (response.code === 200) {
          refereeTasks.value = response.data || []
        } else {
          console.warn('获取裁判任务失败:', response.message)
        }
      } catch (error) {
        console.error('获取裁判任务失败:', error)
        // 如果获取失败，使用空数组
        refereeTasks.value = []
      }
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
    
    // 组件挂载时加载数据
    onMounted(async () => {
      await Promise.all([
        loadSchedules(),
        loadMyRegistrations(),
        loadRefereeTasks()
      ])
    })
    
    return {
      schedules,
      filteredSchedules,
      myRegistrations,
      refereeTasks,
      loading,
      registerLoading,
      registerDialogVisible,
      selectedSchedule,
      refereeTaskCount,
      approvedRegistrationCount,
      getStatusType,
      getRegistrationStatusType,
      canRegister,
      isRegistered,
      getRegisterButtonText,
      registerSchedule,
      confirmRegister,
      cancelRegistration,
      formatDate,
      formatTime,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.competition-register {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.registration-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}

.registration-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.registration-header h4 {
  margin: 0;
  color: #333;
}

.registration-info {
  margin-bottom: 10px;
}

.registration-info p {
  margin: 5px 0;
  color: #666;
  font-size: 0.9rem;
}

.registration-actions {
  text-align: right;
}

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