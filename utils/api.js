import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const api = axios.create({
  // 使用相对路径，便于跨环境部署（同域反向代理到后端）
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const { data } = response
    // 调试：打印响应数据
    console.log('API响应:', data)
    
    if (data && data.code === 200) {
      return data
    } else {
      // 后端返回了错误，但HTTP状态码是200
      // 尝试多种方式获取错误信息
      let errorMessage = '请求失败'
      if (data) {
        if (data.message) {
          errorMessage = data.message
        } else if (data.msg) {
          errorMessage = data.msg
        } else if (typeof data === 'string') {
          errorMessage = data
        } else if (data.error) {
          errorMessage = data.error
        } else {
          // 如果都没有，尝试从响应中获取
          console.warn('无法从响应中获取错误信息，响应数据:', data)
          if (data.code) {
            errorMessage = `请求失败 (${data.code})`
          }
        }
      }
      
      ElMessage.error(errorMessage)
      const error = new Error(errorMessage)
      error.response = response
      error.data = data
      return Promise.reject(error)
    }
  },
  error => {
    // HTTP请求失败（网络错误、4xx、5xx等）
    console.log('API错误:', error)
    
    if (error.response) {
      const { status, data } = error.response
      console.log('错误响应:', { status, data })
      
      let errorMessage = '请求失败'
      
      // 如果后端返回了错误信息，优先使用后端的错误信息
      if (data) {
        if (data.message) {
          errorMessage = data.message
        } else if (data.msg) {
          errorMessage = data.msg
        } else if (typeof data === 'string') {
          errorMessage = data
        } else if (data.error) {
          errorMessage = data.error
        } else {
          // 根据HTTP状态码显示默认错误信息
          switch (status) {
            case 401:
              errorMessage = '未授权，请重新登录'
              localStorage.removeItem('token')
              localStorage.removeItem('userInfo')
              window.location.href = '/login'
              break
            case 403:
              errorMessage = '权限不足'
              break
            case 404:
              errorMessage = '请求的资源不存在'
              break
            case 500:
              errorMessage = '服务器内部错误'
              break
            default:
              errorMessage = `请求失败 (${status})`
          }
        }
      } else {
        // 没有响应数据，根据状态码显示默认错误
        switch (status) {
          case 401:
            errorMessage = '未授权，请重新登录'
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            window.location.href = '/login'
            break
          case 403:
            errorMessage = '权限不足'
            break
          case 404:
            errorMessage = '请求的资源不存在'
            break
          case 500:
            errorMessage = '服务器内部错误'
            break
          default:
            errorMessage = `请求失败 (${status})`
        }
      }
      
      ElMessage.error(errorMessage)
      // 确保错误对象包含message属性
      error.message = errorMessage
    } else if (error.message) {
      // 如果错误对象已经有message，直接使用
      ElMessage.error(error.message)
    } else {
      // 网络错误
      const errorMessage = '网络错误，请检查网络连接'
      ElMessage.error(errorMessage)
      error.message = errorMessage
    }
    return Promise.reject(error)
  }
)

// 用户相关API
export const userApi = {
  // 用户登录
  login: (data) => api.post('/user/login', data),
  
  // 用户注册
  register: (data) => api.post('/user/register', data),
  
  // 获取用户信息
  getUserInfo: () => api.get('/user/info'),
  
  // 获取所有用户（管理员）
  getAllUsers: () => api.get('/user/list'),
  
  // 更新用户信息
  updateUser: (data) => api.put('/user/update', data),
  
  // 删除用户（管理员）
  deleteUser: (id) => api.delete(`/user/${id}`),
  
  // 搜索用户（管理员）
  searchUsers: (params) => api.get('/user/search', { params }),
  
  // 批量删除用户（管理员）
  batchDeleteUsers: (ids) => api.delete('/user/batch', { data: ids }),
  
  // 重置用户密码（管理员）
  resetUserPassword: (id) => api.post(`/user/${id}/reset-password`),
  
  // 退出登录
  logout: () => api.post('/user/logout'),
  
  // 修改密码
  changePassword: (data) => api.post('/user/change-password', data)
}

// 项目相关API
export const projectApi = {
  // 创建项目
  createProject: (data) => api.post('/project/create', data),
  
  // 删除项目
  deleteProject: (id) => api.delete(`/project/${id}`),
  
  // 获取所有项目
  getAllProjects: () => api.get('/project/list')
}

// 赛程相关API
export const scheduleApi = {
  // 获取所有赛程
  getScheduleList: () => api.get('/schedule/list'),
  
  // 获取未开始的比赛（用于首页显示）
  getUpcomingMatches: () => api.get('/schedule/upcoming'),
  
  // 获取进行中的比赛
  getOngoingMatches: () => api.get('/schedule/ongoing'),
  
  // 获取已完成的比赛
  getCompletedMatches: () => api.get('/schedule/completed'),
  
  // 根据项目ID获取赛程
  getSchedulesByProjectId: (projectId) => api.get(`/schedule/project/${projectId}`),
  
  // 根据ID获取赛程详情
  getScheduleById: (id) => api.get(`/schedule/${id}`),
  
  // 添加赛程
  addSchedule: (data) => api.post('/schedule/add', data),
  
  // 更新赛程
  updateSchedule: (data) => api.put('/schedule/update', data),
  
  // 删除赛程
  deleteSchedule: (id) => api.delete(`/schedule/delete/${id}`),
  
  // 获取所有项目（用于下拉选择）
  getProjectList: () => api.get('/schedule/projects')
}

// 公告相关API
export const announcementApi = {
  // 获取所有公告
  getAllAnnouncements: () => api.get('/announcement/list'),
  
  // 根据状态获取公告
  getAnnouncementsByStatus: (status) => api.get(`/announcement/status/${status}`),
  
  // 根据ID获取公告
  getAnnouncementById: (id) => api.get(`/announcement/${id}`),
  
  // 创建公告
  createAnnouncement: (data) => api.post('/announcement/create', data),
  
  // 更新公告
  updateAnnouncement: (id, data) => api.put(`/announcement/update/${id}`, data),
  
  // 删除公告
  deleteAnnouncement: (id) => api.delete(`/announcement/delete/${id}`),
  
  // 获取已发布的公告（用于首页显示）
  getPublishedAnnouncements: () => api.get('/announcement/published')
}

// 报名相关API
export const registrationApi = {
  // 用户报名比赛
  registerForMatch: (data) => api.post('/registration/register', data),
  
  // 取消报名
  cancelRegistration: (data) => api.post('/registration/cancel', data),
  
  // 查询用户是否已报名某个赛程
  isUserRegistered: (userId, scheduleId) => api.get(`/registration/check/${userId}/${scheduleId}`),
  
  // 根据用户ID查询报名记录
  getRegistrationsByUserId: (userId) => api.get(`/registration/user/${userId}`),
  
  // 根据赛程ID查询报名记录
  getRegistrationsBySchedule: (scheduleId) => api.get(`/registration/schedule/${scheduleId}`),
  
  // 查询所有报名记录（管理员功能）
  getAllRegistrations: () => api.get('/registration/all'),
  
  // 更新报名状态（管理员功能）
  updateRegistrationStatus: (id, status) => api.put(`/registration/status/${id}?status=${status}`),
  
  // 删除报名记录（管理员功能）
  deleteRegistration: (id) => api.delete(`/registration/delete/${id}`)
}

// 任务管理相关API
export const taskApi = {
  // 获取所有任务
  getAllTasks: () => api.get('/task/list'),
  
  // 根据裁判ID获取任务
  getTasksByRefereeId: (refereeId) => api.get(`/task/referee/${refereeId}`),
  
  // 创建任务
  createTask: (data) => api.post('/task/create', data),
  
  // 更新任务
  updateTask: (id, data) => api.put(`/task/update/${id}`, data),
  
  // 删除任务
  deleteTask: (id) => api.delete(`/task/delete/${id}`),
  
  // 分配裁判到任务
  assignRefereeToTask: (taskId, refereeId) => api.post(`/task/${taskId}/assign-referee`, { refereeId }),
  
  // 申请担任裁判
  applyReferee: (scheduleId, refereeId, reason) => api.post('/task/apply-referee', { scheduleId, refereeId, reason }),
  
  // 裁判接受任务
  acceptTask: (taskId) => api.post(`/task/${taskId}/accept`),
  
  // 裁判拒绝任务
  rejectTask: (taskId) => api.post(`/task/${taskId}/reject`),
  
  // 开始任务
  startTask: (taskId) => api.post(`/task/${taskId}/start`),
  
  // 完成任务
  completeTask: (taskId) => api.post(`/task/${taskId}/complete`),
  
  // 获取可用的裁判列表
  getAvailableReferees: () => api.get('/task/available-referees'),
  
  // 根据赛程ID获取任务
  getTasksByScheduleId: (scheduleId) => api.get(`/task/schedule/${scheduleId}`)
}

// 导出单个API方法，方便直接使用
export const getTasksByRefereeId = taskApi.getTasksByRefereeId
export const assignRefereeToTask = taskApi.assignRefereeToTask
export const applyReferee = taskApi.applyReferee
export const startTask = taskApi.startTask
export const completeTask = taskApi.completeTask

// 成绩录入相关API
export const resultApi = {
  // 获取所有成绩记录
  getAllResults: () => api.get('/results'),
  
  // 根据ID获取成绩记录
  getResultById: (id) => api.get(`/results/${id}`),
  
  // 根据赛程ID获取成绩记录
  getResultsBySchedule: (scheduleId) => api.get(`/results/schedule/${scheduleId}`),
  
  // 根据用户ID获取成绩记录
  getResultsByUser: (userId) => api.get(`/results/user/${userId}`),
  
  // 根据裁判ID获取成绩记录
  getResultsByReferee: (refereeId) => api.get(`/results/referee/${refereeId}`),
  
  // 根据状态获取成绩记录
  getResultsByStatus: (status) => api.get(`/results/status/${status}`),
  
  // 获取裁判可以录入成绩的赛程
  getAvailableSchedules: (refereeId) => api.get(`/results/available-schedules/${refereeId}`),
  
  // 获取某个赛程的所有参赛者
  getParticipants: (scheduleId) => api.get(`/results/participants/${scheduleId}`),
  
  // 录入成绩
  insertResult: (data) => api.post('/results', data),
  
  // 批量录入成绩
  batchInsertResults: (data) => api.post('/results/batch', data),
  
  // 更新成绩记录
  updateResult: (id, data) => api.put(`/results/${id}`, data),
  
  // 删除成绩记录
  deleteResult: (id) => api.delete(`/results/${id}`),
  
  // 更新成绩状态
  updateResultStatus: (id, status) => api.put(`/results/${id}/status?status=${status}`),
  
  // 更新排名
  updateResultRank: (id, rank) => api.put(`/results/${id}/rank?rank=${rank}`),
  
  // 确认成绩
  confirmResult: (id) => api.put(`/results/${id}/confirm`),
  
  // 计算并更新排名
  calculateRanks: (scheduleId) => api.put(`/results/schedule/${scheduleId}/calculate-ranks`),

  // 用户提交成绩申诉
  submitAppeal: (resultId, data) => api.post(`/results/${resultId}/appeal`, data),

  // 裁判处理申诉
  handleAppeal: (resultId, data) => api.put(`/results/${resultId}/appeal/handle`, data),
  
  // 导出成绩
  exportResults: (scheduleId) => api.get(`/results/export/${scheduleId}`, { responseType: 'blob' }),
  
  // 获取已完成但未录入成绩的赛程
  getCompletedSchedulesWithoutResults: (refereeId) => api.get(`/results/completed-schedules/${refereeId}`),
  
  // 申请为已完成比赛录入成绩
  applyForResultEntry: (data) => api.post('/results/apply-entry', data),
  
  // 获取裁判申请录入的赛程
  getAppliedSchedules: (refereeId) => api.get(`/results/applied-schedules/${refereeId}`),
  
  // 获取所有申请（管理员）
  getAllApplications: () => api.get('/results/applications'),
  
  // 通过申请（管理员）
  approveApplication: (applicationId) => api.put(`/results/applications/${applicationId}/approve`),
  
  // 拒绝申请（管理员）
  rejectApplication: (applicationId) => api.put(`/results/applications/${applicationId}/reject`)
}

// 文件上传相关API
export const fileApi = {
  uploadImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/file/upload-image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

// 导出单个API方法，方便直接使用
export const getScheduleList = scheduleApi.getScheduleList
export const addSchedule = scheduleApi.addSchedule
export const updateSchedule = scheduleApi.updateSchedule
export const deleteSchedule = scheduleApi.deleteSchedule
export const getProjectList = scheduleApi.getProjectList
export const getRegistrationsBySchedule = registrationApi.getRegistrationsBySchedule
export const updateRegistrationStatus = registrationApi.updateRegistrationStatus
export const deleteRegistration = registrationApi.deleteRegistration

export default api 