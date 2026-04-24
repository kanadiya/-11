package com.example.springboot.service;

import com.example.springboot.dto.ChangePasswordRequest;
import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.RegisterRequest;
import com.example.springboot.entity.User;

import java.util.List;

public interface UserService {
    
    /**
     * 用户注册
     */
    String register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    String login(LoginRequest request);
    
    /**
     * 根据ID查询用户
     */
    User getUserById(Long id);
    
    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);
    
    /**
     * 查询所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 更新用户信息
     */
    boolean updateUser(User user);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Long id);
    
    /**
     * 修改密码
     */
    boolean changePassword(Long userId, ChangePasswordRequest request);
    
    /**
     * 搜索用户
     */
    List<User> searchUsers(String username, String realName, String role, Integer status);
    
    /**
     * 批量删除用户
     */
    int batchDeleteUsers(List<Long> ids);
    
    /**
     * 重置用户密码
     */
    boolean resetUserPassword(Long id);
} 