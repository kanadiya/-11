package com.example.springboot.service;

import com.example.springboot.entity.Project;
import java.util.List;

public interface ProjectService {
    
    // 创建项目
    Project createProject(Project project);
    
    // 删除项目
    boolean deleteProject(Long id);
    
    // 获取所有项目
    List<Project> getAllProjects();
} 