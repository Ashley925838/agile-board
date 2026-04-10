package com.nusiss.agile_board.service;

import com.nusiss.agile_board.model.Project;
import com.nusiss.agile_board.model.Sprint;
import com.nusiss.agile_board.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectService projectService;

    public List<Sprint> getSprintsByProject(Long projectId) {
        Project project = projectService.getProjectById(projectId);
        return sprintRepository.findByProject(project);
    }

    public Sprint createSprint(Long projectId, String name, String goal,
                               LocalDate startDate, LocalDate endDate) {
        Project project = projectService.getProjectById(projectId);
        Sprint sprint = new Sprint();
        sprint.setProject(project);
        sprint.setName(name);
        sprint.setGoal(goal);
        sprint.setStartDate(startDate);
        sprint.setEndDate(endDate);
        return sprintRepository.save(sprint);
    }

    public Sprint getSprintById(Long id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));
    }

    public void deleteSprint(Long id) {
        sprintRepository.deleteById(id);
    }
}