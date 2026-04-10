package com.nusiss.agile_board.repository;

import com.nusiss.agile_board.model.Project;
import com.nusiss.agile_board.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // 查找某个用户创建的所有项目
    List<Project> findByOwner(User owner);
}