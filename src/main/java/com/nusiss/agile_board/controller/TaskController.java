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

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final SprintService sprintService;
    private final UserRepository userRepository;

    // Task 列表（简单列表视图）
    @GetMapping("/sprints/{sprintId}/tasks")
    public String listTasks(@PathVariable Long sprintId, Model model) {
        model.addAttribute("sprint", sprintService.getSprintById(sprintId));
        model.addAttribute("tasks", taskService.getTasksBySprint(sprintId));
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("priorities", Task.Priority.values());
        return "tasks/list";
    }

    // Kanban 看板视图（把 tasks 按状态分成三组传给模板）
    @GetMapping("/sprints/{sprintId}/kanban")
    public String kanbanBoard(@PathVariable Long sprintId, Model model) {
        List<Task> allTasks = taskService.getTasksBySprint(sprintId);

        model.addAttribute("sprint", sprintService.getSprintById(sprintId));
        model.addAttribute("todoTasks",
                allTasks.stream()
                        .filter(t -> t.getStatus() == Task.Status.TODO)
                        .collect(Collectors.toList()));
        model.addAttribute("inProgressTasks",
                allTasks.stream()
                        .filter(t -> t.getStatus() == Task.Status.IN_PROGRESS)
                        .collect(Collectors.toList()));
        model.addAttribute("doneTasks",
                allTasks.stream()
                        .filter(t -> t.getStatus() == Task.Status.DONE)
                        .collect(Collectors.toList()));

        return "tasks/kanban";
    }

    // 创建 Task 表单
    @GetMapping("/sprints/{sprintId}/tasks/new")
    public String newTaskForm(@PathVariable Long sprintId, Model model) {
        model.addAttribute("sprint", sprintService.getSprintById(sprintId));
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("priorities", Task.Priority.values());
        return "tasks/form";
    }

    // 提交创建 Task
    @PostMapping("/sprints/{sprintId}/tasks")
    public String createTask(@PathVariable Long sprintId,
                             @RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam Task.Priority priority,
                             @RequestParam(required = false) Long assignedToId) {
        taskService.createTask(sprintId, title, description, priority, assignedToId);
        return "redirect:/sprints/" + sprintId + "/kanban";
    }

    // Kanban 拖拽更新状态（AJAX）
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
        return "redirect:/sprints/" + sprintId + "/kanban";
    }
}