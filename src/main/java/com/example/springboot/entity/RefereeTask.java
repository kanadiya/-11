package com.example.springboot.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RefereeTask {
    private Long id;
    private Long scheduleId;  // 关联赛程ID
    private Long refereeId;   // 裁判ID
    private String status;    // 任务状态：PENDING-待分配, ASSIGNED-已分配, ONGOING-进行中, FINISHED-已完成
    private String reason;    // 申请理由
    private LocalDateTime applyTime;  // 申请时间
    private LocalDateTime assignTime; // 分配时间
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime completeTime; // 完成时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private String projectName;
    private String refereeName;
    private String venue;
    private LocalDate date;
    private String time;
    private Integer participantCount;
    private String scheduleStatus;

    // 构造函数
    public RefereeTask() {}

    public RefereeTask(Long scheduleId, Long refereeId, String status) {
        this.scheduleId = scheduleId;
        this.refereeId = refereeId;
        this.status = status;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getRefereeId() {
        return refereeId;
    }

    public void setRefereeId(Long refereeId) {
        this.refereeId = refereeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(LocalDateTime applyTime) {
        this.applyTime = applyTime;
    }

    public LocalDateTime getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(LocalDateTime assignTime) {
        this.assignTime = assignTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
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

    public String getRefereeName() {
        return refereeName;
    }

    public void setRefereeName(String refereeName) {
        this.refereeName = refereeName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

    public String getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    @Override
    public String toString() {
        return "RefereeTask{" +
                "id=" + id +
                ", scheduleId=" + scheduleId +
                ", refereeId=" + refereeId +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                ", applyTime=" + applyTime +
                ", assignTime=" + assignTime +
                ", startTime=" + startTime +
                ", completeTime=" + completeTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", projectName='" + projectName + '\'' +
                ", refereeName='" + refereeName + '\'' +
                ", venue='" + venue + '\'' +
                ", date=" + date +
                ", time='" + time + '\'' +
                ", participantCount=" + participantCount +
                ", scheduleStatus='" + scheduleStatus + '\'' +
                '}';
    }
} 