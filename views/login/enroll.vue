<template>
  <div class="register-container">
    <div class="bg-shape shape-1"></div>
    <div class="bg-shape shape-2"></div>
    <div class="bg-shape shape-3"></div>

    <div class="register-box animate-fade-in-up">
      <div class="register-header">
        <div class="icon-wrapper">
          <span class="edit-icon">📝</span>
        </div>
        <h1 class="logo">创建新账号</h1>
        <p class="subtitle">Join Sports Management System</p>
      </div>

      <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          @submit.prevent="handleRegister"
      >
        <el-form-item prop="username" class="stagger-1">
          <el-input
              v-model="registerForm.username"
              placeholder="请输入学号/用户名"
              size="large"
              clearable
              class="custom-input"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password" class="stagger-2">
          <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请设置密码"
              size="large"
              show-password
              clearable
              class="custom-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword" class="stagger-3">
          <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              size="large"
              show-password
              clearable
              class="custom-input"
              @keyup.enter="handleRegister"
          >
            <template #prefix>
              <el-icon><CircleCheck /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="realName" class="stagger-4">
          <el-input
              v-model="registerForm.realName"
              placeholder="请输入真实姓名"
              size="large"
              clearable
              class="custom-input"
          >
            <template #prefix>
              <el-icon><Edit /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="phone" class="stagger-5">
          <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              size="large"
              clearable
              class="custom-input"
          >
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email" class="stagger-6">
          <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱"
              size="large"
              clearable
              class="custom-input"
          >
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item class="stagger-7 action-row">
          <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="loading"
              native-type="submit"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer stagger-5">
        <p>已有账号？ <a href="javascript:void(0)" @click.prevent="goLogin">返回登录</a></p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, CircleCheck, Edit, Phone, Message } from '@element-plus/icons-vue'
import { userApi } from '@/utils/api'

export default {
  name: 'Enroll',
  components: {
    User,
    Lock,
    CircleCheck,
    Edit,
    Phone,
    Message
  },
  setup() {
    const router = useRouter()
    const registerFormRef = ref()
    const loading = ref(false)

    const registerForm = reactive({
      username: '',
      password: '',
      confirmPassword: '',
      realName: '',
      phone: '',
      email: ''
    })

    // 自定义验证：确认密码必须与密码一致
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== registerForm.password) {
        callback(new Error('两次输入的密码不一致!'))
      } else {
        callback()
      }
    }

    const registerRules = {
      username: [
        { required: true, message: '请输入学号/用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请设置密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, validator: validateConfirmPassword, trigger: 'blur' }
      ],
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
      ]
    }

    const handleRegister = async () => {
      if (!registerFormRef.value) return

      try {
        await registerFormRef.value.validate()
        loading.value = true

        await userApi.register({
          username: registerForm.username,
          password: registerForm.password,
          confirmPassword: registerForm.confirmPassword,
          realName: registerForm.realName,
          phone: registerForm.phone,
          email: registerForm.email
        })

        ElMessage.success('注册成功！请登录')

        // 注册成功后，带着用户名跳回登录页自动回填
        router.push({ path: '/login', query: { username: registerForm.username } })

      } catch (error) {
        console.error('注册失败:', error)
        ElMessage.error(error.message || '注册失败，请检查填写信息或稍后重试')
      } finally {
        loading.value = false
      }
    }

    const goLogin = () => {
      // 也可以带上当前输入的用户名回去
      router.push({ path: '/login', query: { username: registerForm.username } })
    }

    return {
      registerFormRef,
      registerForm,
      registerRules,
      loading,
      handleRegister,
      goLogin
    }
  }
}
</script>

<style scoped>
/* 全局容器：延续灰蓝色底色，保持系统级统一 */
.register-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f4f8;
  overflow: hidden;
}

/* 动态背景光球 - 注册页改变了光球的初始位置和主色调比例 */
.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  animation: float 20s infinite ease-in-out alternate;
}

.shape-1 {
  width: 450px;
  height: 450px;
  background: rgba(99, 102, 241, 0.35); /* 偏向靛蓝色 */
  top: 10%;
  right: -10%;
}

.shape-2 {
  width: 400px;
  height: 400px;
  background: rgba(56, 189, 248, 0.3);
  bottom: -10%;
  left: -5%;
  animation-delay: -7s;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: rgba(168, 85, 247, 0.2); /* 偏向紫色，增加浪漫与创造感 */
  top: 40%;
  left: 20%;
  animation-delay: -12s;
}

/* 毛玻璃注册面板 */
.register-box {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  border-radius: 24px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.08),
  0 0 0 1px rgba(255, 255, 255, 0.5) inset;
  padding: 48px 40px;
  width: 100%;
  max-width: 420px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.register-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 30px 60px -15px rgba(0, 0, 0, 0.1),
  0 0 0 1px rgba(255, 255, 255, 0.6) inset;
}

/* 头部样式 */
.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.icon-wrapper {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe); /* 靛蓝渐变 */
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 10px 20px -5px rgba(99, 102, 241, 0.3);
  transform: rotate(5deg);
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.register-box:hover .icon-wrapper {
  transform: rotate(-5deg) scale(1.05);
}

.edit-icon {
  font-size: 30px;
}

.logo {
  font-size: 24px;
  color: #0f172a;
  margin: 0 0 4px;
  font-weight: 800;
  letter-spacing: 0.5px;
}

.subtitle {
  color: #64748b;
  font-size: 13px;
  margin: 0;
  font-weight: 500;
  letter-spacing: 1px;
  text-transform: uppercase;
}

/* 表单输入框微交互 */
.custom-input :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 0 0 1px transparent inset, 0 2px 4px rgba(0,0,0,0.02) !important;
  border: 1px solid #e2e8f0;
  padding: 4px 12px;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: #c7d2fe;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  border-color: #6366f1;
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1) !important;
}

.custom-input :deep(.el-input__inner) {
  height: 44px;
  font-size: 15px;
  color: #1e293b;
}

.action-row {
  margin-top: 32px;
  margin-bottom: 0;
}

/* 高级感按钮 - 注册用渐变色微调，与登录呼应但不完全相同 */
.submit-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #fff;
  border: none;
  background: linear-gradient(135deg, #6366f1 0%, #3b82f6 100%);
  box-shadow: 0 10px 20px -10px rgba(99, 102, 241, 0.5);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
}

.submit-btn::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 15px 25px -10px rgba(99, 102, 241, 0.6);
}

.submit-btn:hover::after {
  left: 100%;
}

.submit-btn:active {
  transform: translateY(0);
}

/* 底部返回链接 */
.register-footer {
  text-align: center;
  margin-top: 24px;
  color: #64748b;
  font-size: 14px;
}

.register-footer a {
  color: #3b82f6;
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
  position: relative;
}

.register-footer a::after {
  content: '';
  position: absolute;
  width: 100%;
  transform: scaleX(0);
  height: 2px;
  bottom: -2px;
  left: 0;
  background-color: #3b82f6;
  transform-origin: bottom right;
  transition: transform 0.25s ease-out;
}

.register-footer a:hover::after {
  transform: scaleX(1);
  transform-origin: bottom left;
}

/* --- 关键帧动画 --- */
@keyframes float {
  0% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(40px, -30px) rotate(5deg); }
  100% { transform: translate(-20px, 40px) rotate(-5deg); }
}

@keyframes fadeInUp {
  0% { opacity: 0; transform: translateY(30px); }
  100% { opacity: 1; transform: translateY(0); }
}

/* 入场动画与错开延迟 */
.animate-fade-in-up {
  animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) both;
}

.stagger-1 { animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) 0.1s both; }
.stagger-2 { animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) 0.2s both; }
.stagger-3 { animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) 0.3s both; }
.stagger-4 { animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) 0.4s both; }
.stagger-5 { animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) 0.5s both; }
</style>