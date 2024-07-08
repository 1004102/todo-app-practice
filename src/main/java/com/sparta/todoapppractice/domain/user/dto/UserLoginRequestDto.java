package com.sparta.todoapppractice.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginRequestDto {

    @NotBlank(message = "아이디를 입력해 주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
}
