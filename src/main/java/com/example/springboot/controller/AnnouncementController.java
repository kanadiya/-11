package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.dto.AnnouncementRequest;
import com.example.springboot.entity.Announcement;
import com.example.springboot.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/announcement")
@CrossOrigin(origins = "*")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    /**
     * 获取所有公告（管理员）
     */
    @GetMapping("/list")
    public Result<List<Announcement>> getAllAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        return Result.success(announcements);
    }
    
    /**
     * 根据状态获取公告
     */
    @GetMapping("/status/{status}")
    public Result<List<Announcement>> getAnnouncementsByStatus(@PathVariable String status) {
        List<Announcement> announcements = announcementService.getAnnouncementsByStatus(status);
        return Result.success(announcements);
    }
    
    /**
     * 根据ID获取公告
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        return Result.success(announcement);
    }
    
    /**
     * 创建公告（管理员）
     */
    @PostMapping("/create")
    public Result<Announcement> createAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        Announcement announcement = announcementService.createAnnouncement(request);
        return Result.success(announcement);
    }
    
    /**
     * 更新公告（管理员）
     */
    @PutMapping("/update/{id}")
    public Result<Announcement> updateAnnouncement(@PathVariable Long id, @Valid @RequestBody AnnouncementRequest request) {
        Announcement announcement = announcementService.updateAnnouncement(id, request);
        return Result.success(announcement);
    }
    
    /**
     * 删除公告（管理员）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return Result.success();
    }
    
    /**
     * 获取已发布的公告（用于首页显示）
     */
    @GetMapping("/published")
    public Result<List<Announcement>> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementService.getPublishedAnnouncements();
        return Result.success(announcements);
    }
} 