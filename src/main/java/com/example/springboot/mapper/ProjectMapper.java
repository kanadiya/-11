package com.example.springboot.mapper;

import com.example.springboot.entity.Project;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper {
    
    // 插入项目
    int insert(Project project);
    
    // 根据ID删除项目
    int deleteById(Long id);
    
    // 查询所有项目
    List<Project> selectAll();
} 