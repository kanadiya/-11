package com.example.springboot.mapper;

import com.example.springboot.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMemberMapper {
    
    // 插入项目成员
    int insert(ProjectMember projectMember);
    
    // 根据ID更新项目成员
    int updateById(ProjectMember projectMember);
    
    // 根据ID删除项目成员
    int deleteById(Long id);
    
    // 根据项目ID和用户ID删除成员
    int deleteByProjectAndUser(Long projectId, Long userId);
    
    // 根据ID查询项目成员
    ProjectMember selectById(Long id);
    
    // 根据项目ID查询所有成员
    List<ProjectMember> selectByProjectId(Long projectId);
    
    // 根据用户ID查询参与的项目
    List<ProjectMember> selectByUserId(Long userId);
    
    // 检查用户是否已在项目中
    ProjectMember selectByProjectAndUser(Long projectId, Long userId);
    
    // 统计项目成员数量
    int countByProjectId(Long projectId);
} 