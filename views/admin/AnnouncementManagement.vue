<template>
  <div class="announcement-management">
    <div class="page-header">
      <h2>公告管理</h2>
      <el-button type="primary" @click="showAddAnnouncementDialog">
        <el-icon><Plus /></el-icon>
        发布公告
      </el-button>
    </div>
    
    <el-card>
      <template #header>
        <span>公告列表</span>
      </template>
      
      <el-table :data="announcements" style="width: 100%">
<!--        <el-table-column prop="id" label="ID" width="80" />-->
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="author" label="发布人" />
        <el-table-column prop="publishTime" label="发布时间" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="editAnnouncement(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteAnnouncement(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑公告对话框 -->
    <el-dialog
      v-model="announcementDialogVisible"
      :title="isEdit ? '编辑公告' : '发布公告'"
      width="600px"
    >
      <el-form :model="announcementForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="announcementForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="announcementForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入公告内容"
          />
        </el-form-item>
        <el-form-item label="发布人">
          <el-input v-model="announcementForm.author" placeholder="请输入发布人" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="announcementForm.status" placeholder="请选择状态">
            <el-option label="草稿" value="草稿" />
            <el-option label="已发布" value="已发布" />
            <el-option label="已撤回" value="已撤回" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="announcementDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAnnouncement">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { announcementApi } from '@/utils/api'

export default {
  name: 'AnnouncementManagement',
  setup() {
    const announcements = ref([])
    
    const announcementDialogVisible = ref(false)
    const isEdit = ref(false)
    
    const announcementForm = reactive({
      id: null,
      title: '',
      content: '',
      author: '',
      status: '草稿'
    })
    
    const getStatusType = (status) => {
      const types = {
        '草稿': 'info',
        '已发布': 'success',
        '已撤回': 'warning'
      }
      return types[status] || 'info'
    }
    
    const showAddAnnouncementDialog = () => {
      isEdit.value = false
      Object.assign(announcementForm, {
        id: null,
        title: '',
        content: '',
        author: '管理员',
        status: '草稿'
      })
      announcementDialogVisible.value = true
    }
    
    const editAnnouncement = (row) => {
      isEdit.value = true
      // 只复制需要的字段，避免传递publishTime等后端不需要的字段
      Object.assign(announcementForm, {
        id: row.id,
        title: row.title,
        content: row.content,
        author: row.author,
        status: row.status
      })
      announcementDialogVisible.value = true
    }
    
    const loadAnnouncements = async () => {
      try {
        const response = await announcementApi.getAllAnnouncements()
        announcements.value = response.data
      } catch (error) {
        ElMessage.error('加载公告列表失败')
      }
    }
    
    const deleteAnnouncement = async (row) => {
      try {
        await ElMessageBox.confirm(
          '确定要删除这个公告吗？',
          '警告',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        await announcementApi.deleteAnnouncement(row.id)
        ElMessage.success('删除成功')
        await loadAnnouncements()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    const saveAnnouncement = async () => {
      try {
        // 构建只包含后端需要字段的数据对象
        const requestData = {
          title: announcementForm.title,
          content: announcementForm.content,
          author: announcementForm.author,
          status: announcementForm.status
        }
        
        if (isEdit.value) {
          await announcementApi.updateAnnouncement(announcementForm.id, requestData)
          ElMessage.success('编辑成功')
        } else {
          await announcementApi.createAnnouncement(requestData)
          ElMessage.success('发布成功')
        }
        announcementDialogVisible.value = false
        await loadAnnouncements()
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '发布失败')
      }
    }
    
    // 在setup函数内部调用onMounted
    onMounted(() => {
      loadAnnouncements()
    })
    
    return {
      announcements,
      announcementDialogVisible,
      isEdit,
      announcementForm,
      getStatusType,
      showAddAnnouncementDialog,
      editAnnouncement,
      deleteAnnouncement,
      saveAnnouncement,
      loadAnnouncements
    }
  }
}
</script>

<style scoped>
.announcement-management {
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
</style> 