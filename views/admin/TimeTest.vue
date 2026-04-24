<template>
  <div class="time-test">
    <h2>时间格式测试</h2>
    
    <el-card>
      <template #header>
        <span>时间格式转换测试</span>
      </template>
      
      <el-form :model="testForm" label-width="120px">
        <el-form-item label="测试日期">
          <el-date-picker
            v-model="testForm.date"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="测试时间">
          <el-time-picker
            v-model="testForm.time"
            placeholder="选择时间"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="testFormatting">测试格式化</el-button>
          <el-button @click="clearForm">清空</el-button>
        </el-form-item>
      </el-form>
      
      <div v-if="testResults.length > 0" class="test-results">
        <h3>测试结果：</h3>
        <el-table :data="testResults" style="width: 100%">
          <el-table-column prop="type" label="类型" width="120" />
          <el-table-column prop="input" label="输入值" />
          <el-table-column prop="output" label="输出值" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { TimeUtils } from '@/utils/timeUtils'

const testForm = reactive({
  date: '',
  time: ''
})

const testResults = ref([])

const testFormatting = () => {
  testResults.value = []
  
  // 测试日期格式化
  if (testForm.date) {
    testResults.value.push({
      type: '日期格式化',
      input: testForm.date.toString(),
      output: TimeUtils.formatDate(testForm.date)
    })
  }
  
  // 测试时间格式化
  if (testForm.time) {
    testResults.value.push({
      type: '时间格式化',
      input: testForm.time.toString(),
      output: TimeUtils.formatTime(testForm.time)
    })
  }
  
  // 测试合并日期时间
  if (testForm.date && testForm.time) {
    testResults.value.push({
      type: '合并日期时间',
      input: `${testForm.date.toString()} + ${testForm.time.toString()}`,
      output: TimeUtils.combineDateTime(testForm.date, testForm.time)
    })
  }
  
  // 测试各种字符串格式
  const testStrings = [
    '2024-01-15',
    '2024-01-15T10:30:00',
    '10:30',
    '10:30:00',
    '2024-01-15T10:30:00.000Z'
  ]
  
  testStrings.forEach(str => {
    if (str.includes(':')) {
      testResults.value.push({
        type: '时间字符串解析',
        input: str,
        output: TimeUtils.formatTime(str)
      })
    } else {
      testResults.value.push({
        type: '日期字符串解析',
        input: str,
        output: TimeUtils.formatDate(str)
      })
    }
  })
}

const clearForm = () => {
  testForm.date = ''
  testForm.time = ''
  testResults.value = []
}
</script>

<style scoped>
.time-test {
  padding: 20px;
}

.test-results {
  margin-top: 20px;
}

.test-results h3 {
  margin-bottom: 15px;
  color: #409eff;
}
</style> 