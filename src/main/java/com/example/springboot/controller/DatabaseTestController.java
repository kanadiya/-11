package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class DatabaseTestController {

    @Autowired
    private ResultService resultService;

    // 测试获取所有成绩
    @GetMapping("/results")
    public Result<List<com.example.springboot.entity.Result>> testGetAllResults() {
        try {
            List<com.example.springboot.entity.Result> results = resultService.getAllResults();
            return Result.success(results);
        } catch (Exception e) {
            return Result.error("测试获取成绩失败: " + e.getMessage());
        }
    }

    // 测试获取裁判可用赛程
    @GetMapping("/available-schedules/{refereeId}")
    public Result<List<com.example.springboot.entity.Result>> testGetAvailableSchedules(@PathVariable Long refereeId) {
        try {
            List<com.example.springboot.entity.Result> schedules = resultService.getAvailableSchedulesForReferee(refereeId);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.error("测试获取可用赛程失败: " + e.getMessage());
        }
    }

    // 测试获取参赛者
    @GetMapping("/participants/{scheduleId}")
    public Result<List<com.example.springboot.entity.Result>> testGetParticipants(@PathVariable Long scheduleId) {
        try {
            List<com.example.springboot.entity.Result> participants = resultService.getParticipantsByScheduleId(scheduleId);
            return Result.success(participants);
        } catch (Exception e) {
            return Result.error("测试获取参赛者失败: " + e.getMessage());
        }
    }

    // 测试录入成绩
    @PostMapping("/insert-result")
    public Result<String> testInsertResult() {
        try {
            com.example.springboot.entity.Result result = new com.example.springboot.entity.Result();
            result.setScheduleId(1L);
            result.setUserId(4L);
            result.setRefereeId(2L);
            result.setScore(new BigDecimal("10.500"));
            result.setUnit("秒");
            result.setRemark("测试成绩录入");
            
            if (resultService.insertResult(result)) {
                return Result.success("测试成绩录入成功");
            } else {
                return Result.error("测试成绩录入失败");
            }
        } catch (Exception e) {
            return Result.error("测试成绩录入失败: " + e.getMessage());
        }
    }

    // 测试计算排名
    @PutMapping("/calculate-ranks/{scheduleId}")
    public Result<String> testCalculateRanks(@PathVariable Long scheduleId) {
        try {
            if (resultService.calculateAndUpdateRanks(scheduleId)) {
                return Result.success("测试排名计算成功");
            } else {
                return Result.error("测试排名计算失败");
            }
        } catch (Exception e) {
            return Result.error("测试排名计算失败: " + e.getMessage());
        }
    }
} 