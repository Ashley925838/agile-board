package com.nusiss.agile_board.controller;

import com.nusiss.agile_board.dto.RegisterDto;
import com.nusiss.agile_board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // GET /login — 显示登录页面
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";   // 对应 templates/auth/login.html
    }

    // GET /register — 显示注册页面
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        return "auth/register";
    }

    // POST /register — 处理注册表单提交
    @PostMapping("/register")
    public String handleRegister(
            @Valid @ModelAttribute("registerDto") RegisterDto dto,
            BindingResult bindingResult,
            Model model) {

        // 如果表单验证失败（@NotBlank、@Email 等注解触发），留在注册页显示错误
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // 调用 service 处理业务逻辑
        String error = userService.register(dto);
        if (error != null) {
            model.addAttribute("error", error);
            return "auth/register";
        }

        // 注册成功，跳转到登录页并显示成功提示
        return "redirect:/login?registered=true";
    }
}