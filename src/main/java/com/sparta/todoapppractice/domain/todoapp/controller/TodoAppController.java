package com.sparta.todoapppractice.domain.todoapp.controller;

import com.sparta.todoapppractice.domain.todoapp.dto.TodoAppRequestDto;
import com.sparta.todoapppractice.domain.todoapp.dto.TodoAppResponseDto;
import com.sparta.todoapppractice.domain.todoapp.service.TodoAppService;
import com.sparta.todoapppractice.global.dto.DataResponse;
import com.sparta.todoapppractice.global.dto.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todoapp")
public class TodoAppController {

    private final TodoAppService todoAppService;

    public TodoAppController(TodoAppService todoAppService) {
        this.todoAppService = todoAppService;
    }

    @PostMapping
    public ResponseEntity<DataResponse<TodoAppResponseDto>> createTodoApp(@Valid @RequestBody TodoAppRequestDto requestDto) {
        DataResponse<TodoAppResponseDto> responseDto = todoAppService.createTodoApp(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<DataResponse<List<TodoAppResponseDto>>> getAllTodoApp() {
        DataResponse<List<TodoAppResponseDto>> response = todoAppService.getAllTodoAppByUser();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{todoAppId}")
    public ResponseEntity<DataResponse<TodoAppResponseDto>> getTodoApp(@PathVariable Long todoAppId) {
        DataResponse<TodoAppResponseDto> response = todoAppService.getTodoApp(todoAppId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{todoAppId}")
    public ResponseEntity<DataResponse<TodoAppResponseDto>> updateTodoApp(@Valid @RequestBody TodoAppRequestDto requestDto,
                                                                          @PathVariable Long todoAppId) {
        DataResponse<TodoAppResponseDto> response = todoAppService.updateTodoApp(todoAppId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{todoAppId}")
    public ResponseEntity<MessageResponse> deleteTodoApp(@PathVariable Long todoAppId) {
        return ResponseEntity.status(HttpStatus.OK).body(todoAppService.deleteTodoApp(todoAppId));
    }
}
