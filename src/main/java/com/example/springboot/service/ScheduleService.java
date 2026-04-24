package com.example.springboot.service;

import com.example.springboot.entity.Schedule;
import java.util.List;

public interface ScheduleService {
    
    // 添加赛程
    boolean addSchedule(Schedule schedule);
    
    // 删除赛程
    boolean deleteSchedule(Long id);
    
    // 更新赛程
    boolean updateSchedule(Schedule schedule);
    
    // 根据ID查询赛程
    Schedule getScheduleById(Long id);
    
    // 查询所有赛程
    List<Schedule> getAllSchedules();
    
    // 根据项目ID查询赛程
    List<Schedule> getSchedulesByProjectId(Long projectId);
    
    // 获取未开始的比赛
    List<Schedule> getUpcomingMatches();

    // 获取进行中的比赛
    List<Schedule> getOngoingMatches();

    // 获取已完成的比赛
    List<Schedule> getCompletedMatches();
} 