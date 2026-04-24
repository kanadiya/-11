package com.example.springboot.service.impl;

import com.example.springboot.entity.Registration;
import com.example.springboot.entity.Schedule;
import com.example.springboot.mapper.RegistrationMapper;
import com.example.springboot.mapper.ScheduleMapper;
import com.example.springboot.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationMapper registrationMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    @Transactional
    public boolean registerForMatch(Long userId, Long scheduleId) {
        try {
            // 检查赛程是否存在
            Schedule schedule = scheduleMapper.selectById(scheduleId);
            if (schedule == null) {
                return false;
            }

            // 检查用户是否已经报名
            Registration existingRegistration = registrationMapper.selectRegistrationByUserIdAndScheduleId(userId, scheduleId);
            if (existingRegistration != null) {
                return false; // 已经报名过了
            }

            // 创建报名记录
            Registration registration = new Registration(userId, scheduleId);
            registration.setUpdateTime(LocalDateTime.now());
            
            int result = registrationMapper.insertRegistration(registration);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean cancelRegistration(Long userId, Long scheduleId) {
        try {
            int result = registrationMapper.deleteRegistrationByUserIdAndScheduleId(userId, scheduleId);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Registration getRegistrationById(Long id) {
        try {
            return registrationMapper.selectRegistrationById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Registration> getRegistrationsByUserId(Long userId) {
        try {
            return registrationMapper.selectRegistrationsByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Registration> getRegistrationsByScheduleId(Long scheduleId) {
        try {
            return registrationMapper.selectRegistrationsByScheduleId(scheduleId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isUserRegistered(Long userId, Long scheduleId) {
        try {
            Registration registration = registrationMapper.selectRegistrationByUserIdAndScheduleId(userId, scheduleId);
            return registration != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Registration> getAllRegistrations() {
        try {
            return registrationMapper.selectAllRegistrations();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public boolean updateRegistrationStatus(Long id, String status) {
        try {
            Registration registration = new Registration();
            registration.setId(id);
            registration.setStatus(status);
            registration.setUpdateTime(LocalDateTime.now());
            
            int result = registrationMapper.updateRegistrationStatus(registration);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteRegistration(Long id) {
        try {
            int result = registrationMapper.deleteRegistration(id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
} 