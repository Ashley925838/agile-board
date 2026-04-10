package com.nusiss.agile_board.controller;

import com.nusiss.agile_board.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    // 列出当前用户的所有项目
    @GetMapping
    public String listProjects(@AuthenticationPrincipal UserDetails currentUser,
                               Model model) {
        model.addAttribute("projects",
                projectService.getProjectsByUser(currentUser.getUsername()));
        return "projects/list";
    }

    // 显示创建项目的表单
    @GetMapping("/new")
    public String newProjectForm() {
        return "projects/form";
    }

    // 处理创建项目表单提交
    @PostMapping
    public String createProject(@RequestParam String name,
                                @RequestParam(required = false) String description,
                                @AuthenticationPrincipal UserDetails currentUser) {
        projectService.createProject(name, description, currentUser.getUsername());
        return "redirect:/projects";
    }

    // 删除项目
    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "redirect:/projects";
    }
}