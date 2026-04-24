package com.example.springboot.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private String status;
    private String priority;
    private Long assigneeId;
    private Long reporterId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private Integer progress;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String projectName;
    private String assigneeName;
    private String reporterName;

    // 构造函数
    public Task() {}

    public Task(Long projectId, String name, String description, String status, String priority) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(BigDecimal estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public BigDecimal getActualHours() {
        return actualHours;
    }

    public void setActualHours(BigDecimal actualHours) {
        this.actualHours = actualHours;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
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

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", assigneeId=" + assigneeId +
                ", reporterId=" + reporterId +
                ", startDate=" + startDate +
                ", dueDate=" + dueDate +
                ", estimatedHours=" + estimatedHours +
                ", actualHours=" + actualHours +
                ", progress=" + progress +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 