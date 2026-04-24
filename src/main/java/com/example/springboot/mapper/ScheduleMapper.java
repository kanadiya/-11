package com.example.springboot.mapper;

import com.example.springboot.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ScheduleMapper {
    
    // 插入赛程
    int insert(Schedule schedule);
    
    // 根据ID删除赛程
    int deleteById(Long id);
    
    // 更新赛程
    int update(Schedule schedule);

    // 仅更新赛程状态
    int updateStatusById(Long id, String status);
    
    // 根据ID查询赛程
    Schedule selectById(Long id);
    
    // 查询所有赛程（包含项目名称）
    List<Schedule> selectAll();
    
    // 根据项目ID查询赛程
    List<Schedule> selectByProjectId(Long projectId);
    
    // 获取未开始的比赛
    List<Schedule> selectUpcomingMatches();
} 