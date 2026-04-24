package com.example.springboot.service.impl;

import com.example.springboot.entity.Project;
import com.example.springboot.mapper.ProjectMapper;
import com.example.springboot.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    @Transactional
    public Project createProject(Project project) {
        project.setCreateTime(LocalDateTime.now());
        projectMapper.insert(project);
        return project;
    }

    @Override
    @Transactional
    public boolean deleteProject(Long id) {
        return projectMapper.deleteById(id) > 0;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectMapper.selectAll();
    }
} 