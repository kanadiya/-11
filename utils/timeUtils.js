/**
 * 时间工具类
 * 用于处理前端和后端之间的时间格式转换
 */
export class TimeUtils {
  /**
   * 格式化日期为YYYY-MM-DD格式
   * @param {Date|string} date - 日期对象或字符串
   * @returns {string} 格式化后的日期字符串
   */
  static formatDate(date) {
    if (!date) return ''
    
    if (typeof date === 'string') {
      // 处理ISO格式的日期字符串
      if (date.includes('T')) {
        return date.split('T')[0]
      }
      return date
    }
    
    // 如果是Date对象，转换为YYYY-MM-DD格式
    if (date instanceof Date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
    
    return date
  }

  /**
   * 格式化时间为HH:mm格式
   * @param {Date|string} time - 时间对象或字符串
   * @returns {string} 格式化后的时间字符串
   */
  static formatTime(time) {
    if (!time) return ''
    
    if (typeof time === 'string') {
      // 处理ISO格式的时间字符串
      if (time.includes('T')) {
        const timePart = time.split('T')[1]
        return timePart.substring(0, 5) // 只返回HH:mm部分
      }
      // 如果是HH:mm:ss格式，只返回HH:mm
      if (time.includes(':')) {
        return time.substring(0, 5)
      }
      return time
    }
    
    // 如果是Date对象，转换为HH:mm格式
    if (time instanceof Date) {
      const hours = String(time.getHours()).padStart(2, '0')
      const minutes = String(time.getMinutes()).padStart(2, '0')
      return `${hours}:${minutes}`
    }
    
    return time
  }

  /**
   * 格式化日期时间为YYYY-MM-DD HH:mm:ss格式
   * @param {Date|string} dateTime - 日期时间对象或字符串
   * @returns {string} 格式化后的日期时间字符串
   */
  static formatDateTime(dateTime) {
    if (!dateTime) return ''
    
    if (typeof dateTime === 'string') {
      return dateTime.replace('T', ' ').substring(0, 19)
    }
    
    if (dateTime instanceof Date) {
      const year = dateTime.getFullYear()
      const month = String(dateTime.getMonth() + 1).padStart(2, '0')
      const day = String(dateTime.getDate()).padStart(2, '0')
      const hours = String(dateTime.getHours()).padStart(2, '0')
      const minutes = String(dateTime.getMinutes()).padStart(2, '0')
      const seconds = String(dateTime.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }
    
    return dateTime
  }

  /**
   * 将日期和时间合并为完整的日期时间字符串
   * @param {Date|string} date - 日期
   * @param {Date|string} time - 时间
   * @returns {string} 合并后的日期时间字符串
   */
  static combineDateTime(date, time) {
    const dateStr = this.formatDate(date)
    const timeStr = this.formatTime(time)
    
    if (!dateStr || !timeStr) return ''
    
    return `${dateStr} ${timeStr}:00`
  }

  /**
   * 验证日期格式是否正确
   * @param {string} dateStr - 日期字符串
   * @returns {boolean} 是否有效
   */
  static isValidDate(dateStr) {
    if (!dateStr) return false
    
    const date = new Date(dateStr)
    return !isNaN(date.getTime())
  }

  /**
   * 验证时间格式是否正确
   * @param {string} timeStr - 时间字符串
   * @returns {boolean} 是否有效
   */
  static isValidTime(timeStr) {
    if (!timeStr) return false
    
    const timeRegex = /^([01]?[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?$/
    return timeRegex.test(timeStr)
  }

  /**
   * 获取当前日期字符串
   * @returns {string} 当前日期字符串 YYYY-MM-DD
   */
  static getCurrentDate() {
    const now = new Date()
    return this.formatDate(now)
  }

  /**
   * 获取当前时间字符串
   * @returns {string} 当前时间字符串 HH:mm
   */
  static getCurrentTime() {
    const now = new Date()
    return this.formatTime(now)
  }

  /**
   * 比较两个时间
   * @param {string} time1 - 时间1
   * @param {string} time2 - 时间2
   * @returns {number} 比较结果 (-1: time1 < time2, 0: time1 = time2, 1: time1 > time2)
   */
  static compareTime(time1, time2) {
    const t1 = this.parseTime(time1)
    const t2 = this.parseTime(time2)
    
    if (t1 < t2) return -1
    if (t1 > t2) return 1
    return 0
  }

  /**
   * 解析时间字符串为分钟数
   * @param {string} timeStr - 时间字符串 HH:mm
   * @returns {number} 分钟数
   */
  static parseTime(timeStr) {
    if (!timeStr) return 0
    
    const parts = timeStr.split(':')
    if (parts.length !== 2) return 0
    
    const hours = parseInt(parts[0], 10)
    const minutes = parseInt(parts[1], 10)
    
    return hours * 60 + minutes
  }
}

export default TimeUtils 