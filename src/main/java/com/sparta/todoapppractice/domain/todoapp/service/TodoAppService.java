package com.sparta.todoapppractice.domain.todoapp.service;

import com.sparta.todoapppractice.domain.todoapp.dto.TodoAppRequestDto;
import com.sparta.todoapppractice.domain.todoapp.dto.TodoAppResponseDto;
import com.sparta.todoapppractice.domain.todoapp.entity.TodoApp;
import com.sparta.todoapppractice.domain.todoapp.repository.TodoAppRepository;
import com.sparta.todoapppractice.domain.user.entity.User;
import com.sparta.todoapppractice.domain.user.service.UserService;
import com.sparta.todoapppractice.global.dto.DataResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoAppService {

    private final TodoAppRepository todoAppRepository;
    private final UserService userService;

    public TodoAppService(TodoAppRepository todoAppRepository, UserService userService) {
        this.todoAppRepository = todoAppRepository;
        this.userService = userService;
    }

    // 할 일 생성
    public DataResponse<TodoAppResponseDto> createTodoApp(TodoAppRequestDto requestDto) {

        User user = userService.getUser();
        TodoApp todoApp = new TodoApp(requestDto);

        todoAppRepository.save(todoApp);

        return new DataResponse<>(200, "할 일 생성에 성공하였습니다.", new TodoAppResponseDto(todoApp));
    }

    // 사용자별 할 일 전체 조회
    public DataResponse<List<TodoAppResponseDto>> getAllTodoAppByUser() {

        User user = userService.getUser();

        List<TodoAppResponseDto> responseDtoList = todoAppRepository.findAllByUser(user).stream()
                .map(TodoAppResponseDto::new).toList();

        return new DataResponse<>(200, "할 일 전체 조회에 성공하였습니다.", responseDtoList);
    }
}
