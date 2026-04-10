package com.nusiss.agile_board.repository;

import com.nusiss.agile_board.model.Sprint;
import com.nusiss.agile_board.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProject(Project project);
}