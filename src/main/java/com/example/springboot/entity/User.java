package com.example.springboot.entity;

import java.util.Date;

public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private String gender;
    private String studentId;
    private Integer age;
    private String college;
    private String major;
    private String className;
    private String role;
    private Object status; // 兼容Integer和String类型
    private Date createTime;
    private Date updateTime;

    // getter和setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Object getStatus() { return status; }
    public void setStatus(Object status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
} 