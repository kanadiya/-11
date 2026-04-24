package com.example.springboot.mapper;

import com.example.springboot.entity.RefereeTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RefereeTaskMapper {
    
    // 插入裁判任务
    int insert(RefereeTask refereeTask);
    
    // 根据ID查询裁判任务
    RefereeTask selectById(@Param("id") Long id);
    
    // 根据裁判ID查询任务列表
    List<RefereeTask> selectByRefereeId(@Param("refereeId") Long refereeId);
    
    // 根据赛程ID查询任务列表
    List<RefereeTask> selectByScheduleId(@Param("scheduleId") Long scheduleId);
    
    // 查询所有裁判任务
    List<RefereeTask> selectAll();
    
    // 根据状态查询任务列表
    List<RefereeTask> selectByStatus(@Param("status") String status);
    
    // 更新裁判任务
    int update(RefereeTask refereeTask);
    
    // 删除裁判任务
    int deleteById(@Param("id") Long id);
    
    // 根据赛程ID和裁判ID查询任务
    RefereeTask selectByScheduleIdAndRefereeId(@Param("scheduleId") Long scheduleId, @Param("refereeId") Long refereeId);
    
    // 统计某个赛程的裁判数量
    int countByScheduleId(@Param("scheduleId") Long scheduleId);
    
    // 统计某个裁判的任务数量
    int countByRefereeId(@Param("refereeId") Long refereeId);
    
    // 查询可用的裁判列表
    List<Map<String, Object>> selectAvailableReferees();
} 