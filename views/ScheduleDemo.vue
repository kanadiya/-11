<template>
  <div class="schedule-demo">
    <div class="demo-header">
      <h1>赛程管理演示</h1>
      <p>这是一个赛程列表的演示页面，展示了比赛时间管理和状态自动更新的功能</p>
    </div>

    <div class="demo-controls">
      <el-button @click="addTestMatch" type="primary">添加测试比赛</el-button>
      <el-button @click="updateAllStatuses" type="success">更新所有状态</el-button>
      <el-button @click="clearMatches" type="danger">清空列表</el-button>
    </div>

    <ScheduleView />
  </div>
</template>

<script>
import { ref } from 'vue'
import ScheduleView from './ScheduleView.vue'
import { ScheduleUtils } from '../utils/scheduleUtils'

export default {
  name: 'ScheduleDemo',
  components: {
    ScheduleView
  },
  setup() {
    const addTestMatch = () => {
      // 这里可以添加测试比赛数据
      console.log('添加测试比赛')
    }

    const updateAllStatuses = () => {
      // 手动更新所有比赛状态
      console.log('更新所有状态')
    }

    const clearMatches = () => {
      // 清空比赛列表
      console.log('清空列表')
    }

    return {
      addTestMatch,
      updateAllStatuses,
      clearMatches
    }
  }
}
</script>

<style scoped>
.schedule-demo {
  padding: 20px;
}

.demo-header {
  text-align: center;
  margin-bottom: 30px;
}

.demo-header h1 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.demo-header p {
  color: #6c757d;
  font-size: 16px;
}

.demo-controls {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 30px;
}
</style> 