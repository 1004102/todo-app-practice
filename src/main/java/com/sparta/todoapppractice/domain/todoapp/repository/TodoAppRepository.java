package com.sparta.todoapppractice.domain.todoapp.repository;

import com.sparta.todoapppractice.domain.todoapp.entity.TodoApp;
import com.sparta.todoapppractice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoAppRepository extends JpaRepository<TodoApp, Long> {
    List<TodoApp> findAllByUser(User user);
    Optional<TodoApp> findByIdAndUser(Long todoAppId, User user);
}
