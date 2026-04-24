package com.example.springboot.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Schedule {
    private Long id;
    private Long projectId;
    private String projectName; // 关联查询项目名称
    private LocalDate date;
    private LocalTime time;
    private String venue;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 构造函数
    public Schedule() {}

    public Schedule(Long id, Long projectId, LocalDate date, LocalTime time, String venue, String status) {
        this.id = id;
        this.projectId = projectId;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.status = status;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", venue='" + venue + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
} 