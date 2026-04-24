package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.dto.RegistrationRequest;
import com.example.springboot.entity.Registration;
import com.example.springboot.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // 用户报名比赛
    @PostMapping("/register")
    public Result<String> registerForMatch(@RequestBody RegistrationRequest request) {
        try {
            if (request.getUserId() == null) {
                return Result.error("用户ID不能为空");
            }
            if (request.getScheduleId() == null) {
                return Result.error("赛程ID不能为空");
            }

            boolean success = registrationService.registerForMatch(request.getUserId(), request.getScheduleId());
            if (success) {
                return Result.success("报名成功");
            } else {
                return Result.error("报名失败，可能已经报名过或赛程不存在");
            }
        } catch (Exception e) {
            return Result.error("报名失败: " + e.getMessage());
        }
    }

    // 取消报名
    @PostMapping("/cancel")
    public Result<String> cancelRegistration(@RequestBody RegistrationRequest request) {
        try {
            if (request.getUserId() == null) {
                return Result.error("用户ID不能为空");
            }
            if (request.getScheduleId() == null) {
                return Result.error("赛程ID不能为空");
            }

            // 查找报名记录
            List<Registration> registrations = registrationService.getRegistrationsByUserId(request.getUserId());
            Registration targetRegistration = null;
            for (Registration reg : registrations) {
                if (reg.getScheduleId().equals(request.getScheduleId())) {
                    targetRegistration = reg;
                    break;
                }
            }
            
            if (targetRegistration == null) {
                return Result.error("未找到报名记录");
            }

            // 更新状态为已取消
            boolean success = registrationService.updateRegistrationStatus(targetRegistration.getId(), "CANCELLED");
            if (success) {
                return Result.success("取消报名成功");
            } else {
                return Result.error("取消报名失败");
            }
        } catch (Exception e) {
            return Result.error("取消报名失败: " + e.getMessage());
        }
    }

    // 查询用户是否已报名某个赛程
    @GetMapping("/check/{userId}/{scheduleId}")
    public Result<Boolean> isUserRegistered(@PathVariable Long userId, @PathVariable Long scheduleId) {
        try {
            boolean isRegistered = registrationService.isUserRegistered(userId, scheduleId);
            return Result.success(isRegistered);
        } catch (Exception e) {
            return Result.error("查询报名状态失败: " + e.getMessage());
        }
    }

    // 根据用户ID查询报名记录
    @GetMapping("/user/{userId}")
    public Result<List<Registration>> getRegistrationsByUserId(@PathVariable Long userId) {
        try {
            List<Registration> registrations = registrationService.getRegistrationsByUserId(userId);
            return Result.success(registrations);
        } catch (Exception e) {
            return Result.error("获取用户报名记录失败: " + e.getMessage());
        }
    }

    // 根据赛程ID查询报名记录
    @GetMapping("/schedule/{scheduleId}")
    public Result<List<Registration>> getRegistrationsByScheduleId(@PathVariable Long scheduleId) {
        try {
            List<Registration> registrations = registrationService.getRegistrationsByScheduleId(scheduleId);
            return Result.success(registrations);
        } catch (Exception e) {
            return Result.error("获取赛程报名记录失败: " + e.getMessage());
        }
    }

    // 查询所有报名记录（管理员功能）
    @GetMapping("/all")
    public Result<List<Registration>> getAllRegistrations() {
        try {
            List<Registration> registrations = registrationService.getAllRegistrations();
            return Result.success(registrations);
        } catch (Exception e) {
            return Result.error("获取所有报名记录失败: " + e.getMessage());
        }
    }

    // 更新报名状态（管理员功能）
    @PutMapping("/status/{id}")
    public Result<String> updateRegistrationStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            boolean success = registrationService.updateRegistrationStatus(id, status);
            if (success) {
                return Result.success("更新报名状态成功");
            } else {
                return Result.error("更新报名状态失败");
            }
        } catch (Exception e) {
            return Result.error("更新报名状态失败: " + e.getMessage());
        }
    }

    // 删除报名记录（管理员功能）
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteRegistration(@PathVariable Long id) {
        try {
            boolean success = registrationService.deleteRegistration(id);
            if (success) {
                return Result.success("删除报名记录成功");
            } else {
                return Result.error("删除报名记录失败");
            }
        } catch (Exception e) {
            return Result.error("删除报名记录失败: " + e.getMessage());
        }
    }
} 