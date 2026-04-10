package com.nusiss.agile_board.controller;

import com.nusiss.agile_board.model.Task;
import com.nusiss.agile_board.service.SprintService;
import com.nusiss.agile_board.service.TaskService;
import com.nusiss.agile_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final SprintService sprintService;
    private final UserRepository userRepository;

    // Task 列表（也是 Kanban 入口）
    @GetMapping("/sprints/{sprintId}/tasks")
    public String listTasks(@PathVariable Long sprintId, Model model) {
        model.addAttribute("sprint", sprintService.getSprintById(sprintId));
        model.addAttribute("tasks", taskService.getTasksBySprint(sprintId));
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("priorities", Task.Priority.values());
        return "tasks/list";
    }

    // 显示创建 Task 表单
    @GetMapping("/sprints/{sprintId}/tasks/new")
    public String newTaskForm(@PathVariable Long sprintId, Model model) {
        model.addAttribute("sprint", sprintService.getSprintById(sprintId));
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("priorities", Task.Priority.values());
        return "tasks/form";
    }

    // 创建 Task
    @PostMapping("/sprints/{sprintId}/tasks")
    public String createTask(@PathVariable Long sprintId,
                             @RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam Task.Priority priority,
                             @RequestParam(required = false) Long assignedToId) {
        taskService.createTask(sprintId, title, description, priority, assignedToId);
        return "redirect:/sprints/" + sprintId + "/tasks";
    }

    // Kanban 拖拽更新状态（AJAX 调用，返回 JSON）
    @PostMapping("/tasks/{taskId}/status")
    @ResponseBody
    public ResponseEntity<String> updateStatus(@PathVariable Long taskId,
                                               @RequestParam Task.Status status) {
        taskService.updateStatus(taskId, status);
        return ResponseEntity.ok("updated");
    }

    // 删除 Task
    @PostMapping("/tasks/{taskId}/delete")
    public String deleteTask(@PathVariable Long taskId,
                             @RequestParam Long sprintId) {
        taskService.deleteTask(taskId);
        return "redirect:/sprints/" + sprintId + "/tasks";
    }
}