package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.RefereeTask;
import com.example.springboot.service.RefereeTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
@CrossOrigin(origins = "*")
public class RefereeTaskController {

    @Autowired
    private RefereeTaskService refereeTaskService;

    // 获取所有任务
    @GetMapping("/list")
    public Result<List<RefereeTask>> getAllTasks() {
        try {
            List<RefereeTask> tasks = refereeTaskService.getAllTasks();
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取任务列表失败: " + e.getMessage());
        }
    }

    // 根据裁判ID获取任务
    @GetMapping("/referee/{refereeId}")
    public Result<List<RefereeTask>> getTasksByRefereeId(@PathVariable Long refereeId) {
        try {
            List<RefereeTask> tasks = refereeTaskService.getTasksByRefereeId(refereeId);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取裁判任务失败: " + e.getMessage());
        }
    }

    // 根据赛程ID获取任务
    @GetMapping("/schedule/{scheduleId}")
    public Result<List<RefereeTask>> getTasksByScheduleId(@PathVariable Long scheduleId) {
        try {
            List<RefereeTask> tasks = refereeTaskService.getTasksByScheduleId(scheduleId);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("获取赛程任务失败: " + e.getMessage());
        }
    }

    // 根据ID获取任务详情
    @GetMapping("/{id}")
    public Result<RefereeTask> getTaskById(@PathVariable Long id) {
        try {
            RefereeTask task = refereeTaskService.getTaskById(id);
            if (task == null) {
                return Result.error("任务不存在");
            }
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("获取任务详情失败: " + e.getMessage());
        }
    }

    // 创建任务
    @PostMapping("/create")
    public Result<RefereeTask> createTask(@RequestBody RefereeTask refereeTask) {
        try {
            RefereeTask createdTask = refereeTaskService.createTask(refereeTask);
            return Result.success(createdTask);
        } catch (Exception e) {
            return Result.error("创建任务失败: " + e.getMessage());
        }
    }

    // 更新任务
    @PutMapping("/update/{id}")
    public Result<RefereeTask> updateTask(@PathVariable Long id, @RequestBody RefereeTask refereeTask) {
        try {
            refereeTask.setId(id);
            RefereeTask updatedTask = refereeTaskService.updateTask(refereeTask);
            return Result.success(updatedTask);
        } catch (Exception e) {
            return Result.error("更新任务失败: " + e.getMessage());
        }
    }

    // 删除任务
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteTask(@PathVariable Long id) {
        try {
            boolean success = refereeTaskService.deleteTask(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除任务失败: " + e.getMessage());
        }
    }

    // 分配裁判到任务
    @PostMapping("/{taskId}/assign-referee")
    public Result<RefereeTask> assignRefereeToTask(@PathVariable Long taskId, @RequestBody AssignRefereeRequest request) {
        try {
            RefereeTask task = refereeTaskService.assignRefereeToTask(taskId, request.getRefereeId());
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("分配裁判失败: " + e.getMessage());
        }
    }

    // 申请担任裁判（根据赛程ID）
    @PostMapping("/apply-referee")
    public Result<RefereeTask> applyReferee(@RequestBody ApplyRefereeRequest request) {
        try {
            RefereeTask task = refereeTaskService.createTaskByScheduleAndReferee(
                request.getScheduleId(), 
                request.getRefereeId(), 
                request.getReason()
            );
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("申请担任裁判失败: " + e.getMessage());
        }
    }

    // 裁判接受任务
    @PostMapping("/{taskId}/accept")
    public Result<RefereeTask> acceptTask(@PathVariable Long taskId) {
        try {
            RefereeTask task = refereeTaskService.acceptTask(taskId);
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("接受任务失败: " + e.getMessage());
        }
    }

    // 裁判拒绝任务
    @PostMapping("/{taskId}/reject")
    public Result<RefereeTask> rejectTask(@PathVariable Long taskId) {
        try {
            RefereeTask task = refereeTaskService.rejectTask(taskId);
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("拒绝任务失败: " + e.getMessage());
        }
    }

    // 开始任务
    @PostMapping("/{taskId}/start")
    public Result<RefereeTask> startTask(@PathVariable Long taskId) {
        try {
            RefereeTask task = refereeTaskService.startTask(taskId);
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("开始任务失败: " + e.getMessage());
        }
    }

    // 完成任务
    @PostMapping("/{taskId}/complete")
    public Result<RefereeTask> completeTask(@PathVariable Long taskId) {
        try {
            RefereeTask task = refereeTaskService.completeTask(taskId);
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("完成任务失败: " + e.getMessage());
        }
    }

    // 获取可用的裁判列表
    @GetMapping("/available-referees")
    public Result<List<Object>> getAvailableReferees() {
        try {
            List<Object> referees = refereeTaskService.getAvailableReferees();
            return Result.success(referees);
        } catch (Exception e) {
            return Result.error("获取可用裁判失败: " + e.getMessage());
        }
    }

    // 内部类用于接收分配裁判的请求
    public static class AssignRefereeRequest {
        private Long refereeId;

        public Long getRefereeId() {
            return refereeId;
        }

        public void setRefereeId(Long refereeId) {
            this.refereeId = refereeId;
        }
    }

    // 内部类用于接收申请裁判的请求
    public static class ApplyRefereeRequest {
        private Long scheduleId;
        private Long refereeId;
        private String reason;

        public Long getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(Long scheduleId) {
            this.scheduleId = scheduleId;
        }

        public Long getRefereeId() {
            return refereeId;
        }

        public void setRefereeId(Long refereeId) {
            this.refereeId = refereeId;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
} 