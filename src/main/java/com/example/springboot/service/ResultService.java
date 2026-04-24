package com.example.springboot.service;

import com.example.springboot.entity.Result;
import java.util.List;

public interface ResultService {
    
    // 插入成绩记录
    boolean insertResult(Result result);
    
    // 更新成绩记录
    boolean updateResult(Result result);
    
    // 删除成绩记录
    boolean deleteResult(Long id);
    
    // 根据ID查询成绩记录
    Result getResultById(Long id);
    
    // 查询所有成绩记录
    List<Result> getAllResults();
    
    // 根据赛程ID查询成绩记录
    List<Result> getResultsByScheduleId(Long scheduleId);
    
    // 根据用户ID查询成绩记录
    List<Result> getResultsByUserId(Long userId);
    
    // 根据裁判ID查询成绩记录
    List<Result> getResultsByRefereeId(Long refereeId);
    
    // 根据状态查询成绩记录
    List<Result> getResultsByStatus(String status);
    
    // 查询裁判可以录入成绩的赛程
    List<Result> getAvailableSchedulesForReferee(Long refereeId);
    
    // 查询某个赛程的所有参赛者
    List<Result> getParticipantsByScheduleId(Long scheduleId);
    
    // 批量录入成绩
    boolean batchInsertResults(List<Result> results);
    
    // 更新成绩状态
    boolean updateResultStatus(Long id, String status);
    
    // 更新排名
    boolean updateResultRank(Long id, String rank);

    // 用户提交成绩疑问/申诉
    boolean submitAppeal(Long resultId, Long userId, String reason, String evidence);

    // 裁判处理申诉
    boolean handleAppeal(Long resultId, Long refereeId, String handleResult, String handleComment);
    
    // 确认成绩
    boolean confirmResult(Long id);
    
    // 计算并更新排名
    boolean calculateAndUpdateRanks(Long scheduleId);
    
    // 验证成绩录入权限
    boolean validateResultEntryPermission(Long refereeId, Long scheduleId);
    
    // 验证成绩数据
    boolean validateResultData(Result result);
    
    // 获取已完成但未录入成绩的赛程
    List<Result> getCompletedSchedulesWithoutResults(Long refereeId);
    
    // 申请为已完成比赛录入成绩
    boolean applyForResultEntry(Result application);
    
    // 获取裁判申请录入的赛程
    List<Result> getAppliedSchedules(Long refereeId);
} 