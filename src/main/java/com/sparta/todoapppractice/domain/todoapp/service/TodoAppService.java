package com.sparta.todoapppractice.domain.todoapp.service;

import com.sparta.todoapppractice.domain.todoapp.dto.TodoAppRequestDto;
import com.sparta.todoapppractice.domain.todoapp.dto.TodoAppResponseDto;
import com.sparta.todoapppractice.domain.todoapp.entity.TodoApp;
import com.sparta.todoapppractice.domain.todoapp.repository.TodoAppRepository;
import com.sparta.todoapppractice.domain.user.entity.User;
import com.sparta.todoapppractice.domain.user.service.UserService;
import com.sparta.todoapppractice.global.dto.DataResponse;
import com.sparta.todoapppractice.global.dto.MessageResponse;
import com.sparta.todoapppractice.global.exception.BadRequestException;
import com.sparta.todoapppractice.global.exception.NotFoundException;
import jakarta.transaction.Transactional;
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
        TodoApp todoApp = new TodoApp(user, requestDto);

        todoAppRepository.save(todoApp);

        return new DataResponse<>(200, "할 일 생성에 성공하였습니다.", new TodoAppResponseDto(todoApp));
    }

    // 사용자별 전체 할 일 조회
    public DataResponse<List<TodoAppResponseDto>> getAllTodoAppByUser() {

        User user = userService.getUser();

        List<TodoAppResponseDto> responseDtoList = todoAppRepository.findAllByUser(user).stream()
                .map(TodoAppResponseDto::new).toList();

        return new DataResponse<>(200, "할 일 전체 조회에 성공하였습니다.", responseDtoList);
    }

    // 사용자별 특정 할 일 조회
    public DataResponse<TodoAppResponseDto> getTodoApp(Long todoAppId) {

        User user = userService.getUser();

        TodoApp todoApp = todoAppRepository.findByIdAndUser(todoAppId, user).orElseThrow(() -> new NotFoundException("해당 할 일을 찾을 수 없습니다."));
        TodoAppResponseDto responseDto = new TodoAppResponseDto(todoApp);

        return new DataResponse<>(200, "특정 할 일 조회에 성공하였습니다.", responseDto);
    }

    // 할 일 수정
    @Transactional
    public DataResponse<TodoAppResponseDto> updateTodoApp(Long id, TodoAppRequestDto requestDto) {

        User user = userService.getUser();

        TodoApp todoApp = findTodoAppById(id);
        todoApp.updateTodoApp(user, requestDto);

        TodoAppResponseDto responseDto = new TodoAppResponseDto(todoApp);

        return new DataResponse<>(200, "할 일 수정에 성공하였습니다.", responseDto);
    }

    public TodoApp findTodoAppById (Long id) {
        return todoAppRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("해당 할 일이 존재하지 않습니다."));
    }

    // 할 일 삭제
    public MessageResponse deleteTodoApp(Long todoAppId) {

        TodoApp todoApp = todoAppRepository.findById(todoAppId)
                .orElseThrow(() -> new NotFoundException("해당 할 일을 찾을 수 없습니다."));

        todoAppRepository.deleteById(todoAppId);

        return new MessageResponse(200, "할 일 삭제에 성공했습니다.");

    }
}
