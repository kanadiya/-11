package com.example.springboot.service;

import com.example.springboot.dto.AnnouncementRequest;
import com.example.springboot.entity.Announcement;

import java.util.List;

public interface AnnouncementService {
    
    /**
     * 获取所有公告
     */
    List<Announcement> getAllAnnouncements();
    
    /**
     * 根据状态获取公告
     */
    List<Announcement> getAnnouncementsByStatus(String status);
    
    /**
     * 根据ID获取公告
     */
    Announcement getAnnouncementById(Long id);
    
    /**
     * 创建公告
     */
    Announcement createAnnouncement(AnnouncementRequest request);
    
    /**
     * 更新公告
     */
    Announcement updateAnnouncement(Long id, AnnouncementRequest request);
    
    /**
     * 删除公告
     */
    void deleteAnnouncement(Long id);
    
    /**
     * 获取已发布的公告（用于首页显示）
     */
    List<Announcement> getPublishedAnnouncements();
} 