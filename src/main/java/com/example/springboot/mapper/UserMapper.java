package com.example.springboot.mapper;

import com.example.springboot.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    
    /**
     * 插入用户
     */
    int insert(User user);
    
    /**
     * 根据ID更新用户
     */
    int updateById(User user);
    
    /**
     * 根据ID删除用户
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 根据ID查询用户
     */
    User selectById(@Param("id") Long id);
    
    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);
    
    /**
     * 根据手机号查询用户
     */
    User selectByPhone(@Param("phone") String phone);
    
    /**
     * 根据邮箱查询用户
     */
    User selectByEmail(@Param("email") String email);
    
    /**
     * 查询所有用户
     */
    List<User> selectAll();
    
    /**
     * 根据条件查询用户
     */
    List<User> selectByCondition(User user);
} 