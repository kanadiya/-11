package com.example.springboot.mapper;

import com.example.springboot.entity.Result;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResultMapper {
    
    // 插入成绩记录
    int insert(Result result);
    
    // 根据ID更新成绩记录
    int updateById(Result result);
    
    // 根据ID删除成绩记录
    int deleteById(Long id);
    
    // 根据ID查询成绩记录
    Result selectById(Long id);
    
    // 查询所有成绩记录
    List<Result> selectAll();
    
    // 根据赛程ID查询成绩记录
    List<Result> selectByScheduleId(Long scheduleId);
    
    // 根据用户ID查询成绩记录
    List<Result> selectByUserId(Long userId);
    
    // 根据裁判ID查询成绩记录
    List<Result> selectByRefereeId(Long refereeId);
    
    // 根据状态查询成绩记录
    List<Result> selectByStatus(String status);
    
    // 查询裁判可以录入成绩的赛程（已完成且裁判已分配）
    List<Result> selectAvailableSchedulesForReferee(Long refereeId);
    
    // 查询某个赛程的所有参赛者（已报名且通过审核）
    List<Result> selectParticipantsByScheduleId(Long scheduleId);
    
    // 查询某个赛程的所有成绩记录（按成绩排序）
    List<Result> selectResultsByScheduleIdOrderByScore(Long scheduleId);
    
    // 批量插入成绩记录
    int batchInsert(List<Result> results);
    
    // 更新成绩状态
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // 更新排名
    int updateRank(@Param("id") Long id, @Param("rank") String rank);

    // 更新申诉备注
    int updateRemark(@Param("id") Long id, @Param("remark") String remark);
    
    // 确认成绩
    int confirmResult(@Param("id") Long id, @Param("confirmTime") String confirmTime);
    
    // 查询已完成但未录入成绩的赛程
    List<Result> selectCompletedSchedulesWithoutResults(Long refereeId);
    
    // 查询裁判申请录入的赛程
    List<Result> selectAppliedSchedules(Long refereeId);
} 