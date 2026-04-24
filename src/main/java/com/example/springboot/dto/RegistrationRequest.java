package com.example.springboot.dto;

public class RegistrationRequest {
    private Long userId;
    private Long scheduleId;

    public RegistrationRequest() {}

    public RegistrationRequest(Long userId, Long scheduleId) {
        this.userId = userId;
        this.scheduleId = scheduleId;
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

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "userId=" + userId +
                ", scheduleId=" + scheduleId +
                '}';
    }
} 