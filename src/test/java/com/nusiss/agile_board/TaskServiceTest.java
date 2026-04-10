package com.nusiss.agile_board;

import com.nusiss.agile_board.model.Task;
import com.nusiss.agile_board.repository.TaskRepository;
import com.nusiss.agile_board.repository.UserRepository;
import com.nusiss.agile_board.service.SprintService;
import com.nusiss.agile_board.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SprintService sprintService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    // ============================================================
    // 测试5：Kanban 拖拽更新状态
    // 业务规则：拖到 IN_PROGRESS 列后，数据库里的状态要更新
    // ============================================================
    @Test
    @DisplayName("Task status updates correctly when dragged on Kanban board")
    void updateStatus_changesTaskStatus() {
        // Arrange：准备一个初始状态是 TODO 的 Task
        Task task = new Task();
        task.setTitle("Implement login page");
        task.setStatus(Task.Status.TODO);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act：模拟用户把卡片拖到 IN_PROGRESS 列
        Task updated = taskService.updateStatus(1L, Task.Status.IN_PROGRESS);

        // Assert：状态应该已经变成 IN_PROGRESS
        assertEquals(Task.Status.IN_PROGRESS, updated.getStatus(),
                "Task status should be updated to IN_PROGRESS after drag");

        // 验证 save() 被调用（状态变更必须持久化到数据库）
        verify(taskRepository, times(1)).save(task);
    }

    // ============================================================
    // 测试6：Task 默认优先级
    // 业务规则：创建时如果没指定优先级，默认是 MEDIUM
    // ============================================================
    @Test
    @DisplayName("New task has MEDIUM priority by default")
    void newTask_defaultPriority_isMedium() {
        Task task = new Task();
        assertEquals(Task.Priority.MEDIUM, task.getPriority(),
                "Default priority should be MEDIUM");
    }

    // ============================================================
    // 测试7：Task 默认状态
    // 业务规则：新建的 Task 应该出现在 Kanban 的 TODO 列
    // ============================================================
    @Test
    @DisplayName("New task starts in TODO status")
    void newTask_defaultStatus_isTodo() {
        Task task = new Task();
        assertEquals(Task.Status.TODO, task.getStatus(),
                "New task should start in TODO column on Kanban board");
    }
}