package com.example.springboot.mapper;

import com.example.springboot.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RegistrationMapper {
    
    // 添加报名记录
    int insertRegistration(Registration registration);
    
    // 根据ID查询报名记录
    Registration selectRegistrationById(Long id);
    
    // 根据用户ID查询报名记录
    List<Registration> selectRegistrationsByUserId(Long userId);
    
    // 根据赛程ID查询报名记录
    List<Registration> selectRegistrationsByScheduleId(Long scheduleId);
    
    // 查询用户是否已报名某个赛程
    Registration selectRegistrationByUserIdAndScheduleId(@Param("userId") Long userId, @Param("scheduleId") Long scheduleId);
    
    // 查询所有报名记录（带关联信息）
    List<Registration> selectAllRegistrations();
    
    // 更新报名状态
    int updateRegistrationStatus(Registration registration);
    
    // 删除报名记录
    int deleteRegistration(Long id);
    
    // 根据用户ID和赛程ID删除报名记录
    int deleteRegistrationByUserIdAndScheduleId(@Param("userId") Long userId, @Param("scheduleId") Long scheduleId);
} 