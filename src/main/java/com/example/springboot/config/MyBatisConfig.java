package com.example.springboot.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.springboot.mapper")
public class MyBatisConfig {
    // MyBatis配置
} 