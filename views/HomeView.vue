<template>
  <div class="sports-management">
    <el-header class="header">
      <div class="header-left">
        <h2 class="logo">🏃‍♂️ 运动会管理系统</h2>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" icon="UserFilled" />
            <span class="username">{{ currentUser.username || '管理员' }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人设置</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>

    <el-container class="main-container">
      <el-aside width="250px" class="sidebar">
        <el-menu
            :default-active="activeMenu"
            class="sidebar-menu"
            @select="handleMenuSelect"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409eff"
        >
          <el-sub-menu index="admin" v-if="currentUser.role === 'ADMIN'">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/admin/dashboard">
              <el-icon><DataBoard /></el-icon>
              <span>数据概览</span>
            </el-menu-item>
            <el-menu-item index="/admin/users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/projects">
              <el-icon><Folder /></el-icon>
              <span>项目管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/schedules">
              <el-icon><Calendar /></el-icon>
              <span>赛程编排</span>
            </el-menu-item>
            <el-menu-item index="/admin/results">
              <el-icon><Medal /></el-icon>
              <span>成绩管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/announcements">
              <el-icon><Bell /></el-icon>
              <span>公告管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/referee-assignment">
              <el-icon><Avatar /></el-icon>
              <span>裁判分配</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="referee" v-if="currentUser.role === 'REFEREE'">
            <template #title>
              <el-icon><Avatar /></el-icon>
              <span>裁判功能</span>
            </template>
            <el-menu-item index="/referee/tasks">
              <el-icon><List /></el-icon>
              <span>比赛任务</span>
            </el-menu-item>
            <el-menu-item index="/referee/review">
              <el-icon><Check /></el-icon>
              <span>报名审核</span>
            </el-menu-item>
            <el-menu-item index="/referee/results">
              <el-icon><Edit /></el-icon>
              <span>成绩录入</span>
            </el-menu-item>
            <el-menu-item index="/referee/appeals">
              <el-icon><Warning /></el-icon>
              <span>申诉处理</span>
            </el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="user" v-if="currentUser.role === 'USER'">
            <template #title>
              <el-icon><UserFilled /></el-icon>
              <span>用户功能</span>
            </template>
            <el-menu-item index="/user/register">
              <el-icon><Plus /></el-icon>
              <span>比赛报名</span>
            </el-menu-item>
            <el-menu-item index="/user/schedule">
              <el-icon><Clock /></el-icon>
              <span>我的赛程</span>
            </el-menu-item>
            <el-menu-item index="/user/results">
              <el-icon><DataAnalysis /></el-icon>
              <span>我的成绩</span>
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/user/profile">
            <el-icon><Setting /></el-icon>
            <span>个人设置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <div class="welcome-banner" v-if="activeMenu === '/dashboard' || activeMenu === '/'">
          <el-card class="welcome-card">
            <div class="welcome-content">
              <div class="welcome-text">
                <h2>欢迎回来，{{ currentUser.username || '用户' }}！</h2>
                <p>今天是 {{ currentDate }}，祝您在运动会中取得好成绩！</p>
              </div>
              <div class="welcome-icon">
                <el-icon size="48"><Trophy /></el-icon>
              </div>
            </div>
          </el-card>
        </div>

        <div class="dashboard-cards" v-if="activeMenu === '/dashboard' || activeMenu === '/'">
          <el-row :gutter="20">
            <el-col :span="6">
              <el-card
                class="data-card interactive-card"
                role="button"
                tabindex="0"
                @click="handleStatCardClick('users')"
                @keyup.enter.prevent="handleStatCardClick('users')"
              >
                <div class="card-content">
                  <div class="card-icon user-icon">
                    <el-icon><User /></el-icon>
                  </div>
                  <div class="card-info">
                    <div class="card-number">{{ statistics.totalUsers }}</div>
                    <div class="card-label">总用户数</div>
                    <div class="card-action-tip">{{ getStatCardActionText('users') }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card
                class="data-card interactive-card"
                role="button"
                tabindex="0"
                @click="handleStatCardClick('projects')"
                @keyup.enter.prevent="handleStatCardClick('projects')"
              >
                <div class="card-content">
                  <div class="card-icon project-icon">
                    <el-icon><Trophy /></el-icon>
                  </div>
                  <div class="card-info">
                    <div class="card-number">{{ statistics.totalProjects }}</div>
                    <div class="card-label">比赛项目</div>
                    <div class="card-action-tip">{{ getStatCardActionText('projects') }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card
                class="data-card interactive-card"
                role="button"
                tabindex="0"
                @click="handleStatCardClick('athletes')"
                @keyup.enter.prevent="handleStatCardClick('athletes')"
              >
                <div class="card-content">
                  <div class="card-icon athlete-icon">
                    <el-icon><Avatar /></el-icon>
                  </div>
                  <div class="card-info">
                    <div class="card-number">{{ statistics.totalAthletes }}</div>
                    <div class="card-label">参赛运动员</div>
                    <div class="card-action-tip">{{ getStatCardActionText('athletes') }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card
                class="data-card interactive-card"
                role="button"
                tabindex="0"
                @click="handleStatCardClick('schedules')"
                @keyup.enter.prevent="handleStatCardClick('schedules')"
              >
                <div class="card-content">
                  <div class="card-icon schedule-icon">
                    <el-icon><Calendar /></el-icon>
                  </div>
                  <div class="card-info">
                    <div class="card-number">{{ statistics.totalSchedules }}</div>
                    <div class="card-label">今日赛程</div>
                    <div class="card-action-tip">{{ getStatCardActionText('schedules') }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="24">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>快速操作</span>
                  </div>
                </template>
                <div class="quick-actions">
                  <div v-if="currentUser.role === 'ADMIN'" class="action-buttons">
                    <el-button type="primary" @click="handleMenuSelect('/admin/users')">
                      <el-icon><User /></el-icon>
                      用户管理
                    </el-button>
                    <el-button type="success" @click="handleMenuSelect('/admin/projects')">
                      <el-icon><Folder /></el-icon>
                      项目管理
                    </el-button>
                    <el-button type="warning" @click="handleMenuSelect('/admin/schedules')">
                      <el-icon><Calendar /></el-icon>
                      赛程编排
                    </el-button>
                    <el-button type="info" @click="handleMenuSelect('/admin/results')">
                      <el-icon><Medal /></el-icon>
                      成绩管理
                    </el-button>
                    <el-button type="warning" @click="handleMenuSelect('/admin/referee-assignment')">
                      <el-icon><Avatar /></el-icon>
                      裁判分配
                    </el-button>
                  </div>

                  <div v-if="currentUser.role === 'REFEREE'" class="action-buttons">
                    <el-button type="primary" @click="handleMenuSelect('/referee/tasks')">
                      <el-icon><List /></el-icon>
                      比赛任务
                    </el-button>
                    <el-button type="success" @click="handleMenuSelect('/referee/review')">
                      <el-icon><Check /></el-icon>
                      报名审核
                    </el-button>
                    <el-button type="warning" @click="handleMenuSelect('/referee/results')">
                      <el-icon><Edit /></el-icon>
                      成绩录入
                    </el-button>
                    <el-button type="info" @click="handleMenuSelect('/referee/appeals')">
                      <el-icon><Warning /></el-icon>
                      申诉处理
                    </el-button>
                  </div>

                  <div v-if="currentUser.role === 'USER'" class="action-buttons">
                    <el-button type="primary" @click="handleMenuSelect('/user/register')">
                      <el-icon><Plus /></el-icon>
                      比赛报名
                    </el-button>
                    <el-button type="success" @click="handleMenuSelect('/user/schedule')">
                      <el-icon><Clock /></el-icon>
                      我的赛程
                    </el-button>
                    <el-button type="warning" @click="handleMenuSelect('/user/results')">
                      <el-icon><DataAnalysis /></el-icon>
                      我的成绩
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>最近比赛</span>
                    <el-button type="text">查看更多</el-button>
                  </div>
                </template>
                <el-table :data="recentMatches" style="width: 100%">
                  <el-table-column prop="name" label="项目名称" />
                  <el-table-column prop="time" label="时间" width="150" />
                  <el-table-column prop="venue" label="场地" width="100" />
                  <el-table-column prop="status" label="状态" width="80">
                    <template #default="{ row }">
                      <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
                    </template>
                  </el-table-column>
                </el-table>
                <div v-if="recentMatches.length === 0" class="no-matches">
                  <el-empty description="暂无未开始的比赛" />
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card>
                <template #header>
                  <div class="card-header">
                    <span>系统公告</span>
                    <el-button type="text">查看更多</el-button>
                  </div>
                </template>
                <div class="announcement-list">
                  <div v-for="announcement in announcements" :key="announcement.id" class="announcement-item">
                    <div class="announcement-title">{{ announcement.title }}</div>
                    <div class="announcement-time">{{ announcement.time }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <UserManagement v-if="activeMenu === '/admin/users'" />

        <Dashboard v-if="activeMenu === '/admin/dashboard'" />

        <ProjectManagement v-if="activeMenu === '/admin/projects'" />

        <ScheduleManagement v-if="activeMenu === '/admin/schedules'" />

        <ResultManagement v-if="activeMenu === '/admin/results'" />

        <AnnouncementManagement v-if="activeMenu === '/admin/announcements'" />

        <RefereeAssignment v-if="activeMenu === '/admin/referee-assignment'" />

        <TaskManagement v-if="activeMenu === '/referee/tasks'" />

        <RegistrationReview v-if="activeMenu === '/referee/review'" />

        <ResultEntry v-if="activeMenu === '/referee/results'" />

        <AppealManagement v-if="activeMenu === '/referee/appeals'" />

        <CompetitionRegister v-if="activeMenu === '/user/register'" />

        <UserSchedule v-if="activeMenu === '/user/schedule'" />

        <UserResults v-if="activeMenu === '/user/results'" />

        <UserProfile v-if="activeMenu === '/user/profile'" />

      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { logout, getCurrentUser } from '@/utils/auth'
import { announcementApi, scheduleApi, userApi, projectApi, registrationApi } from '@/utils/api'
import Dashboard from './admin/Dashboard.vue'
import UserManagement from './admin/UserManagement.vue'
import ProjectManagement from './admin/ProjectManagement.vue'
import ScheduleManagement from './admin/ScheduleManagement.vue'
import ResultManagement from './admin/ResultManagement.vue'
import AnnouncementManagement from './admin/AnnouncementManagement.vue'
import RefereeAssignment from './admin/RefereeAssignment.vue'
import TaskManagement from './referee/TaskManagement.vue'
import RegistrationReview from './referee/RegistrationReview.vue'
import ResultEntry from './referee/ResultEntry.vue'
import AppealManagement from './referee/AppealManagement.vue'
import CompetitionRegister from './user/CompetitionRegister.vue'
import UserSchedule from './user/UserSchedule.vue'
import UserResults from './user/UserResults.vue'
import UserProfile from './user/UserProfile.vue'
import {
  ArrowDown,
  Avatar,
  Bell,
  Calendar, Clock, DataAnalysis,
  DataBoard, Edit, House,
  List,
  Medal, Plus,
  Setting,
  Trophy,
  User, UserFilled, Warning, Folder, Check
} from "@element-plus/icons-vue";

// 当前用户信息
const currentUser = reactive({
  username: '',
  role: ''
})

const router = useRouter()
const route = useRoute()

// 当前激活的菜单
const activeMenu = ref('/dashboard')

// 当前日期
const currentDate = ref('')

// 统计数据
const statistics = reactive({
  totalUsers: 0,
  totalProjects: 0,
  totalAthletes: 0,
  totalSchedules: 0
})

// 最近比赛数据
const recentMatches = ref([])

// 公告数据
const announcements = ref([])

// 处理菜单选择
const handleMenuSelect = (index) => {
  activeMenu.value = index
  console.log('选择菜单:', index)
  // 使用路由跳转
  if (index.startsWith('/')) {
    router.push(index)
  }
}

// 处理用户命令
const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      // 调用退出登录函数
      await logout()
    } catch {
      // 用户取消
    }
  } else if (command === 'profile') {
    // 跳转到当前用户的个人设置页
    activeMenu.value = '/user/profile'
    router.push('/user/profile')
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '进行中': 'success',
    '待开始': 'warning',
    '已完成': 'info'
  }
  return typeMap[status] || 'info'
}

// 格式化比赛时间
const formatMatchTime = (date, time) => {
  try {
    const dateObj = new Date(date + ' ' + time)
    const today = new Date()
    const tomorrow = new Date(today)
    tomorrow.setDate(tomorrow.getDate() + 1)

    // 格式化日期
    const month = dateObj.getMonth() + 1
    const day = dateObj.getDate()
    const hours = dateObj.getHours().toString().padStart(2, '0')
    const minutes = dateObj.getMinutes().toString().padStart(2, '0')

    // 判断是今天还是明天
    if (dateObj.toDateString() === today.toDateString()) {
      return `今天 ${month}/${day} ${hours}:${minutes}`
    } else if (dateObj.toDateString() === tomorrow.toDateString()) {
      return `明天 ${month}/${day} ${hours}:${minutes}`
    } else {
      return `${month}/${day} ${hours}:${minutes}`
    }
  } catch (error) {
    return `${date} ${time}`
  }
}

const loadStatistics = async () => {
  try {
    let usersCount = statistics.totalUsers
    // 管理员接口：只有 ADMIN 才调用，避免普通用户/裁判出现“权限不足”
    if (currentUser.role === 'ADMIN') {
      const usersRes = await userApi.getAllUsers()
      usersCount = (usersRes.data || []).length
    }

    const [projectsRes, registrationsRes, schedulesRes] = await Promise.all([
      projectApi.getAllProjects(),
      registrationApi.getAllRegistrations(),
      scheduleApi.getScheduleList()
    ])

    statistics.totalUsers = usersCount
    statistics.totalProjects = (projectsRes.data || []).length

    // 参赛运动员：报名表里去重后的 userId 数量（真实数据库报名数据）
    const athleteIds = new Set((registrationsRes.data || []).map(item => item.userId).filter(Boolean))
    statistics.totalAthletes = athleteIds.size

    // 今日赛程：按赛程日期等于今天统计
    const today = new Date().toISOString().split('T')[0]
    statistics.totalSchedules = (schedulesRes.data || []).filter(s => {
      if (!s?.date) return false
      return String(s.date).slice(0, 10) === today
    }).length
  } catch (error) {
    console.error('加载首页统计数据失败:', error)
  }
}

const getStatCardActionText = (type) => {
  if (currentUser.role === 'ADMIN') {
    const map = {
      users: '点击进入用户管理',
      projects: '点击进入项目管理',
      athletes: '点击进入成绩管理',
      schedules: '点击进入赛程编排'
    }
    return map[type] || '点击查看详情'
  }
  if (currentUser.role === 'REFEREE') {
    const map = {
      users: '仅管理员可查看明细',
      projects: '点击查看比赛任务',
      athletes: '点击进入报名审核',
      schedules: '点击进入成绩录入'
    }
    return map[type] || '点击查看详情'
  }
  const map = {
    users: '仅管理员可查看明细',
    projects: '点击进入比赛报名',
    athletes: '点击查看我的赛程',
    schedules: '点击查看我的赛程'
  }
  return map[type] || '点击查看详情'
}

const handleStatCardClick = (type) => {
  if (currentUser.role === 'ADMIN') {
    const routeMap = {
      users: '/admin/users',
      projects: '/admin/projects',
      athletes: '/admin/results',
      schedules: '/admin/schedules'
    }
    const target = routeMap[type]
    if (target) handleMenuSelect(target)
    return
  }

  if (currentUser.role === 'REFEREE') {
    const routeMap = {
      projects: '/referee/tasks',
      athletes: '/referee/review',
      schedules: '/referee/results'
    }
    const target = routeMap[type]
    if (target) {
      handleMenuSelect(target)
    } else {
      ElMessage.info('该统计仅管理员可查看详细数据')
    }
    return
  }

  const routeMap = {
    projects: '/user/register',
    athletes: '/user/schedule',
    schedules: '/user/schedule'
  }
  const target = routeMap[type]
  if (target) {
    handleMenuSelect(target)
  } else {
    ElMessage.info('该统计仅管理员可查看详细数据')
  }
}

onMounted(async () => {
  console.log('运动会管理系统已加载')
  // 获取当前用户信息
  const user = getCurrentUser()
  if (user) {
    currentUser.username = user.username
    currentUser.role = user.role
  }

  // 设置当前激活的菜单
  activeMenu.value = route.path

  // 格式化当前日期
  const now = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  currentDate.value = now.toLocaleDateString('zh-CN', options)

  // 加载公告数据
  try {
    const response = await announcementApi.getPublishedAnnouncements()
    announcements.value = response.data.map(item => ({
      id: item.id,
      title: item.title,
      time: item.publishTime ? new Date(item.publishTime).toLocaleString('zh-CN') : ''
    }))
  } catch (error) {
    console.error('加载公告失败:', error)
  }

  // 加载最近比赛数据
  await loadRecentMatches()

  // 加载首页统计数据（真实数据库）
  await loadStatistics()
})

// 加载最近比赛数据
const loadRecentMatches = async () => {
  try {
    const response = await scheduleApi.getUpcomingMatches()
    if (response.data && response.data.length > 0) {
      recentMatches.value = response.data.map(match => ({
        id: match.id,
        name: match.projectName || '未知项目',
        time: formatMatchTime(match.date, match.time),
        status: match.status,
        venue: match.venue
      }))
    } else {
      // 如果没有未开始的比赛，显示提示信息
      recentMatches.value = []
    }
  } catch (error) {
    console.error('加载最近比赛失败:', error)
    // 如果加载失败，显示默认数据
    recentMatches.value = [
      { name: '100米短跑', time: '09:00', status: '待开始' },
      { name: '跳远', time: '14:00', status: '待开始' },
      { name: '铅球', time: '16:00', status: '待开始' }
    ]
  }
}
</script>

<style scoped>
/* 全局布局与背景 */
.sports-management {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8fafc; /* 现代 SaaS 灰蓝底色 */
  overflow: hidden;
}

/* 顶部玻璃态导航栏 */
.header {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: #0f172a;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 4px 20px -2px rgba(0, 0, 0, 0.05);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  z-index: 10;
}

.logo {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 0.5px;
  background: linear-gradient(135deg, #2563eb 0%, #7c3aed 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #475569;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.user-info:hover {
  background-color: #f1f5f9;
}

.username {
  margin: 0 10px;
  font-size: 14px;
}

.main-container {
  flex: 1;
  overflow: hidden;
}

/* 侧边栏：深邃高级质感 */
.sidebar {
  background-color: #0f172a !important; /* Tailwind Slate 900 */
  border-right: none;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
  z-index: 5;
}

/* 强制覆盖 Element Plus 默认菜单样式 */
.sidebar-menu {
  background-color: #0f172a !important;
  border-right: none;
}

:deep(.el-menu) {
  background-color: transparent !important;
  border-right: none;
}

:deep(.el-sub-menu__title), :deep(.el-menu-item) {
  color: #94a3b8 !important;
  transition: all 0.3s ease;
}

:deep(.el-sub-menu__title:hover), :deep(.el-menu-item:hover) {
  background-color: rgba(255, 255, 255, 0.05) !important;
  color: #f8fafc !important;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(56, 189, 248, 0.1) !important;
  color: #38bdf8 !important;
  font-weight: 600;
  border-right: 3px solid #38bdf8;
}

/* 主内容区域 */
.main-content {
  padding: 24px 32px;
  overflow-y: auto;
  background-color: transparent;
}

/* 自定义主内容滚动条 */
.main-content::-webkit-scrollbar {
  width: 6px;
}
.main-content::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 3px;
}
.main-content::-webkit-scrollbar-track {
  background-color: transparent;
}

/* 所有卡片的全局统一风格 */
:deep(.el-card) {
  border: none;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.03), 0 2px 4px -1px rgba(0, 0, 0, 0.02);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.3s ease;
}

:deep(.el-card:hover) {
  transform: translateY(-2px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.08), 0 4px 6px -2px rgba(0, 0, 0, 0.04);
}

/* 欢迎横幅：炫彩渐变 */
.welcome-banner {
  margin-bottom: 24px;
}

.welcome-card {
  background: linear-gradient(135deg, #6366f1 0%, #3b82f6 100%) !important;
  color: white;
  position: relative;
  overflow: hidden;
}

/* 欢迎横幅背景装饰球 */
.welcome-card::after {
  content: '';
  position: absolute;
  right: -5%;
  top: -60%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(255,255,255,0.15) 0%, transparent 60%);
  border-radius: 50%;
}

.welcome-content {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.welcome-text h2 {
  margin: 0 0 12px 0;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 1px;
}

.welcome-text p {
  margin: 0;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
}

.welcome-icon {
  color: rgba(255, 255, 255, 0.9);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-8px); }
  100% { transform: translateY(0px); }
}

/* 数据概览卡片 */
.dashboard-cards {
  margin-bottom: 24px;
}

.data-card .card-content {
  display: flex;
  align-items: center;
  padding: 4px 0;
}

/* 现代感弥散阴影图标 */
.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 18px;
  font-size: 28px;
  color: white;
  transition: transform 0.3s ease;
}

.data-card:hover .card-icon {
  transform: scale(1.05) rotate(-5deg);
}

.user-icon {
  background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
  box-shadow: 0 8px 16px -4px rgba(139, 92, 246, 0.5);
}

.project-icon {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  box-shadow: 0 8px 16px -4px rgba(244, 63, 94, 0.5);
}

.athlete-icon {
  background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%);
  box-shadow: 0 8px 16px -4px rgba(14, 165, 233, 0.5);
}

.schedule-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 8px 16px -4px rgba(16, 185, 129, 0.5);
}

.card-info {
  flex: 1;
}

.card-number {
  font-size: 30px;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 4px;
  font-family: 'DIN Alternate', -apple-system, sans-serif;
}

.card-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.card-action-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #94a3b8;
}

.interactive-card {
  cursor: pointer;
}

.interactive-card:active {
  transform: translateY(0) scale(0.995);
}

.interactive-card:focus-visible {
  outline: 2px solid #60a5fa;
  outline-offset: 2px;
}

/* 卡片标题栏 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #0f172a;
}

:deep(.el-card__header) {
  border-bottom: 1px solid #f1f5f9;
  padding: 18px 24px;
}

:deep(.el-card__body) {
  padding: 24px;
}

/* 快速操作按钮组 */
.quick-actions {
  padding: 4px 0;
}

.action-buttons {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  border-radius: 12px;
  border: none;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-buttons .el-button--primary {
  background: #eff6ff;
  color: #2563eb;
}
.action-buttons .el-button--primary:hover { background: #2563eb; color: #fff; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2); }

.action-buttons .el-button--success {
  background: #f0fdf4;
  color: #16a34a;
}
.action-buttons .el-button--success:hover { background: #16a34a; color: #fff; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(22, 163, 74, 0.2); }

.action-buttons .el-button--warning {
  background: #fff7ed;
  color: #ea580c;
}
.action-buttons .el-button--warning:hover { background: #ea580c; color: #fff; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(234, 88, 12, 0.2); }

.action-buttons .el-button--info {
  background: #f8fafc;
  color: #475569;
}
.action-buttons .el-button--info:hover { background: #475569; color: #fff; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(71, 85, 105, 0.2); }

/* 公告列表交互 */
.announcement-list {
  max-height: 320px;
  overflow-y: auto;
  padding-right: 8px;
}

.announcement-list::-webkit-scrollbar { width: 4px; }
.announcement-list::-webkit-scrollbar-thumb { background-color: #e2e8f0; border-radius: 2px; }

.announcement-item {
  padding: 14px 16px;
  border-radius: 10px;
  margin-bottom: 8px;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.announcement-item:hover {
  background-color: #f8fafc;
  border-color: #e2e8f0;
  transform: translateX(4px);
}

.announcement-title {
  font-size: 15px;
  color: #334155;
  margin-bottom: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.2s;
}

.announcement-item:hover .announcement-title {
  color: #2563eb;
}

.announcement-time {
  font-size: 13px;
  color: #94a3b8;
}

/* 表格样式美化 */
:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  --el-table-header-bg-color: #f8fafc;
  --el-table-header-text-color: #475569;
}

:deep(.el-table th.el-table__cell) {
  font-weight: 600;
}

.no-matches {
  padding: 30px;
  text-align: center;
}

.page-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

/* 响应式设计保留并优化 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px !important;
  }

  .header {
    padding: 0 16px;
  }

  .logo {
    font-size: 18px;
  }

  .main-content {
    padding: 16px;
  }
}
</style>