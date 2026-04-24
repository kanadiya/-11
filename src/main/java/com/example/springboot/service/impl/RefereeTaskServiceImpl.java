package com.example.springboot.service.impl;

import com.example.springboot.entity.RefereeTask;
import com.example.springboot.mapper.RefereeTaskMapper;
import com.example.springboot.service.RefereeTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class RefereeTaskServiceImpl implements RefereeTaskService {

    @Autowired
    private RefereeTaskMapper refereeTaskMapper;

    @Override
    @Transactional
    public RefereeTask createTask(RefereeTask refereeTask) {
        refereeTask.setCreateTime(LocalDateTime.now());
        refereeTask.setUpdateTime(LocalDateTime.now());
        refereeTaskMapper.insert(refereeTask);
        return refereeTask;
    }

    @Override
    public RefereeTask getTaskById(Long id) {
        return refereeTaskMapper.selectById(id);
    }

    @Override
    public List<RefereeTask> getTasksByRefereeId(Long refereeId) {
        return refereeTaskMapper.selectByRefereeId(refereeId);
    }

    @Override
    public List<RefereeTask> getTasksByScheduleId(Long scheduleId) {
        return refereeTaskMapper.selectByScheduleId(scheduleId);
    }

    @Override
    public List<RefereeTask> getAllTasks() {
        return refereeTaskMapper.selectAll();
    }

    @Override
    public List<RefereeTask> getTasksByStatus(String status) {
        return refereeTaskMapper.selectByStatus(status);
    }

    @Override
    @Transactional
    public RefereeTask updateTask(RefereeTask refereeTask) {
        refereeTask.setUpdateTime(LocalDateTime.now());
        refereeTaskMapper.update(refereeTask);
        return refereeTaskMapper.selectById(refereeTask.getId());
    }

    @Override
    @Transactional
    public boolean deleteTask(Long id) {
        return refereeTaskMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public RefereeTask assignRefereeToTask(Long taskId, Long refereeId) {
        RefereeTask task = refereeTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        task.setRefereeId(refereeId);
        task.setStatus("ASSIGNED");
        task.setAssignTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        
        refereeTaskMapper.update(task);
        return refereeTaskMapper.selectById(taskId);
    }

    // 新增方法：根据赛程ID和裁判ID创建任务
    @Override
    @Transactional
    public RefereeTask createTaskByScheduleAndReferee(Long scheduleId, Long refereeId, String reason) {
        // 检查是否已经存在该赛程和裁判的任务
        RefereeTask existingTask = refereeTaskMapper.selectByScheduleIdAndRefereeId(scheduleId, refereeId);
        if (existingTask != null) {
            throw new RuntimeException("该裁判已经申请过此赛程");
        }
        
        RefereeTask task = new RefereeTask();
        task.setScheduleId(scheduleId);
        task.setRefereeId(refereeId);
        task.setStatus("PENDING");
        task.setReason(reason);
        task.setApplyTime(LocalDateTime.now());
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        
        refereeTaskMapper.insert(task);
        return refereeTaskMapper.selectById(task.getId());
    }

    @Override
    @Transactional
    public RefereeTask acceptTask(Long taskId) {
        RefereeTask task = refereeTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        task.setStatus("ASSIGNED");
        task.setUpdateTime(LocalDateTime.now());
        
        refereeTaskMapper.update(task);
        return refereeTaskMapper.selectById(taskId);
    }

    @Override
    @Transactional
    public RefereeTask rejectTask(Long taskId) {
        RefereeTask task = refereeTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        task.setStatus("PENDING");
        task.setRefereeId(null);
        task.setUpdateTime(LocalDateTime.now());
        
        refereeTaskMapper.update(task);
        return refereeTaskMapper.selectById(taskId);
    }

    @Override
    @Transactional
    public RefereeTask startTask(Long taskId) {
        RefereeTask task = refereeTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        if (!"ASSIGNED".equals(task.getStatus())) {
            throw new RuntimeException("任务状态不正确，无法开始");
        }
        
        task.setStatus("ONGOING");
        task.setStartTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        
        refereeTaskMapper.update(task);
        return refereeTaskMapper.selectById(taskId);
    }

    @Override
    @Transactional
    public RefereeTask completeTask(Long taskId) {
        RefereeTask task = refereeTaskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        if (!"ONGOING".equals(task.getStatus())) {
            throw new RuntimeException("任务状态不正确，无法完成");
        }
        
        task.setStatus("FINISHED");
        task.setCompleteTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        
        refereeTaskMapper.update(task);
        return refereeTaskMapper.selectById(taskId);
    }

    @Override
    public RefereeTask getTaskByScheduleIdAndRefereeId(Long scheduleId, Long refereeId) {
        return refereeTaskMapper.selectByScheduleIdAndRefereeId(scheduleId, refereeId);
    }

    @Override
    public int countRefereesByScheduleId(Long scheduleId) {
        return refereeTaskMapper.countByScheduleId(scheduleId);
    }

    @Override
    public int countTasksByRefereeId(Long refereeId) {
        return refereeTaskMapper.countByRefereeId(refereeId);
    }

    @Override
    public List<Object> getAvailableReferees() {
        List<Object> availableReferees = new ArrayList<>();
        List<Map<String, Object>> allReferees = refereeTaskMapper.selectAvailableReferees();
        for (Map<String, Object> referee : allReferees) {
            Map<String, Object> availableReferee = new HashMap<>();
            availableReferee.put("id", referee.get("id"));
            availableReferee.put("realName", referee.get("real_name"));
            availableReferee.put("username", referee.get("username"));
            availableReferee.put("phone", referee.get("phone"));
            availableReferee.put("email", referee.get("email"));
            availableReferee.put("status", referee.get("status"));
            availableReferees.add(availableReferee);
        }
        return availableReferees;
    }
} 