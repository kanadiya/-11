<template>
  <div class="dashboard">
    <div class="charts-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>用户注册趋势</span>
            </template>
            <div ref="rrChartRef" class="chart-view"></div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>比赛项目分布</span>
            </template>
            <div ref="projectChartRef" class="chart-view"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <div class="recent-activities">
      <el-card>
        <template #header>
          <span>最近活动</span>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="activity in recentActivities"
            :key="activity.id"
            :timestamp="activity.time"
            :type="activity.type"
          >
            {{ activity.content }}
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>

  </div>
</template>

<script>
import { ref, reactive, onMounted, computed, watch, nextTick, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { userApi, projectApi, scheduleApi, resultApi, announcementApi, registrationApi } from '@/utils/api'
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  components: {
    Plus
  },
  setup() {
    const stats = reactive({
      totalUsers: 0,
      totalProjects: 0,
      totalSchedules: 0,
      totalResults: 0
    })
    
    const users = ref([])
    const rrChartRef = ref(null)
    const projectChartRef = ref(null)
    let rrChart = null
    let projectChart = null
    const loading = ref(false)
    const showUserDialog = ref(false)
    const showAddUserDialog = ref(false)
    const isEdit = ref(false)
    const submitting = ref(false)
    const userFormRef = ref()
    const selectedUsers = ref([])
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    
    const searchForm = reactive({
      username: '',
      realName: '',
      role: ''
    })
    
    const userForm = reactive({
      id: null,
      username: '',
      realName: '',
      phone: '',
      email: '',
      role: 'USER',
      status: 1,
      password: '',
      confirmPassword: ''
    })
    
    const userRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' }
      ],
      phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
      ],
      email: [
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
      ],
      role: [
        { required: true, message: '请选择角色', trigger: 'change' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== userForm.password) {
              callback(new Error('两次输入密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }
    
    const recentActivities = ref([])

    const formatTime = (val) => {
      if (!val) return ''
      return String(val).replace('T', ' ').substring(0, 16)
    }

    const loadOverviewStats = async () => {
      try {
        const [usersRes, projectsRes, schedulesRes, resultsRes] = await Promise.all([
          userApi.getAllUsers(),
          projectApi.getAllProjects(),
          scheduleApi.getScheduleList(),
          resultApi.getAllResults()
        ])
        stats.totalUsers = (usersRes.data || []).length
        stats.totalProjects = (projectsRes.data || []).length
        stats.totalSchedules = (schedulesRes.data || []).length
        stats.totalResults = (resultsRes.data || []).length
      } catch (e) {
        ElMessage.error('加载数据概览失败')
      }
    }

    const loadRecentActivities = async () => {
      try {
        const [registrationsRes, resultsRes, announcementsRes] = await Promise.all([
          registrationApi.getAllRegistrations(),
          resultApi.getAllResults(),
          announcementApi.getAllAnnouncements()
        ])

        const activities = []
        ;(registrationsRes.data || []).slice(0, 3).forEach(item => {
          activities.push({
            id: `reg-${item.id}`,
            content: `用户 ${item.username || item.userId} 报名了 ${item.projectName || ('赛程' + item.scheduleId)}`,
            time: formatTime(item.registerTime || item.updateTime || item.createTime),
            type: 'primary'
          })
        })
        ;(resultsRes.data || []).slice(0, 3).forEach(item => {
          activities.push({
            id: `res-${item.id}`,
            content: `${item.projectName || '比赛'} 录入成绩：${item.realName || item.username || item.userId}`,
            time: formatTime(item.entryTime || item.updateTime || item.createTime),
            type: 'success'
          })
        })
        ;(announcementsRes.data || []).slice(0, 3).forEach(item => {
          activities.push({
            id: `ann-${item.id}`,
            content: `公告已更新：${item.title || '未命名公告'}`,
            time: formatTime(item.updateTime || item.publishTime || item.createTime),
            type: 'info'
          })
        })

        recentActivities.value = activities
          .filter(a => a.time)
          .sort((a, b) => (a.time < b.time ? 1 : -1))
          .slice(0, 8)
      } catch (e) {
        recentActivities.value = []
      }
    }

    const groupCountByDate = (list, timeKey) => {
      const map = {}
      ;(list || []).forEach(item => {
        const raw = item?.[timeKey] || item?.createTime || item?.updateTime
        if (!raw) return
        const day = String(raw).substring(0, 10)
        map[day] = (map[day] || 0) + 1
      })
      return map
    }

    const renderRRChart = async () => {
      try {
        const [registrationsRes, resultsRes] = await Promise.all([
          registrationApi.getAllRegistrations(),
          resultApi.getAllResults()
        ])
        const regMap = groupCountByDate(registrationsRes.data || [], 'registerTime')
        const resMap = groupCountByDate(resultsRes.data || [], 'entryTime')
        const dates = Array.from(new Set([...Object.keys(regMap), ...Object.keys(resMap)])).sort()

        if (!rrChart && rrChartRef.value) {
          rrChart = echarts.init(rrChartRef.value)
        }
        rrChart?.setOption({
          tooltip: { trigger: 'axis' },
          legend: { data: ['报名数', '成绩录入数'] },
          grid: { left: 40, right: 20, top: 40, bottom: 30 },
          xAxis: { type: 'category', data: dates },
          yAxis: { type: 'value', minInterval: 1 },
          series: [
            { name: '报名数', type: 'line', smooth: true, data: dates.map(d => regMap[d] || 0), areaStyle: {} },
            { name: '成绩录入数', type: 'line', smooth: true, data: dates.map(d => resMap[d] || 0), areaStyle: {} }
          ]
        })
      } catch (e) {
        console.error('R-R图加载失败:', e)
      }
    }

    const renderProjectChart = async () => {
      try {
        const [projectsRes, schedulesRes] = await Promise.all([
          projectApi.getAllProjects(),
          scheduleApi.getScheduleList()
        ])
        const projects = projectsRes.data || []
        const schedules = schedulesRes.data || []
        const counts = {}
        projects.forEach(p => { counts[p.id] = 0 })
        schedules.forEach(s => { counts[s.projectId] = (counts[s.projectId] || 0) + 1 })
        const pieData = projects.map(p => ({ name: p.name, value: counts[p.id] || 0 }))

        if (!projectChart && projectChartRef.value) {
          projectChart = echarts.init(projectChartRef.value)
        }
        projectChart?.setOption({
          tooltip: { trigger: 'item' },
          legend: { bottom: 0 },
          series: [
            {
              name: '项目分布',
              type: 'pie',
              radius: ['35%', '65%'],
              avoidLabelOverlap: false,
              data: pieData
            }
          ]
        })
      } catch (e) {
        console.error('项目分布图加载失败:', e)
      }
    }

    const handleResize = () => {
      rrChart?.resize()
      projectChart?.resize()
    }

    // 获取用户列表
    const loadUsers = async () => {
      loading.value = true
      try {
        const response = await userApi.getAllUsers()
        if (response.code === 200) {
          users.value = response.data
          total.value = response.data.length
          stats.totalUsers = response.data.length
        } else {
          ElMessage.error(response.message || '获取用户列表失败')
        }
      } catch (error) {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败')
      } finally {
        loading.value = false
      }
    }

    // 搜索用户
    const searchUsers = async () => {
      loading.value = true
      try {
        const params = {}
        if (searchForm.username) params.username = searchForm.username
        if (searchForm.realName) params.realName = searchForm.realName
        if (searchForm.role) params.role = searchForm.role
        
        const response = await userApi.searchUsers(params)
        if (response.code === 200) {
          users.value = response.data
          total.value = response.data.length
        } else {
          ElMessage.error(response.message || '搜索用户失败')
        }
      } catch (error) {
        console.error('搜索用户失败:', error)
        ElMessage.error('搜索用户失败')
      } finally {
        loading.value = false
      }
    }

    // 重置搜索
    const resetSearch = () => {
      searchForm.username = ''
      searchForm.realName = ''
      searchForm.role = ''
      loadUsers()
    }

    // 处理选择变化
    const handleSelectionChange = (selection) => {
      selectedUsers.value = selection
    }

    // 批量删除
    const batchDelete = async () => {
      if (selectedUsers.value.length === 0) {
        ElMessage.warning('请选择要删除的用户')
        return
      }

      try {
        await ElMessageBox.confirm(
          `确定要删除选中的 ${selectedUsers.value.length} 个用户吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        const ids = selectedUsers.value.map(user => user.id)
        const response = await userApi.batchDeleteUsers(ids)
        if (response.code === 200) {
          ElMessage.success(response.message || '批量删除成功')
          loadUsers()
          selectedUsers.value = []
        } else {
          ElMessage.error(response.message || '批量删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量删除失败:', error)
          ElMessage.error('批量删除失败')
        }
      }
    }

    // 重置密码
    const resetPassword = async (user) => {
      try {
        await ElMessageBox.confirm(
          `确定要重置用户 "${user.username}" 的密码吗？`,
          '确认重置密码',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        const response = await userApi.resetUserPassword(user.id)
        if (response.code === 200) {
          ElMessage.success(response.message || '密码重置成功')
        } else {
          ElMessage.error(response.message || '密码重置失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('重置密码失败:', error)
          ElMessage.error('重置密码失败')
        }
      }
    }

    // 分页处理
    const handleSizeChange = (val) => {
      pageSize.value = val
      loadUsers()
    }

    const handleCurrentChange = (val) => {
      currentPage.value = val
      loadUsers()
    }

    // 获取角色标签
    const getRoleLabel = (role) => {
      const roleMap = {
        'ADMIN': '管理员',
        'USER': '普通用户',
        'REFEREE': '裁判员'
      }
      return roleMap[role] || role
    }

    // 获取角色标签类型
    const getRoleType = (role) => {
      const typeMap = {
        'ADMIN': 'danger',
        'USER': 'primary',
        'REFEREE': 'warning'
      }
      return typeMap[role] || 'info'
    }

    // 编辑用户
    const editUser = (user) => {
      isEdit.value = true
      Object.assign(userForm, {
        id: user.id,
        username: user.username,
        realName: user.realName || '',
        phone: user.phone || '',
        email: user.email || '',
        role: user.role,
        status: user.status,
        password: '',
        confirmPassword: ''
      })
      showUserDialog.value = true
    }

    // 删除用户
    const deleteUser = async (user) => {
      if (user.username === 'admin') {
        ElMessage.warning('不能删除管理员账户')
        return
      }

      try {
        await ElMessageBox.confirm(
          `确定要删除用户 "${user.username}" 吗？`,
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        const response = await userApi.deleteUser(user.id)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          loadUsers()
        } else {
          ElMessage.error(response.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除用户失败:', error)
          ElMessage.error('删除用户失败')
        }
      }
    }

    // 提交用户表单
    const submitUser = async () => {
      if (!userFormRef.value) return

      try {
        await userFormRef.value.validate()
        submitting.value = true

        let response
        if (isEdit.value) {
          // 更新用户
          const updateData = {
            id: userForm.id,
            realName: userForm.realName,
            phone: userForm.phone,
            email: userForm.email,
            role: userForm.role,
            status: userForm.status
          }
          response = await userApi.updateUser(updateData)
        } else {
          // 添加用户
          const addData = {
            username: userForm.username,
            password: userForm.password,
            confirmPassword: userForm.confirmPassword,
            realName: userForm.realName,
            phone: userForm.phone,
            email: userForm.email,
            role: userForm.role
          }
          response = await userApi.register(addData)
        }

        if (response.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
          showUserDialog.value = false
          loadUsers()
          resetUserForm()
        } else {
          ElMessage.error(response.message || (isEdit.value ? '更新失败' : '添加失败'))
        }
      } catch (error) {
        console.error('提交用户表单失败:', error)
        ElMessage.error('操作失败')
      } finally {
        submitting.value = false
      }
    }

    // 重置用户表单
    const resetUserForm = () => {
      Object.assign(userForm, {
        id: null,
        username: '',
        realName: '',
        phone: '',
        email: '',
        role: 'USER',
        status: 1,
        password: '',
        confirmPassword: ''
      })
      isEdit.value = false
      if (userFormRef.value) {
        userFormRef.value.resetFields()
      }
    }

    // 监听添加用户对话框
    const watchAddUserDialog = () => {
      if (showAddUserDialog.value) {
        resetUserForm()
        showUserDialog.value = true
        showAddUserDialog.value = false
      }
    }

    // 计算属性
    const dialogTitle = computed(() => {
      return isEdit.value ? '编辑用户' : '添加用户'
    })
    
    onMounted(() => {
      console.log('Dashboard mounted')
      loadUsers()
      loadOverviewStats()
      loadRecentActivities()
      nextTick(() => {
        renderRRChart()
        renderProjectChart()
      })
      window.addEventListener('resize', handleResize)
    })

    onBeforeUnmount(() => {
      window.removeEventListener('resize', handleResize)
      rrChart?.dispose()
      projectChart?.dispose()
      rrChart = null
      projectChart = null
    })

    // 监听添加用户对话框变化
    watch(showAddUserDialog, watchAddUserDialog)
    
    return {
      stats,
      users,
      loading,
      recentActivities,
      showUserDialog,
      showAddUserDialog,
      isEdit,
      submitting,
      userForm,
      userFormRef,
      userRules,
      dialogTitle,
      getRoleLabel,
      getRoleType,
      editUser,
      deleteUser,
      submitUser
      ,
      searchForm,
      searchUsers,
      resetSearch,
      handleSelectionChange,
      selectedUsers,
      batchDelete,
      resetPassword,
      currentPage,
      pageSize,
      total,
      handleSizeChange,
      handleCurrentChange
      ,
      rrChartRef,
      projectChartRef
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.dashboard-header {
  margin-bottom: 30px;
}

.dashboard-header h2 {
  color: #333;
  margin-bottom: 8px;
}

.dashboard-header p {
  color: #666;
  margin: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  font-size: 3rem;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 0.9rem;
}

.user-management {
  margin-bottom: 30px;
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

.search-bar {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.search-bar .el-form-item {
  margin-bottom: 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.charts-section {
  margin-bottom: 30px;
}

.chart-view {
  height: 200px;
  background: #fff;
  border-radius: 8px;
}

.recent-activities {
  margin-bottom: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 