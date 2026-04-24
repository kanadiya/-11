package com.example.springboot.service.impl;

import com.example.springboot.dto.ChangePasswordRequest;
import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.RegisterRequest;
import com.example.springboot.entity.User;
import com.example.springboot.mapper.UserMapper;
import com.example.springboot.service.UserService;
import com.example.springboot.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String register(RegisterRequest request) {
        System.out.println("开始注册用户: " + request.getUsername());
        
        // 验证密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            System.out.println("密码确认失败: " + request.getUsername());
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        System.out.println("检查用户名是否存在: " + request.getUsername());
        User existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            System.out.println("用户名已存在: " + request.getUsername());
            throw new RuntimeException("用户名已存在，请选择其他用户名");
        }

        // 检查手机号是否已存在
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            System.out.println("检查手机号是否存在: " + request.getPhone());
            existingUser = userMapper.selectByPhone(request.getPhone().trim());
            if (existingUser != null) {
                System.out.println("手机号已被注册: " + request.getPhone());
                throw new RuntimeException("手机号已被注册，请使用其他手机号");
            }
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            System.out.println("检查邮箱是否存在: " + request.getEmail());
            existingUser = userMapper.selectByEmail(request.getEmail().trim());
            if (existingUser != null) {
                System.out.println("邮箱已被注册: " + request.getEmail());
                throw new RuntimeException("邮箱已被注册，请使用其他邮箱");
            }
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername().trim());
        // 密码不加密，直接存储明文
        user.setPassword(request.getPassword());
        user.setRealName(request.getRealName() != null ? request.getRealName().trim() : null);
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        user.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        // 设置角色，如果请求中没有提供role，则默认为USER
        user.setRole(request.getRole() != null ? request.getRole().trim() : "USER");
        // status 在数据库中为 VARCHAR，统一用 active/inactive
        user.setStatus("active");

        System.out.println("准备插入用户: " + user.getUsername());
        int result = userMapper.insert(user);
        if (result > 0) {
            System.out.println("用户注册成功: " + user.getUsername() + ", ID: " + user.getId());
            // 生成JWT令牌
            return jwtUtil.generateToken(user.getUsername(), user.getId());
        } else {
            System.out.println("用户注册失败: " + user.getUsername());
            throw new RuntimeException("注册失败，请稍后重试");
        }
    }

    @Override
    public String login(LoginRequest request) {
        System.out.println("尝试登录用户: " + request.getUsername());
        
        // 根据用户名查询用户
        User user = userMapper.selectByUsername(request.getUsername().trim());
        if (user == null) {
            System.out.println("用户不存在: " + request.getUsername());
            throw new RuntimeException("用户名或密码错误");
        }

        System.out.println("找到用户: " + user.getUsername() + ", ID: " + user.getId() + ", 角色: " + user.getRole() + ", 状态: " + user.getStatus());

        // 验证密码：支持加密密码和明文密码两种方式
        boolean passwordMatch = false;
        String storedPassword = user.getPassword();
        String inputPassword = request.getPassword();
        
        System.out.println("存储的密码格式: " + (storedPassword != null && storedPassword.startsWith("$2a$") ? "BCrypt加密" : "明文"));
        System.out.println("输入的密码长度: " + (inputPassword != null ? inputPassword.length() : 0));
        
        // 首先尝试BCrypt加密密码匹配（兼容旧数据）
        try {
            if (storedPassword != null && storedPassword.startsWith("$2a$")) {
                // 密码是BCrypt加密格式，使用BCrypt验证
                passwordMatch = passwordEncoder.matches(inputPassword, storedPassword);
                System.out.println("BCrypt密码验证结果: " + passwordMatch);
                if (!passwordMatch) {
                    System.out.println("BCrypt密码验证失败，尝试明文比较");
                    // BCrypt验证失败，也尝试明文比较（以防数据库中的密码被修改为明文）
                    passwordMatch = storedPassword.equals(inputPassword);
                    System.out.println("明文比较结果: " + passwordMatch);
                }
            } else {
                // 密码是明文格式，直接比较
                passwordMatch = storedPassword != null && storedPassword.equals(inputPassword);
                System.out.println("明文密码验证结果: " + passwordMatch);
            }
        } catch (Exception e) {
            // BCrypt验证失败，尝试明文比较
            System.out.println("BCrypt验证异常: " + e.getMessage());
            passwordMatch = storedPassword != null && storedPassword.equals(inputPassword);
            System.out.println("BCrypt验证异常，使用明文比较: " + passwordMatch);
        }
        
        if (!passwordMatch) {
            System.out.println("密码验证失败: " + request.getUsername() + ", 存储密码: " + storedPassword + ", 输入密码: " + inputPassword);
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        // 兼容处理：status可能是Integer(0/1)或String("active"/"inactive")
        boolean isDisabled = false;
        if (user.getStatus() instanceof Integer) {
            isDisabled = ((Integer) user.getStatus()) == 0;
        } else if (user.getStatus() instanceof String) {
            isDisabled = "inactive".equalsIgnoreCase((String) user.getStatus());
        }
        
        if (isDisabled) {
            System.out.println("用户账户已被禁用: " + user.getUsername());
            throw new RuntimeException("账户已被禁用，请联系管理员");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        System.out.println("用户登录成功: " + user.getUsername() + ", 生成Token");
        return token;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public boolean changePassword(Long userId, ChangePasswordRequest request) {
        // 验证新密码确认
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }

        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证原密码：支持加密密码和明文密码两种方式
        String storedPassword = user.getPassword();
        String inputOldPassword = request.getOldPassword();
        boolean oldPasswordMatch = false;
        
        try {
            if (storedPassword != null && storedPassword.startsWith("$2a$")) {
                // 密码是BCrypt加密格式，使用BCrypt验证
                oldPasswordMatch = passwordEncoder.matches(inputOldPassword, storedPassword);
            } else {
                // 密码是明文格式，直接比较
                oldPasswordMatch = storedPassword != null && storedPassword.equals(inputOldPassword);
            }
        } catch (Exception e) {
            // BCrypt验证失败，尝试明文比较
            oldPasswordMatch = storedPassword != null && storedPassword.equals(inputOldPassword);
        }
        
        if (!oldPasswordMatch) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码：不加密，直接存储明文
        user.setPassword(request.getNewPassword());
        int result = userMapper.updateById(user);
        
        return result > 0;
    }

    @Override
    public List<User> searchUsers(String username, String realName, String role, Integer status) {
        User condition = new User();
        if (username != null && !username.trim().isEmpty()) {
            condition.setUsername(username.trim());
        }
        if (realName != null && !realName.trim().isEmpty()) {
            condition.setRealName(realName.trim());
        }
        if (role != null && !role.trim().isEmpty()) {
            condition.setRole(role.trim());
        }
        if (status != null) {
            condition.setStatus(status);
        }
        
        return userMapper.selectByCondition(condition);
    }

    @Override
    public int batchDeleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (Long id : ids) {
            try {
                if (userMapper.deleteById(id) > 0) {
                    successCount++;
                }
            } catch (Exception e) {
                System.out.println("删除用户ID " + id + " 失败: " + e.getMessage());
            }
        }
        
        return successCount;
    }

    @Override
    public boolean resetUserPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 重置密码为123456（不加密，直接存储明文）
        user.setPassword("123456");
        int result = userMapper.updateById(user);
        
        return result > 0;
    }
} 