package com.nusiss.agile_board.service;

import com.nusiss.agile_board.model.Sprint;
import com.nusiss.agile_board.model.Task;
import com.nusiss.agile_board.model.User;
import com.nusiss.agile_board.repository.TaskRepository;
import com.nusiss.agile_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final SprintService sprintService;
    private final UserRepository userRepository;

    public List<Task> getTasksBySprint(Long sprintId) {
        Sprint sprint = sprintService.getSprintById(sprintId);
        return taskRepository.findBySprint(sprint);
    }

    public Task createTask(Long sprintId, String title, String description,
                           Task.Priority priority, Long assignedToId) {
        Sprint sprint = sprintService.getSprintById(sprintId);
        Task task = new Task();
        task.setSprint(sprint);
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        if (assignedToId != null) {
            userRepository.findById(assignedToId)
                    .ifPresent(task::setAssignedTo);
        }
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    // Kanban 看板拖拽时调用这个方法更新状态
    public Task updateStatus(Long taskId, Task.Status newStatus) {
        Task task = getTaskById(taskId);
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // Dashboard 用：统计某个Sprint里完成的任务数
    public long countDone(Long sprintId) {
        Sprint sprint = sprintService.getSprintById(sprintId);
        return taskRepository.findBySprintAndStatus(sprint, Task.Status.DONE).size();
    }

    public long countTotal(Long sprintId) {
        Sprint sprint = sprintService.getSprintById(sprintId);
        return taskRepository.findBySprint(sprint).size();
    }
}