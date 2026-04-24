package com.example.springboot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TaskRequest {
    @NotNull(message = "项目ID不能为空")
    private Long projectId;
    
    @NotBlank(message = "任务名称不能为空")
    @Size(max = 200, message = "任务名称长度不能超过200个字符")
    private String name;
    
    @Size(max = 1000, message = "任务描述长度不能超过1000个字符")
    private String description;
    
    @NotBlank(message = "任务状态不能为空")
    private String status;
    
    @NotBlank(message = "优先级不能为空")
    private String priority;
    
    private Long assigneeId;
    private Long reporterId;
    private LocalDate startDate;
    private LocalDate dueDate;
    private BigDecimal estimatedHours;
    private BigDecimal actualHours;
    private Integer progress;

    // 构造函数
    public TaskRequest() {}

    // Getter和Setter方法
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

    @Override
    public String toString() {
        return "TaskRequest{" +
                "projectId=" + projectId +
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
                '}';
    }
} 