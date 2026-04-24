package com.example.springboot.service;

import com.example.springboot.entity.RefereeTask;

import java.util.List;

public interface RefereeTaskService {
    
    // 创建裁判任务
    RefereeTask createTask(RefereeTask refereeTask);
    
    // 根据ID查询裁判任务
    RefereeTask getTaskById(Long id);
    
    // 根据裁判ID查询任务列表
    List<RefereeTask> getTasksByRefereeId(Long refereeId);
    
    // 根据赛程ID查询任务列表
    List<RefereeTask> getTasksByScheduleId(Long scheduleId);
    
    // 查询所有裁判任务
    List<RefereeTask> getAllTasks();
    
    // 根据状态查询任务列表
    List<RefereeTask> getTasksByStatus(String status);
    
    // 更新裁判任务
    RefereeTask updateTask(RefereeTask refereeTask);
    
    // 删除裁判任务
    boolean deleteTask(Long id);
    
    // 分配裁判到任务
    RefereeTask assignRefereeToTask(Long taskId, Long refereeId);
    
    // 根据赛程ID和裁判ID创建任务
    RefereeTask createTaskByScheduleAndReferee(Long scheduleId, Long refereeId, String reason);
    
    // 裁判接受任务
    RefereeTask acceptTask(Long taskId);
    
    // 裁判拒绝任务
    RefereeTask rejectTask(Long taskId);
    
    // 开始任务
    RefereeTask startTask(Long taskId);
    
    // 完成任务
    RefereeTask completeTask(Long taskId);
    
    // 根据赛程ID和裁判ID查询任务
    RefereeTask getTaskByScheduleIdAndRefereeId(Long scheduleId, Long refereeId);
    
    // 统计某个赛程的裁判数量
    int countRefereesByScheduleId(Long scheduleId);
    
    // 统计某个裁判的任务数量
    int countTasksByRefereeId(Long refereeId);
    
    // 获取可用的裁判列表
    List<Object> getAvailableReferees();
} 