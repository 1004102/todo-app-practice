package com.sparta.todoapppractice.domain.user.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserSignupRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 10, message = "아이디는 4자 이상, 10자 이하여야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 영문 소문자, 숫자만 가능합니다.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자 이상, 15자 이하여야 합니다.")
    @Pattern(regexp = "(?=.*[a-zA-Z0-9])(?=.*[!@#$%^&*()_+=?<>{};:'/]).+", message = "비밀번호는 영문 대소문자와 숫자, 특수 문자로 이루어져야 합니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;
}