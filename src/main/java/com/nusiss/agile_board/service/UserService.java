package com.nusiss.agile_board.service;

import com.nusiss.agile_board.dto.RegisterDto;
import com.nusiss.agile_board.model.User;
import com.nusiss.agile_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor   // Lombok：自动生成包含所有 final 字段的构造器（依赖注入用）
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // 来自 Spring Security 配置

    /**
     * 注册新用户
     * 返回 null 表示成功，返回错误信息字符串表示失败原因
     */
    public String register(RegisterDto dto) {

        // 业务规则验证
        if (userRepository.existsByUsername(dto.getUsername())) {
            return "Username already taken";
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            return "Email already registered";
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return "Passwords do not match";
        }

        // 创建 User 实体
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // 密码必须加密后才能存入数据库——BCrypt 是业界标准
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
        return null;  // null = 成功
    }
}