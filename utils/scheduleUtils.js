/**
 * 赛程管理工具类
 */
export class ScheduleUtils {
  /**
   * 计算比赛状态
   * @param {string} matchTime - 比赛时间字符串 (YYYY-MM-DD HH:mm:ss)
   * @returns {string} 比赛状态: 'upcoming' | 'ongoing' | 'finished'
   */
  static calculateMatchStatus(matchTime) {
    const now = new Date()
    const matchDate = new Date(matchTime)
    const oneHourLater = new Date(matchDate.getTime() + 60 * 60 * 1000)

    if (now < matchDate) {
      return 'upcoming'
    } else if (now >= matchDate && now < oneHourLater) {
      return 'ongoing'
    } else {
      return 'finished'
    }
  }

  /**
   * 格式化日期时间
   * @param {string} dateTimeStr - 日期时间字符串
   * @param {string} format - 格式化模式 ('full' | 'date' | 'time' | 'relative')
   * @returns {string} 格式化后的时间字符串
   */
  static formatDateTime(dateTimeStr, format = 'full') {
    const date = new Date(dateTimeStr)
    const now = new Date()
    
    switch (format) {
      case 'date':
        return date.toLocaleDateString('zh-CN')
      case 'time':
        return date.toLocaleTimeString('zh-CN', { 
          hour: '2-digit', 
          minute: '2-digit' 
        })
      case 'relative':
        return this.getRelativeTime(date, now)
      case 'full':
      default:
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')
        return `${year}-${month}-${day} ${hours}:${minutes}`
    }
  }

  /**
   * 获取相对时间描述
   * @param {Date} targetDate - 目标时间
   * @param {Date} now - 当前时间
   * @returns {string} 相对时间描述
   */
  static getRelativeTime(targetDate, now) {
    const diffMs = targetDate.getTime() - now.getTime()
    const diffMinutes = Math.floor(diffMs / (1000 * 60))
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

    if (diffMs < 0) {
      // 已过去的时间
      if (diffDays < -1) {
        return `${Math.abs(diffDays)}天前`
      } else if (diffHours < -1) {
        return `${Math.abs(diffHours)}小时前`
      } else {
        return `${Math.abs(diffMinutes)}分钟前`
      }
    } else {
      // 未来的时间
      if (diffDays > 0) {
        return `${diffDays}天后`
      } else if (diffHours > 0) {
        return `${diffHours}小时后`
      } else if (diffMinutes > 0) {
        return `${diffMinutes}分钟后`
      } else {
        return '即将开始'
      }
    }
  }

  /**
   * 获取状态文本
   * @param {string} status - 状态代码
   * @returns {string} 状态文本
   */
  static getStatusText(status) {
    const statusMap = {
      'upcoming': '未开始',
      'ongoing': '进行中',
      'finished': '已结束',
      'cancelled': '已取消',
      'postponed': '已延期'
    }
    return statusMap[status] || '未知'
  }

  /**
   * 获取状态样式类
   * @param {string} status - 状态代码
   * @returns {string} CSS类名
   */
  static getStatusClass(status) {
    return `status-${status}`
  }

  /**
   * 检查比赛是否在指定时间范围内
   * @param {string} matchTime - 比赛时间
   * @param {string} startDate - 开始日期
   * @param {string} endDate - 结束日期
   * @returns {boolean} 是否在范围内
   */
  static isMatchInDateRange(matchTime, startDate, endDate) {
    const matchDate = new Date(matchTime)
    const start = startDate ? new Date(startDate) : null
    const end = endDate ? new Date(endDate) : null

    if (start && matchDate < start) return false
    if (end && matchDate > end) return false
    return true
  }

  /**
   * 按状态过滤比赛
   * @param {Array} matches - 比赛列表
   * @param {string} status - 状态筛选
   * @returns {Array} 过滤后的比赛列表
   */
  static filterMatchesByStatus(matches, status) {
    if (!status) return matches
    return matches.filter(match => match.status === status)
  }

  /**
   * 按日期过滤比赛
   * @param {Array} matches - 比赛列表
   * @param {string} date - 日期筛选 (YYYY-MM-DD)
   * @returns {Array} 过滤后的比赛列表
   */
  static filterMatchesByDate(matches, date) {
    if (!date) return matches
    return matches.filter(match => {
      const matchDate = new Date(match.matchTime).toISOString().split('T')[0]
      return matchDate === date
    })
  }

  /**
   * 按队伍过滤比赛
   * @param {Array} matches - 比赛列表
   * @param {string} teamName - 队伍名称
   * @returns {Array} 过滤后的比赛列表
   */
  static filterMatchesByTeam(matches, teamName) {
    if (!teamName) return matches
    const searchTerm = teamName.toLowerCase()
    return matches.filter(match => 
      match.homeTeam.toLowerCase().includes(searchTerm) ||
      match.awayTeam.toLowerCase().includes(searchTerm)
    )
  }

  /**
   * 排序比赛列表
   * @param {Array} matches - 比赛列表
   * @param {string} sortBy - 排序字段 ('time' | 'status' | 'teams')
   * @param {string} order - 排序顺序 ('asc' | 'desc')
   * @returns {Array} 排序后的比赛列表
   */
  static sortMatches(matches, sortBy = 'time', order = 'asc') {
    const sorted = [...matches]
    
    sorted.sort((a, b) => {
      let comparison = 0
      
      switch (sortBy) {
        case 'time':
          comparison = new Date(a.matchTime) - new Date(b.matchTime)
          break
        case 'status':
          const statusOrder = { 'upcoming': 1, 'ongoing': 2, 'finished': 3 }
          comparison = statusOrder[a.status] - statusOrder[b.status]
          break
        case 'teams':
          comparison = a.homeTeam.localeCompare(b.homeTeam)
          break
        default:
          comparison = 0
      }
      
      return order === 'desc' ? -comparison : comparison
    })
    
    return sorted
  }

  /**
   * 获取比赛统计信息
   * @param {Array} matches - 比赛列表
   * @returns {Object} 统计信息
   */
  static getMatchStatistics(matches) {
    const stats = {
      total: matches.length,
      upcoming: 0,
      ongoing: 0,
      finished: 0,
      cancelled: 0,
      postponed: 0
    }

    matches.forEach(match => {
      if (stats.hasOwnProperty(match.status)) {
        stats[match.status]++
      }
    })

    return stats
  }

  /**
   * 验证比赛时间格式
   * @param {string} matchTime - 比赛时间字符串
   * @returns {boolean} 是否有效
   */
  static validateMatchTime(matchTime) {
    const date = new Date(matchTime)
    return !isNaN(date.getTime())
  }

  /**
   * 获取距离比赛开始的时间
   * @param {string} matchTime - 比赛时间
   * @returns {Object} 时间差对象
   */
  static getTimeUntilMatch(matchTime) {
    const now = new Date()
    const matchDate = new Date(matchTime)
    const diffMs = matchDate.getTime() - now.getTime()

    if (diffMs <= 0) {
      return { days: 0, hours: 0, minutes: 0, seconds: 0, isPast: true }
    }

    const days = Math.floor(diffMs / (1000 * 60 * 60 * 24))
    const hours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((diffMs % (1000 * 60)) / 1000)

    return { days, hours, minutes, seconds, isPast: false }
  }
}

export default ScheduleUtils 