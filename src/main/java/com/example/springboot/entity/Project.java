package com.example.springboot.entity;

import java.time.LocalDateTime;

public class Project {
    private Long id;
    private String name;
    private LocalDateTime createTime;

    // 构造函数
    public Project() {}

    public Project(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                '}';
    }
} 