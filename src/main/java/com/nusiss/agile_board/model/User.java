package com.nusiss.agile_board.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")    // 表名用 users 而不是 user，因为 user 是 SQL 保留字
@Data                     // Lombok：自动生成 getter/setter/toString/equals
@NoArgsConstructor        // Lombok：自动生成无参构造器
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Column(unique = true, nullable = false)
    private String email;

    // 存储的是加密后的密码，永远不存明文
    // 这是 Spring Security 的强制要求，也是基本的安全常识
    @NotBlank
    @Column(nullable = false)
    private String password;

    // 用户角色：ROLE_USER 或 ROLE_ADMIN
    // Spring Security 用 "ROLE_" 前缀来识别权限
    @Column(nullable = false)
    private String role = "ROLE_USER";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 当 Entity 第一次保存到数据库之前自动执行
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}