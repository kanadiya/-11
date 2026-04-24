package com.example.springboot.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class AnnouncementRequest {
    
    private Long id;
    
    @NotBlank(message = "公告标题不能为空")
    private String title;
    
    @NotBlank(message = "公告内容不能为空")
    private String content;
    
    @NotBlank(message = "发布人不能为空")
    private String author;
    
    @NotBlank(message = "状态不能为空")
    private String status;
} 