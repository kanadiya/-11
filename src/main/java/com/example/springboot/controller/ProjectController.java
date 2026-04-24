package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Project;
import com.example.springboot.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // 创建项目
    @PostMapping("/create")
    public Result<Project> createProject(@RequestBody Project project) {
        try {
            Project createdProject = projectService.createProject(project);
            return Result.success(createdProject);
        } catch (Exception e) {
            return Result.error("创建项目失败: " + e.getMessage());
        }
    }

    // 删除项目
    @DeleteMapping("/{id}")
    public Result<String> deleteProject(@PathVariable Long id) {
        try {
            boolean success = projectService.deleteProject(id);
            if (success) {
                return Result.success("项目删除成功");
            } else {
                return Result.error("项目删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除项目失败: " + e.getMessage());
        }
    }

    // 获取所有项目
    @GetMapping("/list")
    public Result<List<Project>> getAllProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            return Result.success(projects);
        } catch (Exception e) {
            return Result.error("获取项目列表失败: " + e.getMessage());
        }
    }
} 