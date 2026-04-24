package com.example.springboot.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Result {
    private Long id;
    private Long scheduleId;      // 关联赛程ID
    private Long userId;          // 运动员用户ID
    private Long refereeId;       // 录入成绩的裁判ID
    private BigDecimal score;     // 成绩数值
    private String unit;          // 成绩单位（秒、米、厘米、分等）
    private String rank;          // 排名
    private String status;        // 状态：PENDING-待确认, CONFIRMED-已确认, DISPUTED-有争议
    private String remark;        // 备注
    private LocalDateTime entryTime;    // 录入时间
    private LocalDateTime confirmTime;  // 确认时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联查询字段
    private String projectName;   // 项目名称
    private String username;      // 运动员用户名
    private String realName;      // 运动员真实姓名
    private String refereeName;   // 裁判姓名
    private String venue;         // 比赛场地
    private LocalDateTime scheduleDate; // 比赛日期
    private String scheduleTime;  // 比赛时间
    private String scheduleStatus; // 赛程状态

    // 构造函数
    public Result() {}

    public Result(Long scheduleId, Long userId, Long refereeId, BigDecimal score, String unit) {
        this.scheduleId = scheduleId;
        this.userId = userId;
        this.refereeId = refereeId;
        this.score = score;
        this.unit = unit;
        this.status = "PENDING";
        this.entryTime = LocalDateTime.now();
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRefereeId() {
        return refereeId;
    }

    public void setRefereeId(Long refereeId) {
        this.refereeId = refereeId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(LocalDateTime confirmTime) {
        this.confirmTime = confirmTime;
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

    public LocalDateTime getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDateTime scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", scheduleId=" + scheduleId +
                ", userId=" + userId +
                ", refereeId=" + refereeId +
                ", score=" + score +
                ", unit='" + unit + '\'' +
                ", rank='" + rank + '\'' +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                ", entryTime=" + entryTime +
                ", confirmTime=" + confirmTime +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", projectName='" + projectName + '\'' +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", refereeName='" + refereeName + '\'' +
                ", venue='" + venue + '\'' +
                ", scheduleDate=" + scheduleDate +
                ", scheduleTime='" + scheduleTime + '\'' +
                ", scheduleStatus='" + scheduleStatus + '\'' +
                '}';
    }
} 