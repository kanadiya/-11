package com.example.springboot.service;

import com.example.springboot.entity.Registration;

import java.util.List;

public interface RegistrationService {
    
    // 用户报名比赛
    boolean registerForMatch(Long userId, Long scheduleId);
    
    // 取消报名
    boolean cancelRegistration(Long userId, Long scheduleId);
    
    // 根据ID查询报名记录
    Registration getRegistrationById(Long id);
    
    // 根据用户ID查询报名记录
    List<Registration> getRegistrationsByUserId(Long userId);
    
    // 根据赛程ID查询报名记录
    List<Registration> getRegistrationsByScheduleId(Long scheduleId);
    
    // 查询用户是否已报名某个赛程
    boolean isUserRegistered(Long userId, Long scheduleId);
    
    // 查询所有报名记录
    List<Registration> getAllRegistrations();
    
    // 更新报名状态
    boolean updateRegistrationStatus(Long id, String status);
    
    // 删除报名记录
    boolean deleteRegistration(Long id);
} 