import { userApi } from './api'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 退出登录
export const logout = async () => {
  try {
    // 调用后端退出登录API
    await userApi.logout()
    
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('rememberedUser')
    
    ElMessage.success('退出登录成功')
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    // 即使后端调用失败，也要清除本地存储并跳转
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('rememberedUser')
    router.push('/login')
  }
}

// 检查是否已登录
export const isLoggedIn = () => {
  const token = localStorage.getItem('token')
  return !!token
}

// 获取当前用户信息
export const getCurrentUser = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    try {
      return JSON.parse(userInfo)
    } catch (error) {
      console.error('解析用户信息失败:', error)
      return null
    }
  }
  return null
}

// 检查用户权限
export const hasPermission = (requiredRole) => {
  const user = getCurrentUser()
  if (!user) return false
  
  // 管理员拥有所有权限
  if (user.role === 'ADMIN') return true
  
  return user.role === requiredRole
} 