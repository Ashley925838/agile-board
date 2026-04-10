package com.nusiss.agile_board.repository;

import com.nusiss.agile_board.model.Task;
import com.nusiss.agile_board.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findBySprint(Sprint sprint);
    List<Task> findBySprintAndStatus(Sprint sprint, Task.Status status);
}