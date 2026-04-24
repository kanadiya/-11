<template>
  <div class="user-schedule">
    <div class="page-header">
      <h2>我的赛程</h2>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>比赛安排</span>
          </template>
          
          <el-table :data="mySchedules" style="width: 100%" v-loading="loading">
            <el-table-column prop="projectName" label="比赛项目" />
            <el-table-column prop="date" label="比赛日期" />
            <el-table-column prop="time" label="比赛时间" />
            <el-table-column prop="venue" label="比赛场地" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button size="small" @click="viewScheduleDetail(scope.row)">详情</el-button>
                <el-button size="small" type="danger" v-if="scope.row.registrationStatus === 'PENDING'" @click="cancelRegistration(scope.row)">取消报名</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>今日赛程</span>
          </template>
          
          <div v-if="todaySchedules.length === 0" class="empty-state">
            <el-empty description="今日无比赛安排" />
          </div>
          
          <div v-else>
            <div 
              v-for="schedule in todaySchedules" 
              :key="schedule.id"
              class="schedule-item"
            >
              <div class="schedule-header">
                <h4>{{ schedule.projectName }}</h4>
                <el-tag :type="getStatusType(schedule.status)">
                  {{ schedule.status }}
                </el-tag>
              </div>
              <div class="schedule-info">
                <p>时间：{{ schedule.time }}</p>
                <p>场地：{{ schedule.venue }}</p>
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>赛程统计</span>
          </template>
          
          <div class="stats">
            <div class="stat-item">
              <div class="stat-number">{{ stats.total }}</div>
              <div class="stat-label">总比赛数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.completed }}</div>
              <div class="stat-label">已完成</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.upcoming }}</div>
              <div class="stat-label">即将开始</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 赛程详情对话框 -->
    <el-dialog
      v-model="scheduleDetailVisible"
      title="赛程详情"
      width="500px"
    >
      <div v-if="currentSchedule">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="比赛项目">{{ currentSchedule.projectName }}</el-descriptions-item>
          <el-descriptions-item label="比赛日期">{{ currentSchedule.date }}</el-descriptions-item>
          <el-descriptions-item label="比赛时间">{{ currentSchedule.time }}</el-descriptions-item>
          <el-descriptions-item label="比赛场地">{{ currentSchedule.venue }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentSchedule.status)">
              {{ currentSchedule.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注意事项">{{ currentSchedule.notes || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { registrationApi } from '@/utils/api'

export default {
  name: 'UserSchedule',
  setup() {
    const mySchedules = ref([])
    const loading = ref(false)
    
    const scheduleDetailVisible = ref(false)
    const currentSchedule = ref(null)

    const getCurrentUserId = () => {
      try {
        const stored = localStorage.getItem('userInfo')
        if (!stored) return null
        const user = JSON.parse(stored)
        return user?.id || null
      } catch (e) {
        return null
      }
    }

    const deriveScheduleStatus = (scheduleDate, scheduleTime, registrationStatus) => {
      if (registrationStatus === 'CANCELLED') return '已取消'
      if (registrationStatus === 'REJECTED') return '报名被拒绝'
      if (registrationStatus === 'PENDING') return '待审核'

      // APPROVED 按时间判断
      try {
        const dt = new Date(`${scheduleDate} ${scheduleTime}`)
        const now = new Date()
        const diff = dt.getTime() - now.getTime()
        if (diff > 30 * 60 * 1000) return '未开始'
        if (diff > -2 * 60 * 60 * 1000) return '即将开始'
        return '已完成'
      } catch (e) {
        return '未开始'
      }
    }

    const loadMySchedules = async () => {
      const userId = getCurrentUserId()
      if (!userId) {
        ElMessage.error('未找到用户信息，请重新登录')
        return
      }
      loading.value = true
      try {
        const res = await registrationApi.getRegistrationsByUserId(userId)
        if (res.code === 200) {
          mySchedules.value = (res.data || []).map(item => ({
            id: item.id,
            scheduleId: item.scheduleId,
            projectName: item.projectName || '未知项目',
            date: item.scheduleDate ? String(item.scheduleDate).slice(0, 10) : '',
            time: item.scheduleTime || '',
            venue: item.venue || '',
            registrationStatus: item.status,
            status: deriveScheduleStatus(item.scheduleDate, item.scheduleTime, item.status),
            notes: item.status === 'PENDING' ? '报名待审核，请关注状态更新' : '请按时到达比赛场地'
          }))
        } else {
          ElMessage.error(res.message || '加载赛程失败')
        }
      } catch (error) {
        ElMessage.error('加载赛程失败')
      } finally {
        loading.value = false
      }
    }
    
    // 计算今日赛程
    const todaySchedules = computed(() => {
      const today = new Date().toISOString().split('T')[0]
      return mySchedules.value.filter(schedule => schedule.date === today)
    })
    
    // 计算统计数据
    const stats = computed(() => {
      const total = mySchedules.value.length
      const completed = mySchedules.value.filter(s => s.status === '已完成').length
      const upcoming = mySchedules.value.filter(s => s.status === '即将开始' || s.status === '未开始').length
      
      return { total, completed, upcoming }
    })
    
    const getStatusType = (status) => {
      const types = {
        '未开始': 'info',
        '即将开始': 'warning',
        '进行中': 'primary',
        '已完成': 'success',
        '已取消': 'danger',
        '待审核': 'info',
        '报名被拒绝': 'danger'
      }
      return types[status] || 'info'
    }
    
    const viewScheduleDetail = (schedule) => {
      currentSchedule.value = schedule
      scheduleDetailVisible.value = true
    }
    
    const cancelRegistration = async (schedule) => {
      try {
        const userId = getCurrentUserId()
        if (!userId) {
          ElMessage.error('用户信息异常，请重新登录')
          return
        }
        const res = await registrationApi.cancelRegistration({
          userId,
          scheduleId: schedule.scheduleId
        })
        if (res.code === 200) {
          ElMessage.success('已取消报名')
          await loadMySchedules()
        } else {
          ElMessage.error(res.message || '取消报名失败')
        }
      } catch (e) {
        ElMessage.error('取消报名失败')
      }
    }

    onMounted(() => {
      loadMySchedules()
    })
    
    return {
      loading,
      mySchedules,
      todaySchedules,
      stats,
      scheduleDetailVisible,
      currentSchedule,
      getStatusType,
      viewScheduleDetail,
      cancelRegistration
    }
  }
}
</script>

<style scoped>
.user-schedule {
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

.schedule-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}

.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.schedule-header h4 {
  margin: 0;
  color: #333;
}

.schedule-info {
  margin-bottom: 10px;
}

.schedule-info p {
  margin: 5px 0;
  color: #666;
  font-size: 0.9rem;
}

.stats {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}
</style> 