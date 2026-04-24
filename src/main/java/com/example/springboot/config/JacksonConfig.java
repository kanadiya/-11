package com.example.springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 配置忽略未知字段，提高系统健壮性
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // 注册JavaTimeModule来处理Java 8时间类型
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 配置LocalDate的序列化和反序列化
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        
        // 配置LocalTime的序列化和反序列化
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        
        // 自定义LocalTime反序列化器，处理多种时间格式
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ISO_LOCAL_TIME) {
            @Override
            public LocalTime deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
                String text = p.getText();
                if (text == null || text.isEmpty()) {
                    return null;
                }
                
                try {
                    // 处理ISO格式的日期时间字符串，提取时间部分
                    if (text.contains("T")) {
                        // 如果是ISO格式的日期时间，提取时间部分
                        String timePart = text.split("T")[1];
                        // 移除时区信息
                        if (timePart.contains("Z")) {
                            timePart = timePart.replace("Z", "");
                        }
                        if (timePart.contains("+")) {
                            timePart = timePart.split("\\+")[0];
                        }
                        if (timePart.contains("-")) {
                            timePart = timePart.split("-")[0];
                        }
                        return LocalTime.parse(timePart, DateTimeFormatter.ISO_LOCAL_TIME);
                    } else if (text.contains(":")) {
                        // 如果是时间格式，直接解析
                        if (text.length() == 5) {
                            // HH:mm格式
                            return LocalTime.parse(text + ":00", DateTimeFormatter.ofPattern("HH:mm:ss"));
                        } else {
                            // HH:mm:ss格式
                            return LocalTime.parse(text, DateTimeFormatter.ofPattern("HH:mm:ss"));
                        }
                    } else {
                        // 如果是纯时间格式，直接解析
                        return LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
                    }
                } catch (Exception e) {
                    // 如果解析失败，尝试其他格式
                    try {
                        return LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
                    } catch (Exception ex) {
                        throw new RuntimeException("无法解析时间格式: " + text, ex);
                    }
                }
            }
        });
        
        objectMapper.registerModule(javaTimeModule);
        
        return objectMapper;
    }
} 