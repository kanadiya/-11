package com.example.springboot.service.impl;

import com.example.springboot.entity.Schedule;
import com.example.springboot.mapper.ScheduleMapper;
import com.example.springboot.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public boolean addSchedule(Schedule schedule) {
        return scheduleMapper.insert(schedule) > 0;
    }

    @Override
    public boolean deleteSchedule(Long id) {
        return scheduleMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateSchedule(Schedule schedule) {
        return scheduleMapper.update(schedule) > 0;
    }

    @Override
    public Schedule getScheduleById(Long id) {
        Schedule schedule = scheduleMapper.selectById(id);
        return applyAndPersistStatus(schedule);
    }

    @Override
    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = scheduleMapper.selectAll();
        return schedules.stream()
                .map(this::applyAndPersistStatus)
                .collect(Collectors.toList());
    }

    @Override
    public List<Schedule> getSchedulesByProjectId(Long projectId) {
        List<Schedule> schedules = scheduleMapper.selectByProjectId(projectId);
        return schedules.stream()
                .map(this::applyAndPersistStatus)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Schedule> getUpcomingMatches() {
        // 基于日期规则实时计算，避免旧状态导致数据错误
        List<Schedule> schedules = scheduleMapper.selectAll();
        return schedules.stream()
                .map(this::applyAndPersistStatus)
                .filter(s -> "未开始".equals(s.getStatus()))
                .sorted(Comparator.comparing(Schedule::getDate).thenComparing(Schedule::getTime))
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<Schedule> getOngoingMatches() {
        List<Schedule> schedules = scheduleMapper.selectAll();
        return schedules.stream()
                .map(this::applyAndPersistStatus)
                .filter(s -> "进行中".equals(s.getStatus()))
                .sorted(Comparator.comparing(Schedule::getDate).thenComparing(Schedule::getTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<Schedule> getCompletedMatches() {
        List<Schedule> schedules = scheduleMapper.selectAll();
        return schedules.stream()
                .map(this::applyAndPersistStatus)
                .filter(s -> "已结束".equals(s.getStatus()))
                .sorted(Comparator.comparing(Schedule::getDate).thenComparing(Schedule::getTime))
                .collect(Collectors.toList());
    }

    private Schedule applyAndPersistStatus(Schedule schedule) {
        if (schedule == null || schedule.getDate() == null) {
            return schedule;
        }

        String newStatus = calculateStatusByDate(schedule.getDate());
        if (!newStatus.equals(schedule.getStatus())) {
            schedule.setStatus(newStatus);
            scheduleMapper.updateStatusById(schedule.getId(), newStatus);
        }
        return schedule;
    }

    // 规则：比赛当天=进行中；未到日期=未开始；已过日期=已结束
    private String calculateStatusByDate(LocalDate matchDate) {
        LocalDate today = LocalDate.now();
        if (matchDate.isAfter(today)) {
            return "未开始";
        }
        if (matchDate.isEqual(today)) {
            return "进行中";
        }
        return "已结束";
    }
} 