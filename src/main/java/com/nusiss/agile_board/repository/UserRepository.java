package com.nusiss.agile_board.repository;

import com.nusiss.agile_board.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA 的"查询方法"：根据方法名自动生成 SQL
    // findByUsername → SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // findByEmail → SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // existsByUsername → SELECT COUNT(*) FROM users WHERE username = ? > 0
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}