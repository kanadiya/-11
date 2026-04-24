package com.example.springboot.mapper;

import com.example.springboot.entity.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper {
    
    // 插入任务
    int insert(Task task);
    
    // 根据ID更新任务
    int updateById(Task task);
    
    // 根据ID删除任务
    int deleteById(Long id);
    
    // 根据项目ID删除所有任务
    int deleteByProjectId(Long projectId);
    
    // 根据ID查询任务
    Task selectById(Long id);
    
    // 根据项目ID查询所有任务
    List<Task> selectByProjectId(Long projectId);
    
    // 根据负责人ID查询任务
    List<Task> selectByAssigneeId(Long assigneeId);
    
    // 根据条件查询任务
    List<Task> selectByCondition(Task task);
    
    // 统计项目任务数量
    int countByProjectId(Long projectId);
    
    // 统计各状态任务数量
    List<Task> countByStatus(Long projectId);
} 