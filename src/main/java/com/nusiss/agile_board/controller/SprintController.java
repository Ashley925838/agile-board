package com.nusiss.agile_board.controller;

import com.nusiss.agile_board.service.ProjectService;
import com.nusiss.agile_board.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/projects/{projectId}/sprints")
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;
    private final ProjectService projectService;

    @GetMapping
    public String listSprints(@PathVariable Long projectId, Model model) {
        model.addAttribute("project", projectService.getProjectById(projectId));
        model.addAttribute("sprints", sprintService.getSprintsByProject(projectId));
        return "sprints/list";
    }

    @GetMapping("/new")
    public String newSprintForm(@PathVariable Long projectId, Model model) {
        model.addAttribute("project", projectService.getProjectById(projectId));
        return "sprints/form";
    }

    @PostMapping
    public String createSprint(@PathVariable Long projectId,
                               @RequestParam String name,
                               @RequestParam(required = false) String goal,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        sprintService.createSprint(projectId, name, goal, startDate, endDate);
        return "redirect:/projects/" + projectId + "/sprints";
    }

    @PostMapping("/{sprintId}/delete")
    public String deleteSprint(@PathVariable Long projectId,
                               @PathVariable Long sprintId) {
        sprintService.deleteSprint(sprintId);
        return "redirect:/projects/" + projectId + "/sprints";
    }
}