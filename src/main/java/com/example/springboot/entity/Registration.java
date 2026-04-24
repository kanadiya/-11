package com.example.springboot.entity;

import java.time.LocalDateTime;

public class Registration {
    private Long id;
    private Long userId;
    private Long scheduleId;
    private String status; // 报名状态：PENDING(待审核)、APPROVED(已通过)、REJECTED(已拒绝)、CANCELLED(已取消)
    private LocalDateTime registerTime;
    private LocalDateTime updateTime;
    
    // 关联查询字段
    private String username;
    private String realName;
    private String phone;
    private String projectName;
    private String scheduleDate;
    private String scheduleTime;
    private String venue;

    // 构造函数
    public Registration() {}

    public Registration(Long userId, Long scheduleId) {
        this.userId = userId;
        this.scheduleId = scheduleId;
        this.status = "PENDING"; // 默认为待审核状态
        this.registerTime = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", userId=" + userId +
                ", scheduleId=" + scheduleId +
                ", status='" + status + '\'' +
                ", registerTime=" + registerTime +
                ", updateTime=" + updateTime +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", projectName='" + projectName + '\'' +
                ", scheduleDate='" + scheduleDate + '\'' +
                ", scheduleTime='" + scheduleTime + '\'' +
                ", venue='" + venue + '\'' +
                '}';
    }
} 