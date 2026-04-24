package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Project;
import com.example.springboot.entity.Schedule;
import com.example.springboot.service.ProjectService;
import com.example.springboot.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "*")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private ProjectService projectService;

    // 获取所有赛程
    @GetMapping("/list")
    public Result<List<Schedule>> getAllSchedules() {
        try {
            List<Schedule> schedules = scheduleService.getAllSchedules();
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.error("获取赛程列表失败: " + e.getMessage());
        }
    }
    
    // 获取未开始的比赛（用于首页显示）
    @GetMapping("/upcoming")
    public Result<List<Schedule>> getUpcomingMatches() {
        try {
            List<Schedule> matches = scheduleService.getUpcomingMatches();
            return Result.success(matches);
        } catch (Exception e) {
            return Result.error("获取未开始比赛失败: " + e.getMessage());
        }
    }

    // 获取进行中的比赛
    @GetMapping("/ongoing")
    public Result<List<Schedule>> getOngoingMatches() {
        try {
            List<Schedule> matches = scheduleService.getOngoingMatches();
            return Result.success(matches);
        } catch (Exception e) {
            return Result.error("获取进行中比赛失败: " + e.getMessage());
        }
    }

    // 获取已完成的比赛
    @GetMapping("/completed")
    public Result<List<Schedule>> getCompletedMatches() {
        try {
            List<Schedule> matches = scheduleService.getCompletedMatches();
            return Result.success(matches);
        } catch (Exception e) {
            return Result.error("获取已完成比赛失败: " + e.getMessage());
        }
    }

    // 根据项目ID获取赛程
    @GetMapping("/project/{projectId}")
    public Result<List<Schedule>> getSchedulesByProjectId(@PathVariable Long projectId) {
        try {
            List<Schedule> schedules = scheduleService.getSchedulesByProjectId(projectId);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.error("获取项目赛程失败: " + e.getMessage());
        }
    }

    // 根据ID获取赛程详情
    @GetMapping("/{id:\\d+}")
    public Result<Schedule> getScheduleById(@PathVariable Long id) {
        try {
            Schedule schedule = scheduleService.getScheduleById(id);
            if (schedule != null) {
                return Result.success(schedule);
            } else {
                return Result.error("赛程不存在");
            }
        } catch (Exception e) {
            return Result.error("获取赛程详情失败: " + e.getMessage());
        }
    }

    // 添加赛程
    @PostMapping("/add")
    public Result<String> addSchedule(@RequestBody Schedule schedule) {
        try {
            if (schedule.getProjectId() == null) {
                return Result.error("项目ID不能为空");
            }
            if (schedule.getDate() == null) {
                return Result.error("比赛日期不能为空");
            }
            if (schedule.getTime() == null) {
                return Result.error("比赛时间不能为空");
            }
            if (schedule.getVenue() == null || schedule.getVenue().trim().isEmpty()) {
                return Result.error("比赛场地不能为空");
            }
            if (schedule.getStatus() == null || schedule.getStatus().trim().isEmpty()) {
                schedule.setStatus("未开始");
            }

            boolean success = scheduleService.addSchedule(schedule);
            if (success) {
                return Result.success("添加赛程成功");
            } else {
                return Result.error("添加赛程失败");
            }
        } catch (Exception e) {
            return Result.error("添加赛程失败: " + e.getMessage());
        }
    }

    // 更新赛程
    @PutMapping("/update")
    public Result<String> updateSchedule(@RequestBody Schedule schedule) {
        try {
            if (schedule.getId() == null) {
                return Result.error("赛程ID不能为空");
            }
            if (schedule.getProjectId() == null) {
                return Result.error("项目ID不能为空");
            }
            if (schedule.getDate() == null) {
                return Result.error("比赛日期不能为空");
            }
            if (schedule.getTime() == null) {
                return Result.error("比赛时间不能为空");
            }
            if (schedule.getVenue() == null || schedule.getVenue().trim().isEmpty()) {
                return Result.error("比赛场地不能为空");
            }
            if (schedule.getStatus() == null || schedule.getStatus().trim().isEmpty()) {
                schedule.setStatus("未开始");
            }

            boolean success = scheduleService.updateSchedule(schedule);
            if (success) {
                return Result.success("更新赛程成功");
            } else {
                return Result.error("更新赛程失败");
            }
        } catch (Exception e) {
            return Result.error("更新赛程失败: " + e.getMessage());
        }
    }

    // 删除赛程
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteSchedule(@PathVariable Long id) {
        try {
            boolean success = scheduleService.deleteSchedule(id);
            if (success) {
                return Result.success("删除赛程成功");
            } else {
                return Result.error("删除赛程失败");
            }
        } catch (Exception e) {
            return Result.error("删除赛程失败: " + e.getMessage());
        }
    }

    // 获取所有项目（用于下拉选择）
    @GetMapping("/projects")
    public Result<List<Project>> getAllProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
    }
} 