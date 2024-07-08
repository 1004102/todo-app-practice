package com.sparta.todoapppractice.domain.todoapp.entity;

import com.sparta.todoapppractice.domain.todoapp.dto.TodoAppRequestDto;
import com.sparta.todoapppractice.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "todoApp")
public class TodoApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    public TodoApp(User user, TodoAppRequestDto requestDto) {
        this.user = user;
        this.content = requestDto.getContent();
    }

    public void updateTodoApp (User user, TodoAppRequestDto requestDto) {
        this.user = user;
        this.content = requestDto.getContent();
    }
}
