package com.example.springboot.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后密码: " + encodedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("密码验证结果: " + matches);
    }
} 