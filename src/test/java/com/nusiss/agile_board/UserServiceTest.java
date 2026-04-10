package com.nusiss.agile_board;

import com.nusiss.agile_board.dto.RegisterDto;
import com.nusiss.agile_board.repository.UserRepository;
import com.nusiss.agile_board.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)   // 告诉 JUnit 使用 Mockito
class UserServiceTest {

    // @Mock = 创建一个假的对象，我们控制它的行为
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // @InjectMocks = 创建真实的 UserService，把上面的 Mock 注入进去
    @InjectMocks
    private UserService userService;

    private RegisterDto validDto;

    // 每个测试运行之前先执行这个方法，准备测试数据
    @BeforeEach
    void setUp() {
        validDto = new RegisterDto();
        validDto.setUsername("testuser");
        validDto.setEmail("test@example.com");
        validDto.setPassword("password123");
        validDto.setConfirmPassword("password123");
    }

    // ============================================================
    // 测试1：正常注册流程
    // 业务规则：用户名和邮箱都不重复、密码一致 → 注册成功，返回 null
    // ============================================================
    @Test
    @DisplayName("Registration succeeds when username and email are unique")
    void register_success() {
        // Arrange（准备）：告诉假的 Repository "用户名和邮箱都不存在"
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");

        // Act（执行）：调用真实的 register 方法
        String result = userService.register(validDto);

        // Assert（验证）：返回 null 表示成功
        assertNull(result, "Successful registration should return null");

        // 验证 save() 被调用了一次（说明用户确实被保存了）
        verify(userRepository, times(1)).save(any());
    }

    // ============================================================
    // 测试2：用户名重复时拒绝注册
    // 业务规则：用户名必须唯一，已存在时返回错误信息
    // ============================================================
    @Test
    @DisplayName("Registration fails when username already exists")
    void register_duplicateUsername_returnsError() {
        // Arrange：假装用户名已经存在
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        String result = userService.register(validDto);

        // Assert：应该返回错误信息，而不是 null
        assertNotNull(result, "Should return an error message");
        assertEquals("Username already taken", result);

        // 最重要：验证 save() 从未被调用（重复用户名不能被保存到数据库）
        verify(userRepository, never()).save(any());
    }

    // ============================================================
    // 测试3：密码不一致时拒绝注册
    // 业务规则：confirmPassword 必须和 password 一致
    // ============================================================
    @Test
    @DisplayName("Registration fails when passwords do not match")
    void register_passwordMismatch_returnsError() {
        // Arrange：用户名和邮箱没问题，但密码不一致
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        validDto.setConfirmPassword("different_password");  // 故意设错

        // Act
        String result = userService.register(validDto);

        // Assert
        assertNotNull(result);
        assertEquals("Passwords do not match", result);
        verify(userRepository, never()).save(any());
    }

    // ============================================================
    // 测试4：邮箱重复时拒绝注册
    // 业务规则：邮箱也必须唯一
    // ============================================================
    @Test
    @DisplayName("Registration fails when email already exists")
    void register_duplicateEmail_returnsError() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        String result = userService.register(validDto);

        assertNotNull(result);
        assertEquals("Email already registered", result);
        verify(userRepository, never()).save(any());
    }
}