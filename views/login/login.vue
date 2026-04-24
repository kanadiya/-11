<template>
  <div class="login-container">
    <div class="bg-shape shape-1"></div>
    <div class="bg-shape shape-2"></div>
    <div class="bg-shape shape-3"></div>

    <div class="login-box animate-fade-in-up">
      <div class="login-header">
        <div class="icon-wrapper">
          <span class="run-icon">🏃‍♂️</span>
        </div>
        <h1 class="logo">运动会管理系统</h1>
        <p class="subtitle">Sports Management System</p>
      </div>

      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
      >
        <el-form-item prop="username" class="stagger-1">
          <el-input
              v-model="loginForm.username"
              placeholder="请输入学号"
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
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              show-password
              clearable
              class="custom-input"
              @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item class="stagger-3 options-row">
          <el-checkbox v-model="loginForm.remember" class="custom-checkbox">记住我</el-checkbox>
          <a href="#" class="forgot-pwd">忘记密码？</a>
        </el-form-item>

        <el-form-item class="stagger-4">
          <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="loading"
              @click="handleLogin"
          >
            {{ loading ? '登录中...' : '立即登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer stagger-5">
        <p>还没有账号？ <a href="javascript:void(0)" @click.prevent="goRegister">立即注册</a></p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue' // 引入图标
import { userApi } from '@/utils/api'

export default {
  name: 'Login',
  components: {
    User,
    Lock
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const loginFormRef = ref()
    const loading = ref(false)

    const loginForm = reactive({
      username: '',
      password: '',
      remember: false
    })

    const loginRules = {
      username: [
        { required: true, message: '请输入学号', trigger: 'blur' },
        { min: 3, max: 20, message: '学号是否有误？', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
      ]
    }

    const handleLogin = async () => {
      if (!loginFormRef.value) return

      try {
        await loginFormRef.value.validate()
        loading.value = true

        // 调用后端登录API
        const response = await userApi.login({
          username: loginForm.username,
          password: loginForm.password
        })

        // 保存token
        localStorage.setItem('token', response.data.token)

        // 获取用户信息
        const userInfoResponse = await userApi.getUserInfo()
        const userInfo = userInfoResponse.data

        // 保存用户信息
        localStorage.setItem('userInfo', JSON.stringify(userInfo))

        if (loginForm.remember) {
          localStorage.setItem('rememberedUser', JSON.stringify({
            username: loginForm.username
          }))
        } else {
          localStorage.removeItem('rememberedUser')
        }

        ElMessage.success('登录成功！')
        router.push('/')

      } catch (error) {
        console.error('登录失败:', error)
        if (!error.message || error.message === '请求失败' || error.message === 'Error: 请求失败') {
          ElMessage.error('登录失败，请检查用户名和密码')
        }
      } finally {
        loading.value = false
      }
    }

    const goRegister = () => {
      router.push({ path: '/enroll', query: { username: loginForm.username } })
    }

    onMounted(() => {
      // 页面加载时检查是否有记住的用户信息
      const remembered = localStorage.getItem('rememberedUser')
      if (remembered) {
        try {
          const user = JSON.parse(remembered)
          loginForm.username = user.username
          loginForm.remember = true
        } catch (error) {
          console.error('解析记住的用户信息失败:', error)
        }
      }

      // 注册后自动回填用户名
      if (route.query.username) {
        loginForm.username = route.query.username
      }
    })

    return {
      loginFormRef,
      loginForm,
      loginRules,
      loading,
      handleLogin,
      goRegister
    }
  }
}
</script>

<style scoped>
/* 全局容器：使用高级的灰蓝色调底色，结合动态光球 */
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f4f8;
  overflow: hidden;
}

/* 动态背景光球 */
.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  animation: float 20s infinite ease-in-out alternate;
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: rgba(56, 189, 248, 0.4);
  top: -10%;
  left: -10%;
}

.shape-2 {
  width: 500px;
  height: 500px;
  background: rgba(99, 102, 241, 0.3);
  bottom: -20%;
  right: -10%;
  animation-delay: -5s;
}

.shape-3 {
  width: 300px;
  height: 300px;
  background: rgba(34, 197, 94, 0.2);
  bottom: 20%;
  left: 20%;
  animation-delay: -10s;
}

/* 毛玻璃登录面板 */
.login-box {
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

.login-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 30px 60px -15px rgba(0, 0, 0, 0.1),
  0 0 0 1px rgba(255, 255, 255, 0.6) inset;
}

/* 头部样式 */
.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.icon-wrapper {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #e0f2fe, #bae6fd);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 10px 20px -5px rgba(56, 189, 248, 0.3);
  transform: rotate(-5deg);
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.login-box:hover .icon-wrapper {
  transform: rotate(5deg) scale(1.05);
}

.run-icon {
  font-size: 32px;
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

/* 表单输入框微交互重写 */
.custom-input :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 0 0 1px transparent inset, 0 2px 4px rgba(0,0,0,0.02) !important;
  border: 1px solid #e2e8f0;
  padding: 4px 12px;
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: #bae6fd;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  border-color: #38bdf8;
  box-shadow: 0 0 0 4px rgba(56, 189, 248, 0.1) !important;
}

.custom-input :deep(.el-input__inner) {
  height: 44px;
  font-size: 15px;
  color: #1e293b;
}

/* 记住我和忘记密码选项行 */
.options-row {
  margin-bottom: 24px;
}

.options-row :deep(.el-form-item__content) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  line-height: 1;
}

.custom-checkbox {
  --el-checkbox-checked-bg-color: #38bdf8;
  --el-checkbox-checked-icon-color: #fff;
  color: #475569;
}

.forgot-pwd {
  color: #64748b;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.forgot-pwd:hover {
  color: #38bdf8;
}

/* 高级感按钮 */
.submit-btn {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #fff;
  border: none;
  background: linear-gradient(135deg, #38bdf8 0%, #2563eb 100%);
  box-shadow: 0 10px 20px -10px rgba(37, 99, 235, 0.5);
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
  box-shadow: 0 15px 25px -10px rgba(37, 99, 235, 0.6);
}

.submit-btn:hover::after {
  left: 100%;
}

.submit-btn:active {
  transform: translateY(0);
}

/* 底部注册链接 */
.login-footer {
  text-align: center;
  margin-top: 24px;
  color: #64748b;
  font-size: 14px;
}

.login-footer a {
  color: #2563eb;
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
  position: relative;
}

.login-footer a::after {
  content: '';
  position: absolute;
  width: 100%;
  transform: scaleX(0);
  height: 2px;
  bottom: -2px;
  left: 0;
  background-color: #2563eb;
  transform-origin: bottom right;
  transition: transform 0.25s ease-out;
}

.login-footer a:hover::after {
  transform: scaleX(1);
  transform-origin: bottom left;
}

/* --- 关键帧动画 --- */
@keyframes float {
  0% { transform: translate(0, 0) rotate(0deg); }
  50% { transform: translate(50px, 30px) rotate(10deg); }
  100% { transform: translate(-30px, 50px) rotate(-5deg); }
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