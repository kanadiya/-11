<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="showAddDialog = true">
              <el-icon><Plus /></el-icon>
              添加用户
            </el-button>
            <el-button type="success" @click="showImportDialog = true">
              <el-icon><Upload /></el-icon>
              批量导入
            </el-button>
            <el-button type="warning" @click="showRoleDialog = true">
              <el-icon><Setting /></el-icon>
              角色分配
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input v-model="searchForm.username" placeholder="用户名" clearable />
          </el-col>
          <el-col :span="6">
            <el-input v-model="searchForm.realName" placeholder="真实姓名" clearable />
          </el-col>
          <el-col :span="6">
            <el-select v-model="searchForm.role" placeholder="角色" clearable>
              <el-option label="管理员" value="ADMIN" />
              <el-option label="裁判" value="REFEREE" />
              <el-option label="用户" value="USER" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 用户表格 -->
      <el-table :data="users" v-loading="loading" style="width: 100%">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleText(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'inactive' ? 'danger' : 'success'">
              {{ row.status === 'inactive' ? '禁用' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="380">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="warning" @click="handleRole(row)">角色</el-button>
            <el-button 
              size="small" 
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(row)"
              :disabled="row.username === 'admin'"
            >
              删除
            </el-button>
            <el-button size="small" type="info" @click="handlePermission(row)">权限</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog 
      v-model="showAddDialog" 
      :title="editingUser ? '编辑用户' : '添加用户'"
      width="500px"
    >
      <el-form 
        ref="userFormRef" 
        :model="userForm" 
        :rules="userRules" 
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="!!editingUser" />
        </el-form-item>
                 <el-form-item label="密码" prop="password" v-if="!editingUser">
           <el-input v-model="userForm.password" type="password" show-password />
         </el-form-item>
         <el-form-item label="确认密码" prop="confirmPassword" v-if="!editingUser">
           <el-input v-model="userForm.confirmPassword" type="password" show-password />
         </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="裁判" value="REFEREE" />
            <el-option label="用户" value="USER" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveUser">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog v-model="showImportDialog" title="批量导入用户" width="600px">
      <el-upload
        class="upload-demo"
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        accept=".xlsx,.xls"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            只能上传 xlsx/xls 文件，且不超过 10MB
          </div>
        </template>
      </el-upload>
      
      <div class="import-template">
        <el-button type="text" @click="downloadTemplate">下载导入模板</el-button>
      </div>
      
      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" @click="handleImport">开始导入</el-button>
      </template>
    </el-dialog>

    <!-- 角色分配对话框 -->
    <el-dialog v-model="showRoleDialog" title="角色分配" width="500px">
      <el-form :model="roleForm" label-width="100px">
        <el-form-item label="选择用户">
          <el-select v-model="roleForm.userIds" multiple placeholder="请选择用户">
            <el-option 
              v-for="user in users" 
              :key="user.id" 
              :label="user.realName" 
              :value="user.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分配角色">
          <el-select v-model="roleForm.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="裁判" value="REFEREE" />
            <el-option label="用户" value="USER" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showRoleDialog = false">取消</el-button>
        <el-button type="primary" @click="handleBatchRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/utils/api'

// 数据
const loading = ref(false)
const users = ref([])
const showAddDialog = ref(false)
const showImportDialog = ref(false)
const showRoleDialog = ref(false)
const editingUser = ref(null)
const userFormRef = ref()

// 搜索表单
const searchForm = reactive({
  username: '',
  realName: '',
  role: ''
})

// 用户表单
const userForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  email: '',
  role: 'USER'
})

// 角色表单
const roleForm = reactive({
  userIds: [],
  role: ''
})

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单验证规则
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
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
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 获取角色类型
const getRoleType = (role) => {
  const typeMap = {
    'ADMIN': 'danger',
    'REFEREE': 'warning',
    'USER': 'info'
  }
  return typeMap[role] || 'info'
}

// 获取角色文本
const getRoleText = (role) => {
  const textMap = {
    'ADMIN': '管理员',
    'REFEREE': '裁判',
    'USER': '用户'
  }
  return textMap[role] || role
}

// 加载用户数据
const loadUsers = async () => {
  loading.value = true
  try {
    const response = await userApi.getAllUsers()
    if (response.code === 200) {
      users.value = response.data
      pagination.total = response.data.length
    } else {
      ElMessage.error(response.message || '加载用户列表失败')
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = async () => {
  loading.value = true
  try {
    const params = {}
    if (searchForm.username) params.username = searchForm.username
    if (searchForm.realName) params.realName = searchForm.realName
    if (searchForm.role) params.role = searchForm.role
    
    const response = await userApi.searchUsers(params)
    if (response.code === 200) {
      users.value = response.data
      pagination.total = response.data.length
    } else {
      ElMessage.error(response.message || '搜索失败')
    }
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 编辑用户
const handleEdit = (user) => {
  editingUser.value = user
  Object.assign(userForm, user)
  showAddDialog.value = true
}

// 切换用户状态（active/inactive）
const handleToggleStatus = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要${user.status === 'inactive' ? '启用' : '禁用'}该用户吗？`,
      '提示',
      { type: 'warning' }
    )
    
    const updatedStatus = user.status === 'inactive' ? 'active' : 'inactive'
    const updatedUser = { ...user, status: updatedStatus }
    const response = await userApi.updateUser(updatedUser)
    if (response.code === 200) {
      user.status = updatedStatus
      ElMessage.success('操作成功')
    } else {
      ElMessage.error(response.message || '操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('切换状态失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 角色管理
const handleRole = (user) => {
  roleForm.userIds = [user.id]
  roleForm.role = user.role
  showRoleDialog.value = true
}

// 删除用户
const handleDelete = async (user) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户 "${user.username}" 吗？此操作不可恢复！`,
      '确认删除',
      { type: 'warning' }
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
      ElMessage.error('删除失败')
    }
  }
}

// 权限管理
const handlePermission = (user) => {
  ElMessage.info('权限管理功能开发中...')
}

// 保存用户
const handleSaveUser = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    if (editingUser.value) {
      // 更新用户 - 编辑时不需要密码字段
      const updateData = {
        id: userForm.id,
        username: userForm.username,
        realName: userForm.realName,
        phone: userForm.phone,
        email: userForm.email,
        role: userForm.role,
        status: userForm.status
      }
      const response = await userApi.updateUser(updateData)
      if (response.code === 200) {
        ElMessage.success('更新成功')
        showAddDialog.value = false
        loadUsers()
        resetForm()
      } else {
        ElMessage.error(response.message || '更新失败')
      }
    } else {
      // 添加用户 - 需要密码确认
      const registerData = {
        username: userForm.username,
        password: userForm.password,
        confirmPassword: userForm.confirmPassword,
        realName: userForm.realName,
        phone: userForm.phone,
        email: userForm.email,
        role: userForm.role
      }
      const response = await userApi.register(registerData)
      if (response.code === 200) {
        ElMessage.success('添加成功')
        showAddDialog.value = false
        loadUsers()
        resetForm()
      } else {
        ElMessage.error(response.message || '添加失败')
      }
    }
  } catch (error) {
    console.error('保存用户失败:', error)
    ElMessage.error('操作失败')
  }
}

// 重置表单
const resetForm = () => {
  editingUser.value = null
  Object.keys(userForm).forEach(key => {
    if (key === 'role') {
      userForm[key] = 'USER'
    } else {
      userForm[key] = ''
    }
  })
}

// 文件上传
const handleFileChange = (file) => {
  console.log('选择文件:', file)
}

// 下载模板
const downloadTemplate = () => {
  ElMessage.success('模板下载中...')
}

// 导入用户
const handleImport = () => {
  ElMessage.success('导入成功')
  showImportDialog.value = false
}

// 批量分配角色
const handleBatchRole = () => {
  ElMessage.success('角色分配成功')
  showRoleDialog.value = false
  loadUsers()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  loadUsers()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadUsers()
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
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
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.import-template {
  margin-top: 10px;
  text-align: center;
}

.upload-demo {
  text-align: center;
}
</style> 