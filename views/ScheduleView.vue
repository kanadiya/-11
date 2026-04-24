<template>
  <div class="schedule-container">
    <div class="header">
      <h1>赛程列表</h1>
      <div class="filters">
        <el-select v-model="statusFilter" placeholder="按状态筛选" clearable>
          <el-option label="全部" value=""></el-option>
          <el-option label="未开始" value="upcoming"></el-option>
          <el-option label="进行中" value="ongoing"></el-option>
          <el-option label="已结束" value="finished"></el-option>
        </el-select>
        <el-date-picker
          v-model="dateFilter"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          clearable
        />
        <el-input
          v-model="teamFilter"
          placeholder="搜索队伍"
          clearable
          style="width: 200px"
        >
          <template #prefix>
            <i class="el-icon-search"></i>
          </template>
        </el-input>
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 120px">
          <el-option label="按时间" value="time"></el-option>
          <el-option label="按状态" value="status"></el-option>
          <el-option label="按队伍" value="teams"></el-option>
        </el-select>
        <el-button
          @click="sortOrder = sortOrder === 'asc' ? 'desc' : 'asc'"
          :icon="sortOrder === 'asc' ? 'el-icon-sort-up' : 'el-icon-sort-down'"
        >
          {{ sortOrder === 'asc' ? '升序' : '降序' }}
        </el-button>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="statistics">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-card class="stat-card total">
            <div class="stat-number">{{ matchStatistics.total }}</div>
            <div class="stat-label">总比赛数</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card upcoming">
            <div class="stat-number">{{ matchStatistics.upcoming }}</div>
            <div class="stat-label">未开始</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card ongoing">
            <div class="stat-number">{{ matchStatistics.ongoing }}</div>
            <div class="stat-label">进行中</div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card finished">
            <div class="stat-number">{{ matchStatistics.finished }}</div>
            <div class="stat-label">已结束</div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card info">
            <div class="current-time">
              <i class="el-icon-time"></i>
              当前时间: {{ new Date().toLocaleString('zh-CN') }}
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="schedule-list">
      <el-card
        v-for="match in filteredMatches"
        :key="match.id"
        class="match-card"
        :class="getMatchStatusClass(match)"
      >
        <div class="match-header">
          <div class="match-teams">
            <span class="team home">{{ match.homeTeam }}</span>
            <span class="vs">VS</span>
            <span class="team away">{{ match.awayTeam }}</span>
          </div>
          <div class="match-status" :class="getStatusClass(match)">
            {{ getStatusText(match) }}
          </div>
        </div>

        <div class="match-info">
          <div class="match-time">
            <i class="el-icon-time"></i>
            <span>{{ formatDateTime(match.matchTime) }}</span>
            <span v-if="match.status === 'upcoming'" class="time-until">
              ({{ getRelativeTime(match.matchTime) }})
            </span>
          </div>
          <div class="match-venue">
            <i class="el-icon-location"></i>
            <span>{{ match.venue }}</span>
          </div>
        </div>

        <div class="match-details">
          <div class="match-type">{{ match.matchType }}</div>
          <div class="match-round">{{ match.round }}</div>
        </div>

        <div v-if="match.status === 'ongoing'" class="live-indicator">
          <div class="live-dot"></div>
          <span>直播中</span>
        </div>
      </el-card>
    </div>

    <div v-if="filteredMatches.length === 0" class="empty-state">
      <el-empty description="暂无赛程数据" />
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ScheduleUtils } from '../utils/scheduleUtils'

export default {
  name: 'ScheduleView',
  setup() {
    // 模拟赛程数据
    const matches = ref([
      {
        id: 1,
        homeTeam: '北京国安',
        awayTeam: '上海申花',
        matchTime: '2024-01-15 19:30:00',
        venue: '工人体育场',
        matchType: '中超联赛',
        round: '第1轮',
        status: 'upcoming'
      },
      {
        id: 2,
        homeTeam: '广州恒大',
        awayTeam: '山东泰山',
        matchTime: '2024-01-15 20:00:00',
        venue: '天河体育场',
        matchType: '中超联赛',
        round: '第1轮',
        status: 'ongoing'
      },
      {
        id: 3,
        homeTeam: '江苏苏宁',
        awayTeam: '天津泰达',
        matchTime: '2024-01-14 15:30:00',
        venue: '南京奥体中心',
        matchType: '中超联赛',
        round: '第1轮',
        status: 'finished'
      },
      {
        id: 4,
        homeTeam: '大连一方',
        awayTeam: '河南建业',
        matchTime: '2024-01-16 19:35:00',
        venue: '大连体育中心',
        matchType: '中超联赛',
        round: '第1轮',
        status: 'upcoming'
      },
      {
        id: 5,
        homeTeam: '武汉卓尔',
        awayTeam: '重庆力帆',
        matchTime: '2024-01-13 16:00:00',
        venue: '武汉体育中心',
        matchType: '中超联赛',
        round: '第1轮',
        status: 'finished'
      }
    ])

    const statusFilter = ref('')
    const dateFilter = ref('')
    const teamFilter = ref('')
    const sortBy = ref('time')
    const sortOrder = ref('asc')
    let timer = null

    // 更新所有比赛状态
    const updateMatchStatuses = () => {
      matches.value.forEach(match => {
        match.status = ScheduleUtils.calculateMatchStatus(match.matchTime)
      })
    }

    // 过滤比赛
    const filteredMatches = computed(() => {
      let filtered = matches.value

      // 按状态筛选
      filtered = ScheduleUtils.filterMatchesByStatus(filtered, statusFilter.value)

      // 按日期筛选
      filtered = ScheduleUtils.filterMatchesByDate(filtered, dateFilter.value)

      // 按队伍筛选
      filtered = ScheduleUtils.filterMatchesByTeam(filtered, teamFilter.value)

      // 排序
      filtered = ScheduleUtils.sortMatches(filtered, sortBy.value, sortOrder.value)

      return filtered
    })

    // 获取比赛统计信息
    const matchStatistics = computed(() => {
      return ScheduleUtils.getMatchStatistics(matches.value)
    })

    // 组件挂载时启动定时器
    onMounted(() => {
      updateMatchStatuses()
      // 每分钟更新一次状态
      timer = setInterval(updateMatchStatuses, 60000)
    })

    // 组件卸载时清除定时器
    onUnmounted(() => {
      if (timer) {
        clearInterval(timer)
      }
    })

    return {
      matches,
      statusFilter,
      dateFilter,
      teamFilter,
      sortBy,
      sortOrder,
      filteredMatches,
      matchStatistics,
      formatDateTime: (dateTimeStr) => ScheduleUtils.formatDateTime(dateTimeStr),
      getStatusText: (match) => ScheduleUtils.getStatusText(match.status),
      getStatusClass: (match) => ScheduleUtils.getStatusClass(match.status),
      getMatchStatusClass: (match) => `match-${match.status}`,
      getRelativeTime: (matchTime) => ScheduleUtils.formatDateTime(matchTime, 'relative'),
      getTimeUntilMatch: (matchTime) => ScheduleUtils.getTimeUntilMatch(matchTime)
    }
  }
}
</script>

<style scoped>
.schedule-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 20px;
}

.header h1 {
  margin: 0;
  color: #2c3e50;
  font-size: 28px;
}

.filters {
  display: flex;
  gap: 15px;
  align-items: center;
}

.schedule-list {
  display: grid;
  gap: 20px;
}

.match-card {
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
}

.match-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.match-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.match-teams {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 18px;
  font-weight: 600;
}

.team {
  padding: 8px 16px;
  border-radius: 20px;
  background: #f8f9fa;
  color: #495057;
}

.team.home {
  background: #e3f2fd;
  color: #1976d2;
}

.team.away {
  background: #fff3e0;
  color: #f57c00;
}

.vs {
  font-weight: bold;
  color: #6c757d;
  font-size: 16px;
}

.match-status {
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
}

.status-upcoming {
  background: #e8f5e8;
  color: #2e7d32;
}

.status-ongoing {
  background: #fff3e0;
  color: #f57c00;
  animation: pulse 2s infinite;
}

.status-finished {
  background: #f5f5f5;
  color: #757575;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}

.match-info {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
  color: #6c757d;
  font-size: 14px;
}

.match-time, .match-venue {
  display: flex;
  align-items: center;
  gap: 5px;
}

.match-details {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #adb5bd;
}

.match-type, .match-round {
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 8px;
}

.live-indicator {
  position: absolute;
  top: 15px;
  right: 15px;
  display: flex;
  align-items: center;
  gap: 5px;
  background: rgba(255, 87, 34, 0.1);
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: #ff5722;
  font-weight: 600;
}

.live-dot {
  width: 8px;
  height: 8px;
  background: #ff5722;
  border-radius: 50%;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0.3; }
}

.match-upcoming {
  border-left: 4px solid #4caf50;
}

.match-ongoing {
  border-left: 4px solid #ff9800;
  background: linear-gradient(135deg, #fff8e1 0%, #ffffff 100%);
}

.match-finished {
  border-left: 4px solid #9e9e9e;
  opacity: 0.8;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.statistics {
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #6c757d;
}

.stat-card.total .stat-number {
  color: #007bff;
}

.stat-card.upcoming .stat-number {
  color: #28a745;
}

.stat-card.ongoing .stat-number {
  color: #ffc107;
  animation: pulse 2s infinite;
}

.stat-card.finished .stat-number {
  color: #6c757d;
}

.stat-card.info {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.current-time {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  color: #495057;
}

.time-until {
  color: #28a745;
  font-size: 12px;
  margin-left: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filters {
    flex-direction: column;
  }
  
  .match-teams {
    font-size: 16px;
    gap: 10px;
  }
  
  .match-info {
    flex-direction: column;
    gap: 10px;
  }
}
</style> 