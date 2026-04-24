package com.example.springboot.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProjectMember {
    private Long id;
    private Long projectId;
    private Long userId;
    private String role;
    private LocalDate joinDate;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String projectName;
    private String userName;
    private String realName;
    private String userRole;

    // 构造函数
    public ProjectMember() {}

    public ProjectMember(Long projectId, Long userId, String role) {
        this.projectId = projectId;
        this.userId = userId;
        this.role = role;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "ProjectMember{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", userId=" + userId +
                ", role='" + role + '\'' +
                ", joinDate=" + joinDate +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 