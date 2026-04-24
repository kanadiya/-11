package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.dto.ChangePasswordRequest;
import com.example.springboot.dto.LoginRequest;
import com.example.springboot.dto.RegisterRequest;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import com.example.springboot.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String token = userService.register(request);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "注册成功");
            return Result.success("注册成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = userService.login(request);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "登录成功");
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }

            String username = jwtUtil.getUsernameFromToken(token);
            User user = userService.getUserByUsername(username);
            
            if (user != null) {
                // 不返回密码
                user.setPassword(null);
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取所有用户（管理员功能）
     */
    @GetMapping("/list")
    public Result<List<User>> getAllUsers(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }

            String username = jwtUtil.getUsernameFromToken(token);
            User currentUser = userService.getUserByUsername(username);
            
            if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
                return Result.error(403, "权限不足");
            }

            List<User> users = userService.getAllUsers();
            // 清除密码信息
            users.forEach(user -> user.setPassword(null));
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<String> updateUser(@RequestBody User user, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }

            Long currentUserId = jwtUtil.getUserIdFromToken(token);
            User currentUser = userService.getUserById(currentUserId);
            
            // 只能更新自己的信息，或者管理员可以更新任何用户
            if (!currentUserId.equals(user.getId()) && !"ADMIN".equals(currentUser.getRole())) {
                return Result.error(403, "权限不足");
            }

            boolean success = userService.updateUser(user);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除用户（管理员功能）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }

            String username = jwtUtil.getUsernameFromToken(token);
            User currentUser = userService.getUserByUsername(username);
            
            if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
                return Result.error(403, "权限不足");
            }

            // 检查是否删除自己
            if (currentUser.getId().equals(id)) {
                return Result.error("不能删除自己的账户");
            }

            // 检查是否删除admin用户
            User targetUser = userService.getUserById(id);
            if (targetUser != null && "admin".equals(targetUser.getUsername())) {
                return Result.error("不能删除超级管理员账户");
            }

            boolean success = userService.deleteUser(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据条件搜索用户（管理员功能）
     */
    @GetMapping("/search")
    public Result<List<User>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }

            String currentUsername = jwtUtil.getUsernameFromToken(token);
            User currentUser = userService.getUserByUsername(currentUsername);
            
            if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
                return Result.error(403, "权限不足");
            }

            List<User> users = userService.searchUsers(username, realName, role, status);
            // 清除密码信息
            users.forEach(user -> user.setPassword(null));
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除用户（管理员功能）
     */
    @DeleteMapping("/batch")
    public Result<String> batchDeleteUsers(@RequestBody List<Long> ids, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }

            String username = jwtUtil.getUsernameFromToken(token);
            User currentUser = userService.getUserByUsername(username);
            
            if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
                return Result.error(403, "权限不足");
            }

            // 检查是否包含admin用户
            for (Long id : ids) {
                User user = userService.getUserById(id);
                if (user != null && "admin".equals(user.getUsername())) {
                    return Result.error("不能删除超级管理员账户");
                }
            }

            // 检查是否删除自己
            if (ids.contains(currentUser.getId())) {
                return Result.error("不能删除自己的账户");
            }

            int successCount = userService.batchDeleteUsers(ids);
            return Result.success("成功删除 " + successCount + " 个用户");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 重置用户密码（管理员功能）
     */
    @PostMapping("/{id}/reset-password")
    public Result<String> resetUserPassword(@PathVariable Long id, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }

            String username = jwtUtil.getUsernameFromToken(token);
            User currentUser = userService.getUserByUsername(username);
            
            if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
                return Result.error(403, "权限不足");
            }

            boolean success = userService.resetUserPassword(id);
            if (success) {
                return Result.success("密码重置成功，新密码为：123456");
            } else {
                return Result.error("密码重置失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token != null && jwtUtil.validateToken(token)) {
                // 这里可以实现token黑名单机制，暂时简单返回成功
                return Result.success("退出登录成功");
            } else {
                return Result.error(401, "未授权访问");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@Valid @RequestBody ChangePasswordRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            if (token == null || !jwtUtil.validateToken(token)) {
                return Result.error(401, "未授权访问");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            boolean success = userService.changePassword(userId, request);
            
            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 