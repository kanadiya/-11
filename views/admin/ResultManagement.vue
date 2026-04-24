<template>
  <div class="result-management">
    <div class="page-header">
      <h2>成绩管理</h2>
      <el-button type="primary" @click="showAddResultDialog">
        <el-icon><Plus /></el-icon>
        新增成绩
      </el-button>
    </div>

    <el-card>
      <template #header>
        <div class="table-header">
          <span>成绩列表</span>
          <div class="filters">
            <el-input v-model="filters.athlete" placeholder="运动员" clearable style="width: 140px" />
            <el-input v-model="filters.project" placeholder="项目" clearable style="width: 140px" />
            <el-select v-model="filters.status" placeholder="状态" clearable style="width: 140px">
              <el-option label="待确认" value="PENDING" />
              <el-option label="已确认" value="CONFIRMED" />
              <el-option label="有争议" value="DISPUTED" />
            </el-select>
            <el-button @click="loadResults">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredResults" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="projectName" label="比赛项目" />
        <el-table-column prop="realName" label="运动员" />
        <el-table-column label="成绩">
          <template #default="{ row }">{{ row.score }}{{ row.unit }}</template>
        </el-table-column>
        <el-table-column prop="rank" label="排名" />
        <el-table-column prop="entryTime" label="录入时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注">
          <template #default="{ row }">
            <div class="remark-text">{{ row.remark }}</div>
            <div v-if="row.appealImages && row.appealImages.length" class="appeal-images">
              <el-image
                v-for="(img, idx) in row.appealImages"
                :key="`${row.id}-${idx}`"
                :src="img"
                :preview-src-list="row.appealImages"
                class="appeal-image"
                fit="cover"
              >
                <template #error>
                  <div class="image-error-box">申诉图片加载失败</div>
                </template>
              </el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230">
          <template #default="{ row }">
            <el-button size="small" @click="editResult(row)">编辑</el-button>
            <el-button size="small" type="success" @click="confirmResult(row)" :disabled="row.status === 'CONFIRMED'">确认</el-button>
            <el-button size="small" type="danger" @click="deleteResult(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="resultDialogVisible" :title="isEdit ? '编辑成绩' : '新增成绩'" width="520px">
      <el-form :model="resultForm" label-width="100px">
        <el-form-item label="赛程">
          <el-select v-model="resultForm.scheduleId" placeholder="请选择赛程" filterable>
            <el-option
              v-for="schedule in schedules"
              :key="schedule.id"
              :label="`${schedule.projectName || '未知项目'} - ${schedule.date || ''} ${schedule.time || ''}`"
              :value="schedule.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="运动员">
          <el-select v-model="resultForm.userId" placeholder="请选择运动员" filterable>
            <el-option
              v-for="athlete in athletes"
              :key="athlete.id"
              :label="athlete.realName || athlete.username"
              :value="athlete.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩">
          <el-input-number v-model="resultForm.score" :min="0.001" :step="0.001" :precision="3" style="width: 100%" />
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
        <el-form-item label="排名">
          <el-input v-model="resultForm.rank" placeholder="例如：1" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="resultForm.status" placeholder="请选择状态">
            <el-option label="待确认" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="有争议" value="DISPUTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="resultForm.remark" type="textarea" :rows="2" />
        </el-form-item>

        <div v-if="dialogAppealImages && dialogAppealImages.length" class="appeal-images-dialog">
          <div class="appeal-images-title">申诉图片预览</div>
          <el-image
            v-for="(img, idx) in dialogAppealImages"
            :key="`dlg-${resultForm.id || 'new'}-${idx}`"
            :src="img"
            :preview-src-list="dialogAppealImages"
            class="appeal-image"
            fit="cover"
          >
            <template #error>
              <div class="image-error-box">申诉图片加载失败</div>
            </template>
          </el-image>
        </div>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resultDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveResult">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { resultApi, scheduleApi, userApi } from '@/utils/api'

export default {
  name: 'ResultManagement',
  setup() {
    const results = ref([])
    const schedules = ref([])
    const athletes = ref([])
    const loading = ref(false)
    const resultDialogVisible = ref(false)
    const isEdit = ref(false)
    const filters = reactive({
      athlete: '',
      project: '',
      status: ''
    })
    
    const resultForm = reactive({
      id: null,
      scheduleId: '',
      userId: '',
      refereeId: '',
      score: null,
      unit: '秒',
      rank: '',
      status: 'PENDING',
      remark: ''
    })
    
    const getStatusType = (status) => {
      const types = {
        PENDING: 'info',
        CONFIRMED: 'success',
        DISPUTED: 'warning'
      }
      return types[status] || 'info'
    }

    const getStatusText = (status) => {
      const text = {
        PENDING: '待确认',
        CONFIRMED: '已确认',
        DISPUTED: '有争议'
      }
      return text[status] || status
    }

    const parseAppealRemark = (remark) => {
      if (!remark) return { images: [] }
      const str = String(remark)
      const imageMatch = str.match(/【申诉图片】([^\n]*)/)
      const rawImages = imageMatch && imageMatch[1]
        ? imageMatch[1].split('||').filter(Boolean)
        : []
      const images = rawImages
        .map(v => String(v).trim())
        .filter(Boolean)
        .map(v => {
          if (v.startsWith('data:image') || v.startsWith('http')) return v
          // 兼容历史数据：只存了文件名，走后端读取接口
          return `/api/file/image/${encodeURIComponent(v)}`
        })
      return { images }
    }

    const dialogAppealImages = computed(() => parseAppealRemark(resultForm.remark).images)

    const filteredResults = computed(() => {
      return results.value
        .filter(item => {
        const athleteOk = !filters.athlete || (item.realName || '').includes(filters.athlete)
        const projectOk = !filters.project || (item.projectName || '').includes(filters.project)
        const statusOk = !filters.status || item.status === filters.status
        return athleteOk && projectOk && statusOk
        })
        .map(item => {
          const parsed = parseAppealRemark(item.remark)
          return {
            ...item,
            appealImages: parsed.images
          }
        })
    })

    const loadResults = async () => {
      loading.value = true
      try {
        const res = await resultApi.getAllResults()
        if (res.code === 200) {
          results.value = (res.data || []).map(item => ({
            ...item,
            entryTime: item.entryTime ? String(item.entryTime).replace('T', ' ').slice(0, 19) : ''
          }))
        } else {
          ElMessage.error(res.message || '加载成绩失败')
        }
      } catch (e) {
        ElMessage.error('加载成绩失败')
      } finally {
        loading.value = false
      }
    }

    const loadSchedulesAndAthletes = async () => {
      try {
        const [scheduleRes, usersRes] = await Promise.all([
          scheduleApi.getScheduleList(),
          userApi.getAllUsers()
        ])
        if (scheduleRes.code === 200) {
          schedules.value = scheduleRes.data || []
        }
        if (usersRes.code === 200) {
          athletes.value = (usersRes.data || []).filter(u => u.role === 'USER')
        }
      } catch (e) {
        ElMessage.error('加载基础数据失败')
      }
    }
    
    const showAddResultDialog = () => {
      isEdit.value = false
      Object.assign(resultForm, {
        id: null,
        scheduleId: '',
        userId: '',
        refereeId: '',
        score: null,
        unit: '秒',
        rank: '',
        status: 'PENDING',
        remark: ''
      })
      resultDialogVisible.value = true
    }
    
    const editResult = (row) => {
      isEdit.value = true
      Object.assign(resultForm, {
        id: row.id,
        scheduleId: row.scheduleId,
        userId: row.userId,
        refereeId: row.refereeId,
        score: row.score ? Number(row.score) : null,
        unit: row.unit || '秒',
        rank: row.rank || '',
        status: row.status || 'PENDING',
        remark: row.remark || ''
      })
      resultDialogVisible.value = true
    }
    
    const deleteResult = async (row) => {
      try {
        await ElMessageBox.confirm('确定要删除这个成绩记录吗？', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const res = await resultApi.deleteResult(row.id)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          await loadResults()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (e) {
        // cancel
      }
    }
    
    const saveResult = async () => {
      try {
        if (!resultForm.scheduleId || !resultForm.userId || !resultForm.score || !resultForm.unit) {
          ElMessage.warning('请填写完整信息')
          return
        }
        const payload = {
          scheduleId: resultForm.scheduleId,
          userId: resultForm.userId,
          refereeId: resultForm.refereeId || undefined,
          score: resultForm.score,
          unit: resultForm.unit,
          rank: resultForm.rank,
          status: resultForm.status,
          remark: resultForm.remark
        }
        let res
        if (isEdit.value) {
          res = await resultApi.updateResult(resultForm.id, payload)
        } else {
          res = await resultApi.insertResult(payload)
        }
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
          resultDialogVisible.value = false
          await loadResults()
        } else {
          ElMessage.error(res.message || '保存失败')
        }
      } catch (e) {
        ElMessage.error('保存失败')
      }
    }

    const confirmResult = async (row) => {
      try {
        const res = await resultApi.confirmResult(row.id)
        if (res.code === 200) {
          ElMessage.success('成绩已确认')
          await loadResults()
        } else {
          ElMessage.error(res.message || '确认失败')
        }
      } catch (e) {
        ElMessage.error('确认失败')
      }
    }

    onMounted(async () => {
      await loadSchedulesAndAthletes()
      await loadResults()
    })
    
    return {
      loading,
      results,
      schedules,
      athletes,
      filters,
      filteredResults,
      resultDialogVisible,
      isEdit,
      resultForm,
      getStatusType,
      getStatusText,
      showAddResultDialog,
      editResult,
      deleteResult,
      saveResult,
      loadResults,
      confirmResult,
      dialogAppealImages
    }
  }
}
</script>

<style scoped>
.result-management {
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

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.filters {
  display: flex;
  align-items: center;
  gap: 8px;
}

.remark-text {
  white-space: pre-wrap;
  word-break: break-word;
  max-width: 520px;
  color: #333;
  line-height: 1.4;
}

.appeal-images {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.appeal-image {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  overflow: hidden;
}

.image-error-box {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 12px;
  line-height: 1.2;
  color: #f56c6c;
  background: #fef0f0;
  padding: 6px;
  box-sizing: border-box;
}

.appeal-images-dialog {
  margin-top: 6px;
}

.appeal-images-title {
  font-size: 12px;
  color: #666;
  margin-bottom: 6px;
}

</style> 