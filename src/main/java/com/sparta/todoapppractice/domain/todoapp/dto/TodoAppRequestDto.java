package com.sparta.todoapppractice.domain.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TodoAppRequestDto {

    @NotBlank
    private String content;
}
