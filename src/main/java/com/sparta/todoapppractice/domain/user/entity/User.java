package com.sparta.todoapppractice.domain.user.entity;

import com.sparta.todoapppractice.domain.user.dto.UserSignupRequestDto;
import com.sparta.todoapppractice.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column
    private String refreshToken;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserStatus status;

    // 패스워드 리스트 저장
    @ElementCollection  // 컬렉션 필드 정의
    private List<String> passwordList = new LinkedList<>();

    private static final int PASSWORD_LENGTH = 3;

    public User(UserSignupRequestDto requestDto, UserRole role, UserStatus status) {
        this.userId = requestDto.getUserId();
        this.password = requestDto.getPassword();
        this.name = requestDto.getName();
        this.role = role;
        this.status = status;
    }

    // 암호화된 패스워드를 사용하여 패스워드 리스트 필드를 설정
    public void encryptionPassword(String encryptionPassword) {
        this.password = encryptionPassword;

        if (passwordList.size() >= PASSWORD_LENGTH) {
            passwordList.remove(0);
        }

        passwordList.add(encryptionPassword);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
    }

    public void updateUserRole(UserRole role) {
        this.role = role;
    }
}
