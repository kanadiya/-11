package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.ResultService;
import com.example.springboot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "*")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private JwtUtil jwtUtil;

    // 获取所有成绩记录
    @GetMapping
    public Result<List<com.example.springboot.entity.Result>> getAllResults() {
        try {
            List<com.example.springboot.entity.Result> results = resultService.getAllResults();
            return Result.success(results);
        } catch (Exception e) {
            return Result.error("获取成绩记录失败: " + e.getMessage());
        }
    }

    // 根据ID获取成绩记录
    @GetMapping("/{id}")
    public Result<com.example.springboot.entity.Result> getResultById(@PathVariable Long id) {
        try {
            com.example.springboot.entity.Result result = resultService.getResultById(id);
            if (result != null) {
                return Result.success(result);
            } else {
                return Result.error("成绩记录不存在");
            }
        } catch (Exception e) {
            return Result.error("获取成绩记录失败: " + e.getMessage());
        }
    }

    // 根据赛程ID获取成绩记录
    @GetMapping("/schedule/{scheduleId}")
    public Result<List<com.example.springboot.entity.Result>> getResultsByScheduleId(@PathVariable Long scheduleId) {
        try {
            List<com.example.springboot.entity.Result> results = resultService.getResultsByScheduleId(scheduleId);
            return Result.success(results);
        } catch (Exception e) {
            return Result.error("获取赛程成绩失败: " + e.getMessage());
        }
    }

    // 根据用户ID获取成绩记录
    @GetMapping("/user/{userId}")
    public Result<List<com.example.springboot.entity.Result>> getResultsByUserId(@PathVariable Long userId) {
        try {
            List<com.example.springboot.entity.Result> results = resultService.getResultsByUserId(userId);
            return Result.success(results);
        } catch (Exception e) {
            return Result.error("获取用户成绩失败: " + e.getMessage());
        }
    }

    // 根据裁判ID获取成绩记录
    @GetMapping("/referee/{refereeId}")
    public Result<List<com.example.springboot.entity.Result>> getResultsByRefereeId(@PathVariable Long refereeId) {
        try {
            List<com.example.springboot.entity.Result> results = resultService.getResultsByRefereeId(refereeId);
            return Result.success(results);
        } catch (Exception e) {
            return Result.error("获取裁判成绩记录失败: " + e.getMessage());
        }
    }

    // 根据状态获取成绩记录
    @GetMapping("/status/{status}")
    public Result<List<com.example.springboot.entity.Result>> getResultsByStatus(@PathVariable String status) {
        try {
            List<com.example.springboot.entity.Result> results = resultService.getResultsByStatus(status);
            return Result.success(results);
        } catch (Exception e) {
            return Result.error("获取成绩记录失败: " + e.getMessage());
        }
    }

    // 获取裁判可以录入成绩的赛程
    @GetMapping("/available-schedules/{refereeId}")
    public Result<List<com.example.springboot.entity.Result>> getAvailableSchedulesForReferee(@PathVariable Long refereeId) {
        try {
            List<com.example.springboot.entity.Result> schedules = resultService.getAvailableSchedulesForReferee(refereeId);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.error("获取可用赛程失败: " + e.getMessage());
        }
    }

    // 获取某个赛程的所有参赛者
    @GetMapping("/participants/{scheduleId}")
    public Result<List<com.example.springboot.entity.Result>> getParticipantsByScheduleId(@PathVariable Long scheduleId) {
        try {
            List<com.example.springboot.entity.Result> participants = resultService.getParticipantsByScheduleId(scheduleId);
            return Result.success(participants);
        } catch (Exception e) {
            return Result.error("获取参赛者失败: " + e.getMessage());
        }
    }

    // 录入成绩
    @PostMapping
    public Result<String> insertResult(@RequestBody com.example.springboot.entity.Result result, HttpServletRequest request) {
        try {
            // 从JWT中获取裁判ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long refereeId = jwtUtil.getUserIdFromToken(token);
                result.setRefereeId(refereeId);
            }

            // 验证权限
            if (!resultService.validateResultEntryPermission(result.getRefereeId(), result.getScheduleId())) {
                return Result.error("没有权限录入该赛程的成绩");
            }

            if (resultService.insertResult(result)) {
                return Result.success("成绩录入成功");
            } else {
                return Result.error("成绩录入失败");
            }
        } catch (Exception e) {
            return Result.error("成绩录入失败: " + e.getMessage());
        }
    }

    // 批量录入成绩
    @PostMapping("/batch")
    public Result<String> batchInsertResults(@RequestBody List<com.example.springboot.entity.Result> results, HttpServletRequest request) {
        try {
            if (results == null || results.isEmpty()) {
                return Result.error("成绩列表不能为空");
            }
            // 从JWT中获取裁判ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                Long refereeId = jwtUtil.getUserIdFromToken(token);
                
                // 为所有成绩记录设置裁判ID
                for (com.example.springboot.entity.Result result : results) {
                    result.setRefereeId(refereeId);
                }
                // 验证批量录入权限（同一赛程）
                Long scheduleId = results.get(0).getScheduleId();
                if (!resultService.validateResultEntryPermission(refereeId, scheduleId)) {
                    return Result.error("没有权限录入该赛程的成绩");
                }
            }

            if (resultService.batchInsertResults(results)) {
                return Result.success("批量成绩录入成功");
            } else {
                return Result.error("批量成绩录入失败");
            }
        } catch (Exception e) {
            return Result.error("批量成绩录入失败: " + e.getMessage());
        }
    }

    // 更新成绩记录
    @PutMapping("/{id}")
    public Result<String> updateResult(@PathVariable Long id, @RequestBody com.example.springboot.entity.Result result, HttpServletRequest request) {
        try {
            com.example.springboot.entity.Result existing = resultService.getResultById(id);
            if (existing == null) {
                return Result.error("成绩记录不存在");
            }

            // 从JWT中获取裁判ID
            String token = request.getHeader("Authorization");
            Long refereeId = null;
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                refereeId = jwtUtil.getUserIdFromToken(token);
            }
            if (refereeId == null) {
                return Result.error("未获取到裁判身份，请重新登录");
            }

            // 验证修改权限（该裁判对该赛程有录入权限）
            if (!resultService.validateResultEntryPermission(refereeId, existing.getScheduleId())) {
                return Result.error("没有权限修改该赛程成绩");
            }

            // 合并更新字段，避免前端只传部分字段导致非空字段被置空
            existing.setRefereeId(refereeId);
            if (result.getScheduleId() != null) {
                existing.setScheduleId(result.getScheduleId());
            }
            if (result.getUserId() != null) {
                existing.setUserId(result.getUserId());
            }
            if (result.getScore() != null) {
                existing.setScore(result.getScore());
            }
            if (result.getUnit() != null && !result.getUnit().trim().isEmpty()) {
                existing.setUnit(result.getUnit().trim());
            }
            // 备注允许清空
            existing.setRemark(result.getRemark());

            if (resultService.updateResult(existing)) {
                return Result.success("成绩更新成功");
            } else {
                return Result.error("成绩更新失败");
            }
        } catch (Exception e) {
            return Result.error("成绩更新失败: " + e.getMessage());
        }
    }

    // 删除成绩记录
    @DeleteMapping("/{id}")
    public Result<String> deleteResult(@PathVariable Long id) {
        try {
            if (resultService.deleteResult(id)) {
                return Result.success("成绩删除成功");
            } else {
                return Result.error("成绩删除失败");
            }
        } catch (Exception e) {
            return Result.error("成绩删除失败: " + e.getMessage());
        }
    }

    // 更新成绩状态
    @PutMapping("/{id}/status")
    public Result<String> updateResultStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            if (resultService.updateResultStatus(id, status)) {
                return Result.success("状态更新成功");
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("状态更新失败: " + e.getMessage());
        }
    }

    // 更新排名
    @PutMapping("/{id}/rank")
    public Result<String> updateResultRank(@PathVariable Long id, @RequestParam String rank) {
        try {
            if (resultService.updateResultRank(id, rank)) {
                return Result.success("排名更新成功");
            } else {
                return Result.error("排名更新失败");
            }
        } catch (Exception e) {
            return Result.error("排名更新失败: " + e.getMessage());
        }
    }

    // 确认成绩
    @PutMapping("/{id}/confirm")
    public Result<String> confirmResult(@PathVariable Long id) {
        try {
            if (resultService.confirmResult(id)) {
                return Result.success("成绩确认成功");
            } else {
                return Result.error("成绩确认失败");
            }
        } catch (Exception e) {
            return Result.error("成绩确认失败: " + e.getMessage());
        }
    }

    // 获取已完成但未录入成绩的赛程
    @GetMapping("/completed-schedules/{refereeId}")
    public Result<List<com.example.springboot.entity.Result>> getCompletedSchedulesWithoutResults(@PathVariable Long refereeId) {
        try {
            List<com.example.springboot.entity.Result> schedules = resultService.getCompletedSchedulesWithoutResults(refereeId);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.error("获取已完成赛程失败: " + e.getMessage());
        }
    }

    // 申请为已完成比赛录入成绩
    @PostMapping("/apply-entry")
    public Result<String> applyForResultEntry(@RequestBody com.example.springboot.entity.Result application) {
        try {
            if (resultService.applyForResultEntry(application)) {
                return Result.success("申请提交成功");
            } else {
                return Result.error("申请提交失败");
            }
        } catch (Exception e) {
            return Result.error("申请提交失败: " + e.getMessage());
        }
    }

    // 获取裁判申请录入的赛程
    @GetMapping("/applied-schedules/{refereeId}")
    public Result<List<com.example.springboot.entity.Result>> getAppliedSchedules(@PathVariable Long refereeId) {
        try {
            List<com.example.springboot.entity.Result> schedules = resultService.getAppliedSchedules(refereeId);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.error("获取申请赛程失败: " + e.getMessage());
        }
    }

        // 计算并更新排名
    @PutMapping("/schedule/{scheduleId}/calculate-ranks")
    public Result<String> calculateAndUpdateRanks(@PathVariable Long scheduleId) {
        try {
            if (resultService.calculateAndUpdateRanks(scheduleId)) {
                return Result.success("排名计算成功");
            } else {
                return Result.error("排名计算失败");
            }
        } catch (Exception e) {
            return Result.error("排名计算失败: " + e.getMessage());
        }
    }

    // 用户提交成绩疑问/申诉
    @PostMapping("/{id}/appeal")
    public Result<String> submitAppeal(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body,
            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            String reason = body.get("reason");
            String evidence = body.get("evidence");
            boolean ok = resultService.submitAppeal(id, userId, reason, evidence);
            return ok ? Result.success("申诉提交成功") : Result.error("申诉提交失败");
        } catch (Exception e) {
            return Result.error("申诉提交失败: " + e.getMessage());
        }
    }

    // 裁判处理申诉
    @PutMapping("/{id}/appeal/handle")
    public Result<String> handleAppeal(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body,
            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }
            Long refereeId = jwtUtil.getUserIdFromToken(token);
            String handleResult = body.get("result");
            String handleComment = body.get("comment");
            boolean ok = resultService.handleAppeal(id, refereeId, handleResult, handleComment);
            return ok ? Result.success("申诉处理成功") : Result.error("申诉处理失败");
        } catch (Exception e) {
            return Result.error("申诉处理失败: " + e.getMessage());
        }
    }
} 