package com.nusiss.agile_board.config;


import com.nusiss.agile_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;

    /**
     * 密码加密器：BCrypt 是目前最推荐的密码哈希算法
     * 每次加密结果不同（加盐），即使两个用户密码相同，数据库里存的哈希值也不同
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 告诉 Spring Security 如何根据用户名查找用户
     * 登录时 Spring Security 会调用这个方法
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole().replace("ROLE_", ""))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    /**
     * 安全规则配置：哪些页面需要登录，哪些页面公开访问
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 这些路径不需要登录就能访问
                        .requestMatchers("/register", "/login", "/h2-console/**",
                                "/css/**", "/js/**").permitAll()
                        // 其他所有路径都需要登录
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")           // 自定义登录页面的 URL
                        .defaultSuccessUrl("/dashboard", true)  // 登录成功跳转到 dashboard
                        .failureUrl("/login?error=true")        // 登录失败返回登录页并带参数
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                // 允许 H2 控制台在 iframe 里显示（H2 console 需要这个）
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // 对 H2 控制台禁用 CSRF（仅开发环境）
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));

        return http.build();
    }
}