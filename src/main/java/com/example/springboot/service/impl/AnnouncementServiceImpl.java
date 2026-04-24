package com.example.springboot.service.impl;

import com.example.springboot.dto.AnnouncementRequest;
import com.example.springboot.entity.Announcement;
import com.example.springboot.mapper.AnnouncementMapper;
import com.example.springboot.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {
    
    @Autowired
    private AnnouncementMapper announcementMapper;
    
    @Override
    public List<Announcement> getAllAnnouncements() {
        return announcementMapper.selectAll();
    }
    
    @Override
    public List<Announcement> getAnnouncementsByStatus(String status) {
        return announcementMapper.selectByStatus(status);
    }
    
    @Override
    public Announcement getAnnouncementById(Long id) {
        return announcementMapper.selectById(id);
    }
    
    @Override
    public Announcement createAnnouncement(AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setAuthor(request.getAuthor());
        announcement.setStatus(request.getStatus());
        
        // 如果状态是已发布，设置发布时间
        if ("已发布".equals(request.getStatus())) {
            announcement.setPublishTime(LocalDateTime.now());
        }
        
        announcementMapper.insert(announcement);
        return announcement;
    }
    
    @Override
    public Announcement updateAnnouncement(Long id, AnnouncementRequest request) {
        Announcement existingAnnouncement = announcementMapper.selectById(id);
        if (existingAnnouncement == null) {
            throw new RuntimeException("公告不存在");
        }
        
        existingAnnouncement.setTitle(request.getTitle());
        existingAnnouncement.setContent(request.getContent());
        existingAnnouncement.setAuthor(request.getAuthor());
        
        // 如果状态从草稿变为已发布，设置发布时间
        if ("已发布".equals(request.getStatus()) && !"已发布".equals(existingAnnouncement.getStatus())) {
            existingAnnouncement.setPublishTime(LocalDateTime.now());
        }
        
        existingAnnouncement.setStatus(request.getStatus());
        
        announcementMapper.update(existingAnnouncement);
        return existingAnnouncement;
    }
    
    @Override
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        announcementMapper.deleteById(id);
    }
    
    @Override
    public List<Announcement> getPublishedAnnouncements() {
        return announcementMapper.selectPublished();
    }
} 