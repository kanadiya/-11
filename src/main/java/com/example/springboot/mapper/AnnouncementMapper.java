package com.example.springboot.mapper;

import com.example.springboot.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    
    /**
     * 获取所有公告
     */
    List<Announcement> selectAll();
    
    /**
     * 根据状态获取公告
     */
    List<Announcement> selectByStatus(@Param("status") String status);
    
    /**
     * 根据ID获取公告
     */
    Announcement selectById(@Param("id") Long id);
    
    /**
     * 插入公告
     */
    int insert(Announcement announcement);
    
    /**
     * 更新公告
     */
    int update(Announcement announcement);
    
    /**
     * 删除公告
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 获取已发布的公告（用于首页显示）
     */
    List<Announcement> selectPublished();
} 