<template>
  <div class="appeal-management">
    <div class="page-header">
      <h2>申诉处理</h2>
    </div>
    
    <el-card>
      <template #header>
        <span>申诉列表</span>
      </template>
      
      <el-table :data="appeals" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="athleteName" label="申诉人" />
        <el-table-column prop="projectName" label="比赛项目" />
        <el-table-column prop="appealReason" label="申诉理由" show-overflow-tooltip />
        <el-table-column prop="submitTime" label="提交时间" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="viewAppeal(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="handleAppeal(scope.row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 查看申诉详情对话框 -->
    <el-dialog
      v-model="appealDetailVisible"
      title="申诉详情"
      width="600px"
    >
      <div v-if="currentAppeal">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申诉人">{{ currentAppeal.athleteName }}</el-descriptions-item>
          <el-descriptions-item label="比赛项目">{{ currentAppeal.projectName }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ currentAppeal.submitTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentAppeal.status)">
              {{ currentAppeal.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申诉理由" :span="2">
            {{ currentAppeal.appealReason }}
          </el-descriptions-item>
          <el-descriptions-item label="相关证据" :span="2">
            <div>{{ currentAppeal.evidenceText || '无' }}</div>
            <div v-if="currentAppeal.evidenceImages && currentAppeal.evidenceImages.length" style="margin-top: 8px;">
              <el-image
                v-for="(img, idx) in currentAppeal.evidenceImages"
                :key="idx"
                :src="img"
                :preview-src-list="currentAppeal.evidenceImages"
                class="appeal-image"
                fit="cover"
              >
                <template #error>
                  <div class="image-error-box">历史图片文件不存在，请补传</div>
                </template>
              </el-image>
            </div>
            <div v-if="currentAppeal.evidenceFileNames && currentAppeal.evidenceFileNames.length" style="margin-top: 8px; color: #666;">
              <div>图片文件：</div>
              <div v-for="(name, idx) in currentAppeal.evidenceFileNames" :key="`f-${idx}`">- {{ name }}</div>
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
    
    <!-- 处理申诉对话框 -->
    <el-dialog
      v-model="appealHandleVisible"
      title="处理申诉"
      width="500px"
    >
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-select v-model="handleForm.result" placeholder="请选择处理结果">
            <el-option label="申诉成立" value="申诉成立" />
            <el-option label="申诉不成立" value="申诉不成立" />
            <el-option label="需要进一步调查" value="需要进一步调查" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理意见">
          <el-input
            v-model="handleForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入处理意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="appealHandleVisible = false">取消</el-button>
          <el-button type="primary" @click="saveHandleResult">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { resultApi } from '@/utils/api'

export default {
  name: 'AppealManagement',
  setup() {
    const appeals = ref([])
    
    const appealDetailVisible = ref(false)
    const appealHandleVisible = ref(false)
    const currentAppeal = ref(null)
    
    const handleForm = reactive({
      result: '',
      comment: ''
    })

    const parseAppealRemark = (remark) => {
      if (!remark) return { reason: '', evidence: '' }
      const reasonMatch = String(remark).match(/【申诉理由】([^\n]*)/)
      const evidenceMatch = String(remark).match(/【申诉证据】([^\n]*)/)
      const imageMatch = String(remark).match(/【申诉图片】([^\n]*)/)
      const rawImages = imageMatch && imageMatch[1] ? imageMatch[1].split('||').filter(Boolean) : []
      const urlImages = rawImages
        .map(v => String(v).trim())
        .filter(Boolean)
        .map(v => {
          if (v.startsWith('data:image') || v.startsWith('http')) return v
          // 兼容历史数据：仅保存了文件名时，走后端文件读取接口
          return `/api/file/image/${encodeURIComponent(v)}`
        })
      const fileNames = rawImages.filter(v => !(String(v).startsWith('data:image') || String(v).startsWith('http')))
      return {
        reason: reasonMatch ? reasonMatch[1] : String(remark),
        evidence: evidenceMatch ? evidenceMatch[1] : '',
        images: urlImages,
        fileNames
      }
    }

    const loadAppeals = async () => {
      try {
        const res = await resultApi.getResultsByStatus('DISPUTED')
        if (res.code === 200) {
          appeals.value = (res.data || []).map(item => {
            const parsed = parseAppealRemark(item.remark)
            return {
              id: item.id,
              userId: item.userId,
              athleteName: item.realName || item.username || `用户-${item.userId}`,
              projectName: item.projectName || '-',
              appealReason: parsed.reason,
              evidenceText: parsed.evidence,
              evidenceImages: parsed.images || [],
              evidenceFileNames: parsed.fileNames || [],
              submitTime: item.updateTime || item.entryTime || '',
              status: '待处理',
              raw: item
            }
          })
        } else {
          ElMessage.error(res.message || '加载申诉列表失败')
        }
      } catch (e) {
        ElMessage.error('加载申诉列表失败')
      }
    }
    
    const getStatusType = (status) => {
      const types = {
        '待处理': 'warning',
        '已处理': 'success',
        '申诉成立': 'success',
        '申诉不成立': 'danger'
      }
      return types[status] || 'info'
    }
    
    const viewAppeal = (row) => {
      currentAppeal.value = row
      appealDetailVisible.value = true
    }
    
    const handleAppeal = (row) => {
      currentAppeal.value = row
      Object.assign(handleForm, {
        result: '',
        comment: ''
      })
      appealHandleVisible.value = true
    }
    
    const saveHandleResult = () => {
      if (!handleForm.result || !handleForm.comment) {
        ElMessage.warning('请填写完整的处理信息')
        return
      }

      resultApi.handleAppeal(currentAppeal.value.id, {
        result: handleForm.result,
        comment: handleForm.comment
      }).then((res) => {
        if (res.code === 200) {
          ElMessage.success('处理成功')
          appealHandleVisible.value = false
          loadAppeals()
        } else {
          ElMessage.error(res.message || '处理失败')
        }
      }).catch(() => {
        ElMessage.error('处理失败')
      })
    }

    onMounted(() => {
      loadAppeals()
    })
    
    return {
      appeals,
      appealDetailVisible,
      appealHandleVisible,
      currentAppeal,
      handleForm,
      getStatusType,
      viewAppeal,
      handleAppeal,
      saveHandleResult,
      loadAppeals
    }
  }
}
</script>

<style scoped>
.appeal-management {
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

.appeal-image {
  width: 80px;
  height: 80px;
  margin-right: 8px;
  border-radius: 6px;
  overflow: hidden;
  vertical-align: top;
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
</style> 