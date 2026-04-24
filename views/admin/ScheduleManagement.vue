<template>
  <div class="schedule-management">
    <div class="page-header">
      <h2>赛程编排</h2>
      <el-button type="primary" @click="showAddScheduleDialog">
        <el-icon><Plus /></el-icon>
        新增赛程
      </el-button>
    </div>
    
    <el-card>
      <template #header>
        <span>赛程列表</span>
      </template>
      
      <el-table :data="schedules" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="projectName" label="比赛项目" />
        <el-table-column prop="date" label="比赛日期">
          <template #default="scope">
            {{ TimeUtils.formatDate(scope.row.date) }}
          </template>
        </el-table-column>
        <el-table-column prop="time" label="比赛时间">
          <template #default="scope">
            {{ TimeUtils.formatTime(scope.row.time) }}
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
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <el-button size="small" @click="editSchedule(scope.row)">编辑</el-button>
            <el-button size="small" type="info" @click="viewRegistrations(scope.row)">查看报名</el-button>
            <el-button size="small" type="danger" @click="handleDeleteSchedule(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑赛程对话框 -->
    <el-dialog
      v-model="scheduleDialogVisible"
      :title="isEdit ? '编辑赛程' : '新增赛程'"
      width="500px"
    >
      <el-form :model="scheduleForm" label-width="100px">
        <el-form-item label="比赛项目">
          <el-select v-model="scheduleForm.projectId" placeholder="请选择比赛项目">
            <el-option
              v-for="project in projects"
              :key="project.id"
              :label="project.name"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="比赛日期">
          <el-date-picker
            v-model="scheduleForm.date"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="比赛时间">
          <el-time-picker
            v-model="scheduleForm.time"
            placeholder="选择时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="比赛场地">
          <el-input v-model="scheduleForm.venue" placeholder="请输入比赛场地" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="scheduleForm.status" placeholder="请选择状态">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="scheduleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveSchedule">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 查看报名对话框 -->
    <el-dialog
      v-model="registrationDialogVisible"
      title="报名情况"
      width="800px"
    >
      <div class="registration-info">
        <h4>{{ currentSchedule.projectName }} - {{ TimeUtils.formatDate(currentSchedule.date) }} {{ TimeUtils.formatTime(currentSchedule.time) }}</h4>
        <p>场地：{{ currentSchedule.venue }}</p>
      </div>
      
      <el-table :data="registrations" style="width: 100%" v-loading="registrationLoading">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="status" label="报名状态">
          <template #default="scope">
            <el-tag :type="getRegistrationStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="报名时间">
          <template #default="scope">
            {{ TimeUtils.formatDateTime(scope.row.registerTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button 
              size="small" 
              type="success" 
              @click="confirmRegistration(scope.row)"
              v-if="scope.row.status === '已报名'"
            >
              确认
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="cancelRegistration(scope.row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="registrationDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getScheduleList, addSchedule, updateSchedule, deleteSchedule, getProjectList, getRegistrationsBySchedule, updateRegistrationStatus, deleteRegistration } from '@/utils/api'
import { TimeUtils } from '@/utils/timeUtils'

export default {
  name: 'ScheduleManagement',
  setup() {
    const schedules = ref([])
    const projects = ref([])
    const loading = ref(false)
    
    const scheduleDialogVisible = ref(false)
    const isEdit = ref(false)
    
    // 报名相关
    const registrationDialogVisible = ref(false)
    const registrations = ref([])
    const registrationLoading = ref(false)
    const currentSchedule = ref({})
    
    const scheduleForm = reactive({
      id: null,
      projectId: '',
      date: '',
      time: '',
      venue: '',
      status: '未开始'
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
        '已报名': 'info',
        '已确认': 'success',
        '已取消': 'danger'
      }
      return types[status] || 'info'
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
    
    // 加载项目列表
    const loadProjects = async () => {
      try {
        const response = await getProjectList()
        if (response.code === 200) {
          projects.value = response.data
        } else {
          ElMessage.error(response.message || '获取项目列表失败')
        }
      } catch (error) {
        console.error('获取项目列表失败:', error)
        ElMessage.error('获取项目列表失败')
      }
    }
    
    const showAddScheduleDialog = () => {
      isEdit.value = false
      Object.assign(scheduleForm, {
        id: null,
        projectId: '',
        date: '',
        time: '',
        venue: '',
        status: '未开始'
      })
      scheduleDialogVisible.value = true
    }
    
    const editSchedule = (row) => {
      isEdit.value = true
      // 深拷贝数据，避免直接修改原数据
      const scheduleData = { ...row }
      
      // 处理日期格式转换
      if (scheduleData.date) {
        if (typeof scheduleData.date === 'string') {
          // 如果是字符串格式，直接使用
          scheduleData.date = scheduleData.date
        } else {
          // 如果是Date对象，转换为字符串
          scheduleData.date = TimeUtils.formatDate(scheduleData.date)
        }
      }
      
      // 处理时间格式转换
      if (scheduleData.time) {
        if (typeof scheduleData.time === 'string') {
          // 如果是字符串格式，确保是HH:mm格式
          scheduleData.time = TimeUtils.formatTime(scheduleData.time)
        } else {
          // 如果是Date对象，转换为字符串
          scheduleData.time = TimeUtils.formatTime(scheduleData.time)
        }
      }
      
      Object.assign(scheduleForm, scheduleData)
      scheduleDialogVisible.value = true
    }
    
    const handleDeleteSchedule = async (row) => {
      try {
        await ElMessageBox.confirm(
          '确定要删除这个赛程吗？',
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        const response = await deleteSchedule(row.id)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          await loadSchedules() // 重新加载列表
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除赛程失败:', error)
          ElMessage.error('删除赛程失败')
        }
      }
    }
    
    const saveSchedule = async () => {
      try {
        // 验证表单
        if (!scheduleForm.projectId) {
          ElMessage.error('请选择比赛项目')
          return
        }
        if (!scheduleForm.date) {
          ElMessage.error('请选择比赛日期')
          return
        }
        if (!scheduleForm.time) {
          ElMessage.error('请选择比赛时间')
          return
        }
        if (!scheduleForm.venue || scheduleForm.venue.trim() === '') {
          ElMessage.error('请输入比赛场地')
          return
        }
        
        // 处理时间数据格式转换
        const scheduleData = { ...scheduleForm }
        
        // 处理日期格式
        if (scheduleData.date) {
          if (scheduleData.date instanceof Date) {
            const year = scheduleData.date.getFullYear()
            const month = String(scheduleData.date.getMonth() + 1).padStart(2, '0')
            const day = String(scheduleData.date.getDate()).padStart(2, '0')
            scheduleData.date = `${year}-${month}-${day}`
          }
        }
        
        // 处理时间格式
        if (scheduleData.time) {
          if (scheduleData.time instanceof Date) {
            const hours = String(scheduleData.time.getHours()).padStart(2, '0')
            const minutes = String(scheduleData.time.getMinutes()).padStart(2, '0')
            scheduleData.time = `${hours}:${minutes}`
          } else if (typeof scheduleData.time === 'string') {
            // 确保时间格式为HH:mm
            scheduleData.time = TimeUtils.formatTime(scheduleData.time)
          }
        }
        
        let response
        if (isEdit.value) {
          response = await updateSchedule(scheduleData)
        } else {
          response = await addSchedule(scheduleData)
        }
        
        if (response.code === 200) {
          ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
          scheduleDialogVisible.value = false
          await loadSchedules() // 重新加载列表
        } else {
          ElMessage.error(response.message || (isEdit.value ? '编辑失败' : '添加失败'))
        }
      } catch (error) {
        console.error('保存赛程失败:', error)
        ElMessage.error('保存赛程失败')
      }
    }
    

    
    // 查看报名情况
    const viewRegistrations = async (schedule) => {
      currentSchedule.value = schedule
      registrationDialogVisible.value = true
      await loadRegistrations(schedule.id)
    }
    
    // 加载报名记录
    const loadRegistrations = async (scheduleId) => {
      registrationLoading.value = true
      try {
        const response = await getRegistrationsBySchedule(scheduleId)
        if (response.code === 200) {
          registrations.value = response.data
        } else {
          ElMessage.error(response.message || '获取报名记录失败')
        }
      } catch (error) {
        console.error('获取报名记录失败:', error)
        ElMessage.error('获取报名记录失败')
      } finally {
        registrationLoading.value = false
      }
    }
    
    // 确认报名
    const confirmRegistration = async (registration) => {
      try {
        const response = await updateRegistrationStatus(registration.id, '已确认')
        if (response.code === 200) {
          ElMessage.success('确认报名成功')
          await loadRegistrations(currentSchedule.value.id)
        } else {
          ElMessage.error(response.message || '确认报名失败')
        }
      } catch (error) {
        console.error('确认报名失败:', error)
        ElMessage.error('确认报名失败')
      }
    }
    
    // 取消报名
    const cancelRegistration = async (registration) => {
      try {
        await ElMessageBox.confirm(
          '确定要取消这个报名吗？',
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        const response = await deleteRegistration(registration.id)
        if (response.code === 200) {
          ElMessage.success('取消报名成功')
          await loadRegistrations(currentSchedule.value.id)
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
    
    // 组件挂载时加载数据
    onMounted(() => {
      loadSchedules()
      loadProjects()
    })
    
    return {
      schedules,
      projects,
      loading,
      scheduleDialogVisible,
      isEdit,
      scheduleForm,
      registrationDialogVisible,
      registrations,
      registrationLoading,
      currentSchedule,
      getStatusType,
      getRegistrationStatusType,
      TimeUtils,
      showAddScheduleDialog,
      editSchedule,
      handleDeleteSchedule,
      saveSchedule,
      viewRegistrations,
      confirmRegistration,
      cancelRegistration
    }
  }
}
</script>

<style scoped>
.schedule-management {
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

.registration-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.registration-info h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.registration-info p {
  margin: 0;
  color: #666;
}
</style> 