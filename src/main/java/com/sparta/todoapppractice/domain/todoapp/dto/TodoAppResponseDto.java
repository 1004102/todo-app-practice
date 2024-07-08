package com.sparta.todoapppractice.domain.todoapp.dto;

import com.sparta.todoapppractice.domain.todoapp.entity.TodoApp;
import lombok.Getter;

@Getter
public class TodoAppResponseDto {

    private Long id;
    private String content;

    public TodoAppResponseDto(TodoApp todoApp) {
        this.id = todoApp.getId();
        this.content = todoApp.getContent();
    }
}
