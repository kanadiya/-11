<template>
  <div class="result-entry">
    <div class="page-header">
      <h2>成绩录入</h2>
      <el-button type="primary" @click="showAddResultDialog">
        <el-icon><Plus /></el-icon>
        录入成绩
      </el-button>
    </div>
    
    <!-- 可用赛程列表 -->
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <span>可录入成绩的赛程</span>
      </template>
      
      <!-- 赛程类型选择 -->
      <div style="margin-bottom: 15px;">
        <el-radio-group v-model="scheduleType" @change="loadSchedulesByType">
          <el-radio-button label="assigned">我负责的赛程</el-radio-button>
          <el-radio-button label="completed">已完成但未录入成绩的赛程</el-radio-button>
        </el-radio-group>
      </div>

      
      <el-table :data="availableSchedules" style="width: 100%">
        <el-table-column prop="projectName" label="比赛项目" />
        <el-table-column prop="venue" label="场地" />
        <el-table-column prop="scheduleDate" label="比赛日期" />
        <el-table-column prop="scheduleTime" label="比赛时间" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getScheduleStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyStatus" label="申请状态" v-if="scheduleType === 'completed'">
          <template #default="scope">
            <el-tag :type="getApplyStatusType(scope.row.applyStatus)">
              {{ getApplyStatusText(scope.row.applyStatus) }}
            </el-tag>
          </template>
        </el-table-column>
                 <el-table-column label="操作" width="200">
           <template #default="scope">
             <el-button size="small" type="primary" @click="selectSchedule(scope.row)">
               选择赛程
             </el-button>
             <el-button 
               v-if="scheduleType === 'completed' && !scope.row.applyStatus" 
               size="small" 
               type="success" 
               @click="applyForResultEntry(scope.row)"
             >
               申请录入
             </el-button>
             <el-button 
               v-if="scheduleType === 'completed' && scope.row.applyStatus === 'APPROVED'" 
               size="small" 
               type="warning" 
               @click="selectSchedule(scope.row)"
             >
               开始录入
             </el-button>
           </template>
         </el-table-column>
      </el-table>
      
      <div v-if="availableSchedules.length === 0" class="no-data">
        <el-empty :description="scheduleType === 'assigned' ? '暂无可录入成绩的赛程' : '暂无可申请录入的已完成赛程'" />
      </div>
      <div v-if="historySchedules.length > 0" style="margin-top: 12px;">
        <el-alert type="info" :closable="false" show-icon title="已为你显示历史录入过成绩的赛程，可直接进入修改" />
        <el-table :data="historySchedules" style="width: 100%; margin-top: 10px;">
          <el-table-column prop="projectName" label="比赛项目" />
          <el-table-column prop="venue" label="场地" />
          <el-table-column prop="scheduleDate" label="比赛日期" />
          <el-table-column prop="scheduleTime" label="比赛时间" />
          <el-table-column label="操作" width="140">
            <template #default="scope">
              <el-button size="small" type="primary" @click="enterModifyMode(scope.row)">进入修改</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    
    <!-- 成绩录入列表 -->
    <el-card v-if="selectedSchedule">
      <template #header>
        <span>{{ selectedSchedule.projectName }} - 成绩录入</span>
      </template>
      
      <!-- 参赛者列表 -->
      <div style="margin-bottom: 20px;">
        <h4>参赛者列表</h4>
        
        <!-- 成绩统计信息 -->
        <div class="statistics-info" style="margin-bottom: 15px; padding: 10px; background-color: #f5f7fa; border-radius: 4px;">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <span class="stat-label">总参赛人数：</span>
                <span class="stat-value">{{ participants.length }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <span class="stat-label">已录入成绩：</span>
                <span class="stat-value">{{ results.length }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <span class="stat-label">待确认成绩：</span>
                <span class="stat-value">{{ pendingResults.length }}</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <span class="stat-label">录入进度：</span>
                <span class="stat-value">{{ progressPercentage }}%</span>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <el-table :data="participants" style="width: 100%">
          <el-table-column prop="realName" label="运动员姓名" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column label="成绩录入" width="300">
            <template #default="scope">
              <el-input 
                v-model="scope.row.score" 
                placeholder="请输入成绩" 
                style="width: 120px; margin-right: 10px;"
              />
              <el-select v-model="scope.row.unit" placeholder="单位" style="width: 80px; margin-right: 10px;" @change="handleUnitChange(scope.row.unit)">
                <el-option label="秒" value="秒" />
                <el-option label="米" value="米" />
                <el-option label="厘米" value="厘米" />
                <el-option label="分" value="分" />
                <el-option label="个" value="个" />
                <el-option label="次" value="次" />
              </el-select>
              <el-button size="small" type="primary" :disabled="!scope.row.score || !scope.row.unit" @click="saveSingleResult(scope.row)">
                保存
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <div style="margin-top: 15px;">
          <el-button type="success" @click="batchSaveResults" :disabled="!hasValidResults">
            保存并计算排名
          </el-button>
          <el-button @click="calculateRanks" :disabled="!hasResults">
            计算排名
          </el-button>
          <!--懒得写了，不写了，裁判要这功能没什么用处-->
<!--          <el-button type="info" @click="exportResults" :disabled="!hasResults">-->
<!--            导出成绩-->
<!--          </el-button>-->
        </div>
      </div>
      
      <!-- 已录入成绩列表 -->
      <div ref="enteredResultsSection">
        <h4>已录入成绩</h4>
        <el-table :data="results" style="width: 100%">
          <el-table-column prop="realName" label="运动员姓名" />
          <el-table-column prop="score" label="成绩" />
          <el-table-column prop="unit" label="单位" />
          <el-table-column prop="rank" label="排名" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="entryTime" label="录入时间" />
          <el-table-column prop="refereeName" label="录入裁判" />
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" @click="editResult(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteResult(scope.row)">删除</el-button>
              <el-button 
                 v-if="scope.row.status === 'PENDING' && scope.row.refereeId === currentUser?.id" 
                 size="small" 
                 type="success" 
                 @click="confirmResult(scope.row)"
               >
                确认
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    
    <!-- 新增/编辑成绩对话框 -->
    <el-dialog
      v-model="resultDialogVisible"
      :title="isEdit ? '编辑成绩' : '录入成绩'"
      width="500px"
    >
      <el-form :model="resultForm" label-width="100px">
        <el-form-item label="比赛项目">
          <el-input v-model="resultForm.projectName" disabled />
        </el-form-item>
        <el-form-item label="运动员">
          <el-select v-model="resultForm.userId" placeholder="请选择运动员">
            <el-option
              v-for="participant in participants"
              :key="participant.userId"
              :label="participant.realName"
              :value="participant.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩">
          <el-input v-model="resultForm.score" placeholder="请输入成绩" />
        </el-form-item>
        <el-form-item label="单位">
          <el-select v-model="resultForm.unit" placeholder="请选择单位">
            <el-option label="秒" value="秒" />
            <el-option label="米" value="米" />
            <el-option label="厘米" value="厘米" />
            <el-option label="分" value="分" />
            <el-option label="个" value="个" />
            <el-option label="次" value="次" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="resultForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resultDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveResult">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 申请录入成绩对话框 -->
    <el-dialog
      v-model="applyDialogVisible"
      title="申请录入成绩"
      width="500px"
    >
      <el-form :model="applyForm" label-width="100px">
        <el-form-item label="比赛项目">
          <el-input v-model="applyForm.projectName" disabled />
        </el-form-item>
        <el-form-item label="比赛时间">
          <el-input v-model="applyForm.scheduleTime" disabled />
        </el-form-item>
        <el-form-item label="申请理由">
          <el-input
            v-model="applyForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请说明申请录入成绩的理由，例如：比赛已完成但成绩未录入、原裁判无法录入等"
          />
        </el-form-item>
        <el-form-item label="预计完成时间">
          <el-date-picker
            v-model="applyForm.estimatedTime"
            type="datetime"
            placeholder="选择预计完成录入的时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="applyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApply">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { resultApi } from '@/utils/api'
import { getCurrentUser } from '@/utils/auth'

// 响应式数据
const availableSchedules = ref([])
const historySchedules = ref([])
const selectedSchedule = ref(null)
const participants = ref([])
const results = ref([])
const scheduleFixedUnit = ref('')
const resultDialogVisible = ref(false)
const isEdit = ref(false)

const resultForm = reactive({
  id: null,
  scheduleId: '',
  userId: '',
  score: '',
  unit: '',
  remark: '',
  projectName: ''
})

// 申请表单
const applyForm = reactive({
  scheduleId: '',
  projectName: '',
  scheduleTime: '',
  reason: '',
  estimatedTime: ''
})

// 申请对话框显示状态
const applyDialogVisible = ref(false)

// 计算属性
const hasValidResults = computed(() => {
  return participants.value.some(p => p.score && p.unit)
})

const hasResults = computed(() => {
  return results.value.length > 0
})

// 计算进度百分比
const progressPercentage = computed(() => {
  if (participants.value.length === 0) {
    return 0
  }
  const confirmedResults = results.value.filter(r => r.status === 'CONFIRMED').length
  return Math.round((confirmedResults / participants.value.length) * 100)
})

// 获取待确认成绩数量
const pendingResults = computed(() => {
  return results.value.filter(r => r.status === 'PENDING').length
})
const enteredResultsSection = ref(null)

// 单位联动：改一处，全表同步
const handleUnitChange = (unit) => {
  if (!unit) return
  scheduleFixedUnit.value = unit
  participants.value = participants.value.map(p => ({
    ...p,
    unit
  }))
}

const goToModifySection = async () => {
  if (!selectedSchedule.value) {
    // 自动选择一个赛程，避免用户必须手动先点“选择赛程”
    if (availableSchedules.value && availableSchedules.value.length > 0) {
      await selectSchedule(availableSchedules.value[0])
      ElMessage.info('已自动选择赛程，请在下方修改成绩')
    } else {
      // 兜底：从当前裁判历史录入成绩里反推可修改赛程
      const refereeId = currentUser.value?.id
      if (!refereeId) {
        ElMessage.warning('未获取到裁判信息，请重新登录')
        return
      }
      const res = await resultApi.getResultsByReferee(refereeId)
      if (res.code !== 200 || !Array.isArray(res.data) || res.data.length === 0) {
        ElMessage.warning('暂无可修改成绩')
        return
      }
      const first = res.data[0]
      await selectSchedule({
        scheduleId: first.scheduleId,
        projectName: first.projectName,
        venue: first.venue,
        scheduleDate: first.scheduleDate,
        scheduleTime: first.scheduleTime,
        status: first.scheduleStatus
      })
      ElMessage.info('已从历史成绩定位到可修改赛程')
    }
  }
  if (!results.value || results.value.length === 0) {
    ElMessage.warning('该赛程还没有已录入成绩')
    return
  }
  enteredResultsSection.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  ElMessage.info('请在“已录入成绩”列表中点击“编辑”进行修改')
}

const enterModifyMode = async (schedule) => {
  await selectSchedule(schedule)
  enteredResultsSection.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  ElMessage.success('已进入修改功能，请点击对应成绩的“编辑”按钮')
}

// 获取当前用户信息
const currentUser = ref(getCurrentUser())

const loadHistorySchedules = async () => {
  try {
    const refereeId = currentUser.value?.id
    if (!refereeId) return
    const res = await resultApi.getResultsByReferee(refereeId)
    if (res.code !== 200 || !Array.isArray(res.data)) {
      historySchedules.value = []
      return
    }
    const map = new Map()
    res.data.forEach(item => {
      if (item && item.scheduleId != null && !map.has(item.scheduleId)) {
        map.set(item.scheduleId, {
          scheduleId: item.scheduleId,
          projectName: item.projectName,
          venue: item.venue,
          scheduleDate: item.scheduleDate,
          scheduleTime: item.scheduleTime,
          status: item.scheduleStatus
        })
      }
    })
    historySchedules.value = Array.from(map.values())
  } catch (e) {
    historySchedules.value = []
  }
}

// 获取可用赛程
const loadAvailableSchedules = async () => {
  try {
    if (!currentUser.value || !currentUser.value.id) {
      ElMessage.error('用户信息获取失败')
      return
    }
    
    // 验证用户是否为裁判 - 支持多种角色值
    const refereeRoles = ['referee', 'REFEREE', '裁判', 'judge', 'JUDGE']
    
    if (!refereeRoles.includes(currentUser.value.role)) {
      ElMessage.error('只有裁判才能录入成绩')
      return
    }
    
    const response = await resultApi.getAvailableSchedules(currentUser.value.id)
    if (response.code === 200) {
      availableSchedules.value = response.data
    } else {
      ElMessage.error(response.message || '获取可用赛程失败')
    }
  } catch (error) {
    console.error('获取可用赛程失败:', error)
    ElMessage.error('获取可用赛程失败')
  }
}

// 选择赛程
const selectSchedule = async (schedule) => {
  selectedSchedule.value = schedule
  resultForm.scheduleId = schedule.scheduleId
  resultForm.projectName = schedule.projectName
  
  // 加载参赛者列表
  await loadParticipants(schedule.scheduleId)
  
  // 加载已录入成绩
  await loadResults(schedule.scheduleId)
}

// 加载参赛者列表
const loadParticipants = async (scheduleId) => {
  try {
    const response = await resultApi.getParticipants(scheduleId)
    if (response.code === 200) {
      participants.value = response.data.map(p => ({
        ...p,
        score: '',
        unit: scheduleFixedUnit.value || ''
      }))
    } else {
      ElMessage.error(response.message || '获取参赛者失败')
    }
  } catch (error) {
    console.error('获取参赛者失败:', error)
    ElMessage.error('获取参赛者失败')
  }
}

// 加载已录入成绩
const loadResults = async (scheduleId) => {
  try {
    const response = await resultApi.getResultsBySchedule(scheduleId)
    if (response.code === 200) {
      results.value = response.data || []
      // 兜底：部分后端数据场景下按赛程查询为空，改用裁判历史成绩反查当前赛程
      if (results.value.length === 0 && currentUser.value?.id) {
        const historyRes = await resultApi.getResultsByReferee(currentUser.value.id)
        if (historyRes.code === 200 && Array.isArray(historyRes.data)) {
          results.value = historyRes.data.filter(item => item.scheduleId === scheduleId)
        }
      }
      // 同一赛程单位固定：有已录入成绩时，后续录入默认并锁定为该单位
      scheduleFixedUnit.value = results.value.length > 0 ? (results.value[0].unit || '') : ''
      if (scheduleFixedUnit.value) {
        participants.value = participants.value.map(p => ({
          ...p,
          unit: p.unit || scheduleFixedUnit.value
        }))
      }
    } else {
      ElMessage.error(response.message || '获取成绩失败')
    }
  } catch (error) {
    console.error('获取成绩失败:', error)
    ElMessage.error('获取成绩失败')
  }
}

// 保存单个成绩
const saveSingleResult = async (participant) => {
  if (!participant.score || !participant.unit) {
    ElMessage.warning('请输入成绩和单位')
    return
  }
  if (scheduleFixedUnit.value && participant.unit !== scheduleFixedUnit.value) {
    ElMessage.warning(`该赛程单位必须统一为「${scheduleFixedUnit.value}」`)
    participant.unit = scheduleFixedUnit.value
    return
  }
  
  // 验证成绩格式
  const score = parseFloat(participant.score)
  if (isNaN(score) || score < 0) {
    ElMessage.warning('请输入有效的成绩数值')
    return
  }
  
  // 验证成绩范围（根据项目类型）
  const projectType = selectedSchedule.value.projectType
  if (projectType === 'time' && score > 3600) { // 时间项目不超过1小时
    ElMessage.warning('成绩时间过长，请检查')
    return
  }
  
  try {
    const existingResult = results.value.find(r => r.userId === participant.userId)
    const resultData = {
      scheduleId: selectedSchedule.value.scheduleId,
      userId: participant.userId,
      score: score,
      unit: participant.unit,
      remark: ''
    }
    
    const response = existingResult
      ? await resultApi.updateResult(existingResult.id, resultData)
      : await resultApi.insertResult(resultData)
    if (response.code === 200) {
      // 单条保存成功后自动计算该赛程排名
      await resultApi.calculateRanks(selectedSchedule.value.scheduleId)
      ElMessage.success(existingResult ? '成绩修改成功，排名已更新' : '成绩录入成功，排名已更新')
      // 清空输入
      participant.score = ''
      participant.unit = ''
      // 重新加载成绩列表
      await loadResults(selectedSchedule.value.scheduleId)
      await loadHistorySchedules()
    } else {
      ElMessage.error(response.message || '成绩录入失败')
    }
  } catch (error) {
    console.error('成绩录入失败:', error)
    ElMessage.error('成绩录入失败')
  }
}

// 批量保存成绩
const batchSaveResults = async () => {
  const validResults = participants.value.filter(p => p.score && p.unit)
  
  if (validResults.length === 0) {
    ElMessage.warning('没有有效的成绩数据')
    return
  }
  
  try {
    const units = [...new Set(validResults.map(p => p.unit))]
    if (scheduleFixedUnit.value && units.some(u => u !== scheduleFixedUnit.value)) {
      ElMessage.warning(`该赛程单位必须统一为「${scheduleFixedUnit.value}」`)
      return
    }
    if (!scheduleFixedUnit.value && units.length > 1) {
      ElMessage.warning('同一赛程批量录入时，单位必须一致')
      return
    }

    const existingByUserId = new Map(results.value.map(r => [r.userId, r]))
    const toInsert = []
    const toUpdate = []

    validResults.forEach(p => {
      const payload = {
        scheduleId: selectedSchedule.value.scheduleId,
        userId: p.userId,
        score: parseFloat(p.score),
        unit: p.unit,
        remark: ''
      }
      const existing = existingByUserId.get(p.userId)
      if (existing) {
        toUpdate.push({ id: existing.id, payload })
      } else {
        toInsert.push(payload)
      }
    })

    for (const item of toUpdate) {
      const updateRes = await resultApi.updateResult(item.id, item.payload)
      if (updateRes.code !== 200) {
        ElMessage.error(updateRes.message || '批量修改失败')
        return
      }
    }

    let response = { code: 200 }
    if (toInsert.length > 0) {
      response = await resultApi.batchInsertResults(toInsert)
    }
    if (response.code === 200) {
      await resultApi.calculateRanks(selectedSchedule.value.scheduleId)
      ElMessage.success('批量保存成功，排名已更新')
      // 清空所有输入
      participants.value.forEach(p => {
        p.score = ''
        p.unit = ''
      })
      // 重新加载成绩列表
      await loadResults(selectedSchedule.value.scheduleId)
      await loadHistorySchedules()
    } else {
      ElMessage.error(response.message || '批量成绩录入失败')
    }
  } catch (error) {
    console.error('批量成绩录入失败:', error)
    ElMessage.error('批量成绩录入失败')
  }
}

// 计算排名
const calculateRanks = async () => {
  try {
    const response = await resultApi.calculateRanks(selectedSchedule.value.scheduleId)
    if (response.code === 200) {
      ElMessage.success('排名计算成功')
      // 重新加载成绩列表
      await loadResults(selectedSchedule.value.scheduleId)
    } else {
      ElMessage.error(response.message || '排名计算失败')
    }
  } catch (error) {
    console.error('排名计算失败:', error)
    ElMessage.error('排名计算失败')
  }
}

// 导出成绩
// const exportResults = async () => {
//   try {
//     const response = await resultApi.exportResults(selectedSchedule.value.scheduleId)
//     if (response.code === 200) {
//       ElMessage.success('成绩导出成功')
//       // 假设后端返回的是一个文件流，需要处理下载
//       const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
//       const url = window.URL.createObjectURL(blob);
//       const link = document.createElement('a');
//       link.href = url;
//       link.setAttribute('download', `${selectedSchedule.value.projectName}-成绩.xlsx`);
//       document.body.appendChild(link);
//       link.click();
//       document.body.removeChild(link);
//       window.URL.revokeObjectURL(url);
//     } else {
//       ElMessage.error(response.message || '成绩导出失败')
//     }
//   } catch (error) {
//     console.error('成绩导出失败:', error)
//     ElMessage.error('成绩导出失败')
//   }
// }

// 显示新增成绩对话框
const showAddResultDialog = () => {
  if (!selectedSchedule.value) {
    ElMessage.warning('请先选择赛程')
    return
  }
  
  isEdit.value = false
  Object.assign(resultForm, {
    id: null,
    scheduleId: selectedSchedule.value.scheduleId,
    userId: '',
    score: '',
    unit: '',
    remark: '',
    projectName: selectedSchedule.value.projectName
  })
  resultDialogVisible.value = true
}

// 编辑成绩
const editResult = (row) => {
  isEdit.value = true
  Object.assign(resultForm, row)
  resultDialogVisible.value = true
}

// 删除成绩
const deleteResult = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个成绩记录吗？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await resultApi.deleteResult(row.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      await loadResults(selectedSchedule.value.scheduleId)
      await loadHistorySchedules()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除成绩失败:', error)
      ElMessage.error('删除成绩失败')
    }
  }
}

// 确认成绩
const confirmResult = async (row) => {
  try {
    const response = await resultApi.confirmResult(row.id)
    if (response.code === 200) {
      ElMessage.success('成绩确认成功')
      await loadResults(selectedSchedule.value.scheduleId)
    } else {
      ElMessage.error(response.message || '成绩确认失败')
    }
  } catch (error) {
    console.error('成绩确认失败:', error)
    ElMessage.error('成绩确认失败')
  }
}

// 保存成绩
const saveResult = async () => {
  if (!resultForm.userId || !resultForm.score || !resultForm.unit) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  try {
    const resultData = {
      scheduleId: resultForm.scheduleId,
      userId: resultForm.userId,
      score: parseFloat(resultForm.score),
      unit: resultForm.unit,
      remark: resultForm.remark
    }
    
    let response
    if (isEdit.value) {
      response = await resultApi.updateResult(resultForm.id, resultData)
    } else {
      response = await resultApi.insertResult(resultData)
    }
    
    if (response.code === 200) {
      ElMessage.success(isEdit.value ? '编辑成功' : '录入成功')
      resultDialogVisible.value = false
      await loadResults(selectedSchedule.value.scheduleId)
      await loadHistorySchedules()
    } else {
      ElMessage.error(response.message || (isEdit.value ? '编辑失败' : '录入失败'))
    }
  } catch (error) {
    console.error('保存成绩失败:', error)
    ElMessage.error('保存成绩失败')
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'CONFIRMED': 'success',
    'DISPUTED': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    'PENDING': '待确认',
    'CONFIRMED': '已确认',
    'DISPUTED': '有争议'
  }
  return texts[status] || status
}



// 获取赛程状态类型
const getScheduleStatusType = (status) => {
  const types = {
    'ASSIGNED': 'info',
    'COMPLETED': 'success',
    'PENDING_ENTRY': 'warning'
  }
  return types[status] || 'info'
}

// 获取申请状态类型
const getApplyStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return types[status] || 'info'
}

// 获取申请状态文本
const getApplyStatusText = (status) => {
  const texts = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return texts[status] || status
}

// 申请录入成绩
const applyForResultEntry = (schedule) => {
  // 填充申请表单
  applyForm.scheduleId = schedule.scheduleId
  applyForm.projectName = schedule.projectName
  applyForm.scheduleTime = `${schedule.scheduleDate} ${schedule.scheduleTime}`
  applyForm.reason = ''
  applyForm.estimatedTime = ''
  
  // 显示申请对话框
  applyDialogVisible.value = true
}

// 提交申请
const submitApply = async () => {
  if (!applyForm.reason.trim()) {
    ElMessage.warning('请填写申请理由')
    return
  }
  
  try {
    const applyData = {
      scheduleId: applyForm.scheduleId,
      reason: applyForm.reason,
      estimatedTime: applyForm.estimatedTime ? applyForm.estimatedTime.toISOString() : null
    }
    
    const response = await resultApi.applyForResultEntry(applyData)
    if (response.code === 200) {
      ElMessage.success('申请提交成功，请等待管理员审核')
      applyDialogVisible.value = false
      await loadSchedulesByType() // 刷新列表
    } else {
      ElMessage.error(response.message || '申请提交失败')
    }
  } catch (error) {
    console.error('申请提交失败:', error)
    ElMessage.error('申请提交失败')
  }
}

// 加载我的申请
const loadAppliedSchedules = async () => {
  try {
    if (!currentUser.value || !currentUser.value.id) {
      ElMessage.error('用户信息获取失败')
      return
    }
    const response = await resultApi.getAppliedSchedules(currentUser.value.id)
    if (response.code === 200) {
      ElMessage.success('获取申请成功')
      availableSchedules.value = response.data
    } else {
      ElMessage.error(response.message || '获取申请失败')
    }
  } catch (error) {
    console.error('获取申请失败:', error)
    ElMessage.error('获取申请失败')
  }
}

// 加载已完成但未录入成绩的赛程
const loadCompletedSchedules = async () => {
  try {
    if (!currentUser.value || !currentUser.value.id) {
      ElMessage.error('用户信息获取失败')
      return
    }
    
    const response = await resultApi.getCompletedSchedulesWithoutResults(currentUser.value.id)
    if (response.code === 200) {
      availableSchedules.value = response.data
    } else {
      ElMessage.error(response.message || '获取已完成赛程失败')
    }
  } catch (error) {
    console.error('获取已完成赛程失败:', error)
    ElMessage.error('获取已完成赛程失败')
  }
}

// 赛程类型选择
const scheduleType = ref('assigned')

// 根据赛程类型加载赛程
const loadSchedulesByType = async () => {
  if (scheduleType.value === 'assigned') {
    await loadAvailableSchedules()
  } else {
    await loadCompletedSchedules()
  }
}

// 页面加载时获取可用赛程
onMounted(() => {
  loadSchedulesByType()
  loadHistorySchedules()
})
</script>

<style scoped>
.result-entry {
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

.no-data {
  padding: 40px;
  text-align: center;
}

h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.statistics-info {
  font-size: 14px;
  color: #606266;
}

.stat-item {
  margin-bottom: 5px;
}

.stat-label {
  font-weight: bold;
  margin-right: 5px;
}

.stat-value {
  font-weight: bold;
  color: #409eff;
}
</style> 