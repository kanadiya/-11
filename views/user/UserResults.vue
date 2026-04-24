<template>
  <div class="user-results">
    <div class="page-header">
      <h2>我的成绩</h2>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>成绩记录</span>
          </template>
          
          <el-table :data="myResults" style="width: 100%" v-loading="loading">
            <el-table-column prop="projectName" label="比赛项目" />
            <el-table-column prop="score" label="成绩" />
            <el-table-column prop="unit" label="单位" />
            <el-table-column prop="rank" label="排名" />
            <el-table-column prop="date" label="比赛日期" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220">
              <template #default="scope">
                <el-button size="small" @click="viewResultDetail(scope.row)">详情</el-button>
                <el-button 
                  size="small" 
                  type="warning" 
                  v-if="scope.row.status !== 'DISPUTED'"
                  @click="submitAppeal(scope.row)"
                >
                  成绩疑问
                </el-button>
                <el-tag v-else type="danger">已申诉</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>成绩统计</span>
          </template>
          
          <div class="stats">
            <div class="stat-item">
              <div class="stat-number">{{ stats.total }}</div>
              <div class="stat-label">总比赛数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.medals }}</div>
              <div class="stat-label">获得奖牌</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.bestRank }}</div>
              <div class="stat-label">最佳排名</div>
            </div>
          </div>
        </el-card>
        
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>奖牌榜</span>
          </template>
          
          <div v-if="medals.length === 0" class="empty-state">
            <el-empty description="暂无奖牌" />
          </div>
          
          <div v-else>
            <div 
              v-for="medal in medals" 
              :key="medal.id"
              class="medal-item"
            >
              <div class="medal-icon">{{ getMedalIcon(medal.type) }}</div>
              <div class="medal-info">
                <div class="medal-project">{{ medal.projectName }}</div>
                <div class="medal-rank">第{{ medal.rank }}名</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 成绩详情对话框 -->
    <el-dialog
      v-model="resultDetailVisible"
      title="成绩详情"
      width="500px"
    >
      <div v-if="currentResult">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="比赛项目">{{ currentResult.projectName }}</el-descriptions-item>
          <el-descriptions-item label="成绩">{{ currentResult.score }} {{ currentResult.unit }}</el-descriptions-item>
          <el-descriptions-item label="排名">{{ currentResult.rank }}</el-descriptions-item>
          <el-descriptions-item label="比赛日期">{{ currentResult.date }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentResult.status)">
              {{ getStatusText(currentResult.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注">{{ currentResult.remark || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
    
    <!-- 申诉对话框 -->
    <el-dialog
      v-model="appealDialogVisible"
      title="提交申诉"
      width="500px"
    >
      <el-form :model="appealForm" label-width="100px">
        <el-form-item label="申诉理由">
          <el-input
            v-model="appealForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细说明申诉理由"
          />
        </el-form-item>
        <el-form-item label="相关证据">
          <el-input
            v-model="appealForm.evidence"
            type="textarea"
            :rows="3"
            placeholder="请提供相关证据或说明"
          />
        </el-form-item>
        <el-form-item label="证据图片">
          <el-upload
            list-type="picture-card"
            :auto-upload="false"
            :limit="3"
            accept="image/*"
            :file-list="appealForm.imageFiles"
            :on-change="handleImageChange"
            :on-remove="handleImageRemove"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="appealDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAppealForm">提交申诉</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { resultApi, fileApi } from '@/utils/api'
import { getCurrentUser } from '@/utils/auth'

export default {
  name: 'UserResults',
  components: { Plus },
  setup() {
    const myResults = ref([])
    const loading = ref(false)
    
    const resultDetailVisible = ref(false)
    const appealDialogVisible = ref(false)
    const currentResult = ref(null)
    
    const appealForm = reactive({
      reason: '',
      evidence: '',
      imageFiles: [],
      imageNames: []
    })

    const loadMyResults = async () => {
      const user = getCurrentUser()
      if (!user || !user.id) {
        ElMessage.error('未获取到用户信息，请重新登录')
        return
      }
      loading.value = true
      try {
        const response = await resultApi.getResultsByUser(user.id)
        if (response.code === 200) {
          myResults.value = (response.data || []).map(item => ({
            id: item.id,
            projectName: item.projectName || '未知项目',
            score: item.score,
            unit: item.unit || '',
            rank: item.rank || '-',
            date: item.scheduleDate ? String(item.scheduleDate).slice(0, 10) : '',
            status: item.status || 'PENDING',
            remark: item.remark || ''
          }))
        } else {
          ElMessage.error(response.message || '加载成绩失败')
        }
      } catch (error) {
        ElMessage.error('加载成绩失败')
      } finally {
        loading.value = false
      }
    }
    
    // 计算统计数据
    const stats = computed(() => {
      const total = myResults.value.length
      const numericRanks = myResults.value
        .map(r => Number(r.rank))
        .filter(r => !Number.isNaN(r))
      const medals = numericRanks.filter(r => r <= 3).length
      const bestRank = numericRanks.length > 0 ? Math.min(...numericRanks) : '-'
      
      return { total, medals, bestRank }
    })
    
    // 计算奖牌
    const medals = computed(() => {
      return myResults.value
        .filter(r => Number(r.rank) <= 3 && r.status === 'CONFIRMED')
        .map(r => ({
          id: r.id,
          projectName: r.projectName,
          rank: r.rank,
          type: r.rank === 1 ? 'gold' : r.rank === 2 ? 'silver' : 'bronze'
        }))
    })
    
    const getStatusType = (status) => {
      const types = {
        PENDING: 'warning',
        CONFIRMED: 'success',
        DISPUTED: 'danger'
      }
      return types[status] || 'info'
    }

    const getStatusText = (status) => {
      const textMap = {
        PENDING: '待确认',
        CONFIRMED: '已确认',
        DISPUTED: '有争议'
      }
      return textMap[status] || status
    }
    
    const getMedalIcon = (type) => {
      const icons = {
        gold: '🥇',
        silver: '🥈',
        bronze: '🥉'
      }
      return icons[type] || '🏅'
    }
    
    const viewResultDetail = (result) => {
      currentResult.value = result
      resultDetailVisible.value = true
    }
    
    const submitAppeal = (result) => {
      currentResult.value = result
      Object.assign(appealForm, {
        reason: '',
        evidence: '',
        imageFiles: [],
        imageNames: []
      })
      appealDialogVisible.value = true
    }

    const toDataUrl = (file) => {
      return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = () => resolve(reader.result)
        reader.onerror = reject
        reader.readAsDataURL(file)
      })
    }

    const handleImageChange = async (uploadFile, uploadFiles) => {
      appealForm.imageFiles = uploadFiles.slice(0, 3)
      appealForm.imageNames = appealForm.imageFiles.map(f => f.name).filter(Boolean)
    }

    const handleImageRemove = async (uploadFile, uploadFiles) => {
      appealForm.imageFiles = uploadFiles
      appealForm.imageNames = uploadFiles.map(f => f.name).filter(Boolean)
    }
    
    const submitAppealForm = () => {
      if (!appealForm.reason.trim()) {
        ElMessage.warning('请填写申诉理由')
        return
      }

      ;(async () => {
        try {
          const imageUrls = []
          for (const f of appealForm.imageFiles) {
            if (f.raw) {
              const up = await fileApi.uploadImage(f.raw)
              if (up.code === 200 && up.data?.url) {
                imageUrls.push(up.data.url)
              }
            }
          }

          const res = await resultApi.submitAppeal(currentResult.value.id, {
            reason: appealForm.reason,
            evidence: `${appealForm.evidence || ''}${imageUrls.length ? `\n【申诉图片】${imageUrls.join('||')}` : ''}`
          })

          if (res.code === 200) {
            ElMessage.success('申诉提交成功，等待裁判处理')
            appealDialogVisible.value = false
            loadMyResults()
          } else {
            ElMessage.error(res.message || '申诉提交失败')
          }
        } catch (e) {
          ElMessage.error('申诉提交失败')
        }
      })()
    }
    
    onMounted(() => {
      loadMyResults()
    })

    return {
      loading,
      myResults,
      stats,
      medals,
      resultDetailVisible,
      appealDialogVisible,
      currentResult,
      appealForm,
      getStatusType,
      getStatusText,
      getMedalIcon,
      viewResultDetail,
      submitAppeal,
      submitAppealForm,
      loadMyResults,
      handleImageChange,
      handleImageRemove
    }
  }
}
</script>

<style scoped>
.user-results {
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

.medal-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.medal-item:last-child {
  border-bottom: none;
}

.medal-icon {
  font-size: 2rem;
  margin-right: 15px;
}

.medal-info {
  flex: 1;
}

.medal-project {
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}

.medal-rank {
  color: #666;
  font-size: 0.9rem;
}
</style> 