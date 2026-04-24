<template>
  <div class="user-profile">
    <div class="page-header">
      <h2>个人信息</h2>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>基本信息</span>
          </template>
          
          <div class="profile-avatar">
            <el-avatar :size="100" :src="userInfo.avatar" icon="UserFilled" />
            <h3>{{ userInfo.name }}</h3>
            <p>{{ userInfo.studentId }}</p>
          </div>
          
          <el-divider />
          
          <div class="profile-info">
            <div class="info-item">
              <span class="label">姓名：</span>
              <span class="value">{{ userInfo.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">学号：</span>
              <span class="value">{{ userInfo.studentId }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别：</span>
              <span class="value">{{ userInfo.gender }}</span>
            </div>
            <div class="info-item">
              <span class="label">年龄：</span>
              <span class="value">{{ userInfo.age }}岁</span>
            </div>
            <div class="info-item">
              <span class="label">学院：</span>
              <span class="value">{{ userInfo.college }}</span>
            </div>
            <div class="info-item">
              <span class="label">专业：</span>
              <span class="value">{{ userInfo.major }}</span>
            </div>
            <div class="info-item">
              <span class="label">班级：</span>
              <span class="value">{{ userInfo.class }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机：</span>
              <span class="value">{{ userInfo.phone }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span class="value">{{ userInfo.email }}</span>
            </div>
          </div>
          
          <el-button type="primary" style="width: 100%; margin-top: 20px;" @click="editProfile">
            编辑信息
          </el-button>
        </el-card>

        <!-- 偏好设置 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>偏好设置</span>
          </template>
          <el-form :model="preferences" label-width="120px">
            <el-form-item label="公告通知">
              <el-switch v-model="preferences.announcementNotify" @change="savePreferences" />
            </el-form-item>
            <el-form-item label="成绩更新通知">
              <el-switch v-model="preferences.resultNotify" @change="savePreferences" />
            </el-form-item>
            <el-form-item label="界面主题">
              <el-radio-group v-model="preferences.theme" @change="handleThemeChange">
                <el-radio label="green">绿色主题</el-radio>
                <el-radio label="red">红色主题</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <el-col :span="16">
        <!-- 修改密码 -->
        <el-card style="margin-bottom: 20px;">
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmNewPassword">
              <el-input v-model="passwordForm.confirmNewPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">提交</el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card>
          <template #header>
            <span>参赛记录</span>
          </template>
          
          <el-table :data="competitionHistory" style="width: 100%">
            <el-table-column prop="year" label="年份" width="80" />
            <el-table-column prop="competition" label="比赛名称" />
            <el-table-column prop="project" label="参赛项目" />
            <el-table-column prop="result" label="成绩" />
            <el-table-column prop="rank" label="排名" />
            <el-table-column prop="award" label="获奖情况">
              <template #default="scope">
                <el-tag v-if="scope.row.award" :type="getAwardType(scope.row.award)">
                  {{ scope.row.award }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="competitionHistory.length === 0" description="暂无参赛记录" />
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 编辑个人信息对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑个人信息"
      width="600px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="学号">
          <el-input v-model="editForm.studentId" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.gender" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="editForm.age" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="editForm.college" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="editForm.major" />
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="editForm.class" />
        </el-form-item>
        <el-form-item label="手机">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi, registrationApi, resultApi } from '@/utils/api'

export default {
  name: 'UserProfile',
  setup() {
    const userInfo = ref({
      name: '',
      studentId: '',
      gender: '',
      age: null,
      college: '',
      major: '',
      class: '',
      phone: '',
      email: '',
      avatar: ''
    })
    
    const competitionHistory = ref([])
    
    const editDialogVisible = ref(false)
    const editForm = reactive({
      studentId: '',
      name: '',
      gender: '',
      age: null,
      college: '',
      major: '',
      class: '',
      phone: '',
      email: '',
      avatar: ''
    })

    const passwordFormRef = ref()
    const passwordForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmNewPassword: ''
    })

    const passwordRules = {
      oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '新密码长度在 6 到 20 个字符', trigger: 'blur' }
      ],
      confirmNewPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        {
          validator: (rule, value, callback) => {
            if (value !== passwordForm.newPassword) {
              callback(new Error('两次输入的新密码不一致'))
            } else {
              callback()
            }
          },
          trigger: 'blur'
        }
      ]
    }

    const preferences = reactive({
      announcementNotify: true,
      resultNotify: true,
      theme: 'green'
    })

    const loadPreferences = () => {
      try {
        const stored = localStorage.getItem('userPreferences')
        if (stored) {
          const parsed = JSON.parse(stored)
          preferences.announcementNotify = parsed.announcementNotify ?? true
          preferences.resultNotify = parsed.resultNotify ?? true
          preferences.theme = parsed.theme || 'green'
        }
      } catch (e) {
        console.error('加载偏好设置失败:', e)
      }
    }

    const savePreferences = () => {
      try {
        localStorage.setItem('userPreferences', JSON.stringify(preferences))
      } catch (e) {
        console.error('保存偏好设置失败:', e)
      }
    }

    const applyTheme = (theme) => {
      const root = document.documentElement
      const tokens = theme === 'red'
        ? { primary: '#dc2626', success: '#16a34a', danger: '#dc2626', warning: '#f59e0b', info: '#0ea5e9' }
        : { primary: '#16a34a', success: '#16a34a', danger: '#dc2626', warning: '#f59e0b', info: '#0ea5e9' }

      root.setAttribute('data-theme', theme)
      root.style.setProperty('--el-color-primary', tokens.primary)
      root.style.setProperty('--el-color-success', tokens.success)
      root.style.setProperty('--el-color-danger', tokens.danger)
      root.style.setProperty('--el-color-warning', tokens.warning)
      root.style.setProperty('--el-color-info', tokens.info)
    }

    const handleThemeChange = () => {
      savePreferences()
      applyTheme(preferences.theme)
    }

    // 从后端加载当前用户信息
    const loadUserInfo = async () => {
      try {
        const res = await userApi.getUserInfo()
        if (res.code === 200 && res.data) {
          const u = res.data
          userInfo.value = {
            name: u.realName || u.username || '',
            studentId: u.studentId || '',
            gender: u.gender || '',
            age: u.age ?? null,
            college: u.college || '',
            major: u.major || '',
            class: u.className || '',
            phone: u.phone || '',
            email: u.email || '',
            avatar: u.avatar || ''
          }

          // 同步基础信息到 localStorage.userInfo，供路由守卫和其它页面使用
          try {
            const stored = localStorage.getItem('userInfo')
            const base = stored ? JSON.parse(stored) : {}
            const merged = {
              ...base,
              id: u.id,
              realName: u.realName,
              username: u.username,
              phone: u.phone,
              email: u.email,
              avatar: u.avatar,
              gender: u.gender,
              studentId: u.studentId,
              age: u.age,
              college: u.college,
              major: u.major,
              className: u.className,
              role: u.role
            }
            localStorage.setItem('userInfo', JSON.stringify(merged))
          } catch (e) {
            console.error('同步用户信息到本地失败:', e)
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取个人信息失败')
      }
    }
    
    const getCurrentUserId = async () => {
      try {
        const stored = localStorage.getItem('userInfo')
        if (stored) {
          const parsed = JSON.parse(stored)
          if (parsed?.id) return parsed.id
        }
      } catch (e) {}
      const infoRes = await userApi.getUserInfo()
      return infoRes?.data?.id || null
    }

    const loadCompetitionHistory = async () => {
      try {
        const userId = await getCurrentUserId()
        if (!userId) {
          competitionHistory.value = []
          return
        }
        const [regRes, resultRes] = await Promise.all([
          registrationApi.getRegistrationsByUserId(userId),
          resultApi.getResultsByUser(userId)
        ])
        const resultMap = new Map()
        ;(resultRes.data || []).forEach(r => resultMap.set(r.scheduleId, r))

        competitionHistory.value = (regRes.data || []).map(reg => {
          const result = resultMap.get(reg.scheduleId)
          const rank = result?.rank || '-'
          let award = ''
          if (String(rank) === '1') award = '金牌'
          else if (String(rank) === '2') award = '银牌'
          else if (String(rank) === '3') award = '铜牌'
          return {
            year: (reg.scheduleDate ? String(reg.scheduleDate) : '').slice(0, 4) || '-',
            competition: '运动会',
            project: reg.projectName || '-',
            result: result ? `${result.score}${result.unit || ''}` : (reg.status === 'APPROVED' ? '待录入' : reg.status),
            rank,
            award
          }
        })
      } catch (error) {
        console.error('加载参赛记录失败:', error)
        competitionHistory.value = []
      }
    }
    
    const getAwardType = (award) => {
      if (award.includes('金')) return 'success'
      if (award.includes('银')) return 'warning'
      if (award.includes('铜')) return 'info'
      return 'info'
    }
    
    const editProfile = () => {
      Object.assign(editForm, {
        studentId: userInfo.value.studentId,
        name: userInfo.value.name,
        gender: userInfo.value.gender,
        age: userInfo.value.age,
        college: userInfo.value.college,
        major: userInfo.value.major,
        class: userInfo.value.class,
        phone: userInfo.value.phone,
        email: userInfo.value.email,
        avatar: userInfo.value.avatar
      })
      editDialogVisible.value = true
    }
    
    const saveProfile = async () => {
      try {
        // `PUT /api/user/update` 后端会校验 user.id，因此这里需要带上当前用户的 id
        // 同时后端 update SQL 可能会更新 username，这里也一并带上，避免出现空值覆盖
        let current = null
        try {
          const stored = localStorage.getItem('userInfo')
          current = stored ? JSON.parse(stored) : null
        } catch (e) {
          current = null
        }

        // 如果本地没有 id，则先从后端拉一次，确保能通过后端权限校验
        let ensuredId = current?.id
        let ensuredUsername = current?.username
        if (!ensuredId) {
          try {
            const infoRes = await userApi.getUserInfo()
            if (infoRes.code === 200 && infoRes.data) {
              ensuredId = infoRes.data.id
              ensuredUsername = ensuredUsername || infoRes.data.username
            }
          } catch (e) {
            // ignore
          }
        }

        const payload = {
          id: ensuredId,
          username: ensuredUsername,
          realName: editForm.name,
          gender: editForm.gender,
          studentId: editForm.studentId,
          age: editForm.age,
          college: editForm.college,
          major: editForm.major,
          className: editForm.class,
          phone: editForm.phone,
          email: editForm.email,
          avatar: editForm.avatar
        }
        const res = await userApi.updateUser(payload)
        if (res.code === 200) {
          ElMessage.success(res.message || '个人信息更新成功')
          editDialogVisible.value = false
          await loadUserInfo()
        } else {
          ElMessage.error(res.message || '个人信息更新失败')
        }
      } catch (error) {
        console.error('更新个人信息失败:', error)
        ElMessage.error('更新个人信息失败')
      }
    }

    const handleChangePassword = async () => {
      if (!passwordFormRef.value) return
      try {
        await passwordFormRef.value.validate()
        const res = await userApi.changePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
          confirmNewPassword: passwordForm.confirmNewPassword
        })
        if (res.code === 200) {
          ElMessage.success(res.message || '密码修改成功')
          resetPasswordForm()
        } else {
          ElMessage.error(res.message || '密码修改失败')
        }
      } catch (error) {
        if (error && error.message) {
          // 响应拦截器已经提示，这里只作为兜底
          console.error('修改密码失败:', error)
        }
      }
    }

    const resetPasswordForm = () => {
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmNewPassword = ''
      if (passwordFormRef.value) {
        passwordFormRef.value.clearValidate()
      }
    }

    onMounted(() => {
      loadUserInfo()
      loadCompetitionHistory()
      loadPreferences()
      applyTheme(preferences.theme)
    })
    
    return {
      userInfo,
      competitionHistory,
      loadUserInfo,
      passwordFormRef,
      passwordForm,
      passwordRules,
      handleChangePassword,
      resetPasswordForm,
      preferences,
      savePreferences,
      loadPreferences,
      handleThemeChange,
      editDialogVisible,
      editForm,
      getAwardType,
      editProfile,
      saveProfile
    }
  }
}
</script>

<style scoped>
.user-profile {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.profile-avatar {
  text-align: center;
  margin-bottom: 20px;
}

.profile-avatar h3 {
  margin: 10px 0 5px 0;
  color: #333;
}

.profile-avatar p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.profile-info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  margin-bottom: 10px;
}

.info-item .label {
  width: 60px;
  color: #666;
  font-size: 0.9rem;
}

.info-item .value {
  flex: 1;
  color: #333;
  font-weight: 500;
}

</style> 