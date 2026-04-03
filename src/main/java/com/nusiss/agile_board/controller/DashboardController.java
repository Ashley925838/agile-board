package com.nusiss.agile_board.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails currentUser,
                            Model model) {
        // 把当前登录用户的用户名传给模板显示
        model.addAttribute("username", currentUser.getUsername());
        return "dashboard";  // 对应 templates/dashboard.html
    }
}