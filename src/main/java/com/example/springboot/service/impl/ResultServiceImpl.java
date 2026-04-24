package com.example.springboot.service.impl;

import com.example.springboot.entity.Result;
import com.example.springboot.entity.RefereeTask;
import com.example.springboot.mapper.RefereeTaskMapper;
import com.example.springboot.mapper.ResultMapper;
import com.example.springboot.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ResultServiceImpl implements ResultService {

    @Autowired
    private ResultMapper resultMapper;
    
    @Autowired
    private RefereeTaskMapper refereeTaskMapper;

    @Override
    @Transactional
    public boolean insertResult(Result result) {
        if (!validateResultData(result)) {
            throw new RuntimeException("成绩数据不完整或格式错误");
        }
        ensureUnitConsistency(result.getScheduleId(), result.getUnit(), null);
        
        // 设置录入时间
        result.setEntryTime(LocalDateTime.now());
        result.setStatus("PENDING");
        
        return resultMapper.insert(result) > 0;
    }

    @Override
    @Transactional
    public boolean updateResult(Result result) {
        if (!validateResultData(result)) {
            throw new RuntimeException("成绩数据不完整或格式错误");
        }
        ensureUnitConsistency(result.getScheduleId(), result.getUnit(), result.getId());
        
        return resultMapper.updateById(result) > 0;
    }

    @Override
    @Transactional
    public boolean deleteResult(Long id) {
        return resultMapper.deleteById(id) > 0;
    }

    @Override
    public Result getResultById(Long id) {
        return resultMapper.selectById(id);
    }

    @Override
    public List<Result> getAllResults() {
        return resultMapper.selectAll();
    }

    @Override
    public List<Result> getResultsByScheduleId(Long scheduleId) {
        return resultMapper.selectByScheduleId(scheduleId);
    }

    @Override
    public List<Result> getResultsByUserId(Long userId) {
        return resultMapper.selectByUserId(userId);
    }

    @Override
    public List<Result> getResultsByRefereeId(Long refereeId) {
        return resultMapper.selectByRefereeId(refereeId);
    }

    @Override
    public List<Result> getResultsByStatus(String status) {
        return resultMapper.selectByStatus(status);
    }

    @Override
    public List<Result> getAvailableSchedulesForReferee(Long refereeId) {
        return resultMapper.selectAvailableSchedulesForReferee(refereeId);
    }

    @Override
    public List<Result> getParticipantsByScheduleId(Long scheduleId) {
        return resultMapper.selectParticipantsByScheduleId(scheduleId);
    }

    @Override
    @Transactional
    public boolean batchInsertResults(List<Result> results) {
        if (results == null || results.isEmpty()) {
            return false;
        }
        Long scheduleId = results.get(0).getScheduleId();
        String batchUnit = results.get(0).getUnit();
        
        // 验证所有成绩数据
        for (Result result : results) {
            if (!validateResultData(result)) {
                throw new RuntimeException("存在无效成绩数据，请检查后重试");
            }
            if (!scheduleId.equals(result.getScheduleId())) {
                throw new RuntimeException("批量录入必须是同一个赛程");
            }
            if (!batchUnit.equals(result.getUnit())) {
                throw new RuntimeException("同一项目(赛程)批量录入时单位必须一致");
            }
            result.setEntryTime(LocalDateTime.now());
            result.setStatus("PENDING");
        }
        ensureUnitConsistency(scheduleId, batchUnit, null);
        
        return resultMapper.batchInsert(results) > 0;
    }

    @Override
    @Transactional
    public boolean updateResultStatus(Long id, String status) {
        return resultMapper.updateStatus(id, status) > 0;
    }

    @Override
    @Transactional
    public boolean updateResultRank(Long id, String rank) {
        return resultMapper.updateRank(id, rank) > 0;
    }

    @Override
    @Transactional
    public boolean submitAppeal(Long resultId, Long userId, String reason, String evidence) {
        Result result = resultMapper.selectById(resultId);
        if (result == null) {
            throw new RuntimeException("成绩记录不存在");
        }
        if (!userId.equals(result.getUserId())) {
            throw new RuntimeException("只能对自己的成绩发起申诉");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("申诉理由不能为空");
        }
        String safeReason = reason.trim();
        if (safeReason.length() > 300) {
            safeReason = safeReason.substring(0, 300);
        }
        String safeEvidence = evidence == null ? "" : evidence.trim();
        if (safeEvidence.length() > 600) {
            safeEvidence = safeEvidence.substring(0, 600);
        }
        String remark = "【申诉理由】" + safeReason;
        if (!safeEvidence.isEmpty()) {
            remark += "\n【申诉证据】" + safeEvidence;
        }
        resultMapper.updateRemark(resultId, remark);
        return resultMapper.updateStatus(resultId, "DISPUTED") > 0;
    }

    @Override
    @Transactional
    public boolean handleAppeal(Long resultId, Long refereeId, String handleResult, String handleComment) {
        Result result = resultMapper.selectById(resultId);
        if (result == null) {
            throw new RuntimeException("申诉对应成绩不存在");
        }
        if (!validateResultEntryPermission(refereeId, result.getScheduleId())) {
            throw new RuntimeException("没有权限处理该成绩申诉");
        }
        if (handleResult == null || handleResult.trim().isEmpty()) {
            throw new RuntimeException("处理结果不能为空");
        }
        if (handleComment == null || handleComment.trim().isEmpty()) {
            throw new RuntimeException("处理意见不能为空");
        }

        String targetStatus;
        if ("申诉不成立".equals(handleResult)) {
            targetStatus = "CONFIRMED";
        } else if ("需要进一步调查".equals(handleResult)) {
            targetStatus = "PENDING";
        } else {
            // 申诉成立：保留 DISPUTED 状态，等待后续复核或重新录入
            targetStatus = "DISPUTED";
        }

        String oldRemark = result.getRemark() == null ? "" : result.getRemark();
        String mergedRemark = oldRemark
                + "\n【裁判处理】" + handleResult.trim()
                + "\n【处理意见】" + handleComment.trim()
                + "\n【处理裁判ID】" + refereeId
                + "\n【处理时间】" + LocalDateTime.now();

        if (mergedRemark.length() > 900) {
            mergedRemark = mergedRemark.substring(mergedRemark.length() - 900);
        }

        resultMapper.updateRemark(resultId, mergedRemark);
        return resultMapper.updateStatus(resultId, targetStatus) > 0;
    }

    @Override
    @Transactional
    public boolean confirmResult(Long id) {
        return resultMapper.confirmResult(id, LocalDateTime.now().toString()) > 0;
    }

    @Override
    @Transactional
    public boolean calculateAndUpdateRanks(Long scheduleId) {
        // 获取该赛程的所有成绩记录，按成绩排序
        List<Result> results = resultMapper.selectResultsByScheduleIdOrderByScore(scheduleId);
        
        if (results.isEmpty()) {
            return false;
        }
        
        // 计算排名
        for (int i = 0; i < results.size(); i++) {
            Result result = results.get(i);
            String rank = String.valueOf(i + 1);
            resultMapper.updateRank(result.getId(), rank);
        }
        
        return true;
    }

    @Override
    public boolean validateResultEntryPermission(Long refereeId, Long scheduleId) {
        if (refereeId == null || scheduleId == null) {
            return false;
        }
        // 1) 有裁判任务绑定则直接按任务授权
        RefereeTask task = refereeTaskMapper.selectByScheduleIdAndRefereeId(scheduleId, refereeId);
        if (task != null && task.getStatus() != null) {
            Set<String> allowedStatuses = new HashSet<>(Arrays.asList("PENDING", "ASSIGNED", "ONGOING", "FINISHED", "COMPLETED"));
            if (allowedStatuses.contains(task.getStatus().toUpperCase())) {
                return true;
            }
        }

        // 2) 已经录入过该赛程成绩的裁判，允许继续录入剩余参赛者
        List<Result> existingBySchedule = resultMapper.selectByScheduleId(scheduleId);
        for (Result existing : existingBySchedule) {
            if (refereeId.equals(existing.getRefereeId())) {
                return true;
            }
        }

        // 3) 兜底：在“可录入赛程”查询结果中出现的赛程也允许录入
        List<Result> availableSchedules = resultMapper.selectAvailableSchedulesForReferee(refereeId);
        return availableSchedules.stream().anyMatch(s -> scheduleId.equals(s.getScheduleId()));
    }

    @Override
    public boolean validateResultData(Result result) {
        if (result == null) {
            return false;
        }
        
        // 验证必填字段
        if (result.getScheduleId() == null || result.getUserId() == null || 
            result.getRefereeId() == null || result.getScore() == null || 
            result.getUnit() == null || result.getUnit().trim().isEmpty()) {
            return false;
        }
        
        // 验证成绩数值
        if (result.getScore().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        // 验证单位
        String[] validUnits = {"秒", "米", "厘米", "分", "个", "次"};
        boolean validUnit = false;
        for (String unit : validUnits) {
            if (unit.equals(result.getUnit())) {
                validUnit = true;
                break;
            }
        }
        if (!validUnit) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public List<Result> getCompletedSchedulesWithoutResults(Long refereeId) {
        return resultMapper.selectCompletedSchedulesWithoutResults(refereeId);
    }
    
    @Override
    public boolean applyForResultEntry(Result application) {
        // 这里可以实现申请逻辑，暂时返回true
        return true;
    }
    
    @Override
    public List<Result> getAppliedSchedules(Long refereeId) {
        return resultMapper.selectAppliedSchedules(refereeId);
    }

    private void ensureUnitConsistency(Long scheduleId, String unit, Long currentResultId) {
        if (scheduleId == null || unit == null || unit.trim().isEmpty()) {
            return;
        }
        List<Result> existingResults = resultMapper.selectByScheduleId(scheduleId);
        String normalizedUnit = unit.trim();
        for (Result existing : existingResults) {
            if (currentResultId != null && currentResultId.equals(existing.getId())) {
                continue;
            }
            if (existing.getUnit() == null || existing.getUnit().trim().isEmpty()) {
                continue;
            }
            String existingUnit = existing.getUnit().trim();
            if (!existingUnit.equals(normalizedUnit)) {
                throw new RuntimeException("同一项目(赛程)成绩单位必须一致，当前赛程单位应为: " + existingUnit);
            }
        }
    }
} 