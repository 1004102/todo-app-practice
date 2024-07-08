package com.sparta.todoapppractice.domain.user.service;

import com.sparta.todoapppractice.domain.user.dto.UserSignupRequestDto;
import com.sparta.todoapppractice.domain.user.dto.UserWithdrawalRequestDto;
import com.sparta.todoapppractice.domain.user.entity.User;
import com.sparta.todoapppractice.domain.user.entity.UserRole;
import com.sparta.todoapppractice.domain.user.entity.UserStatus;
import com.sparta.todoapppractice.domain.user.repository.UserRepository;
import com.sparta.todoapppractice.domain.user.security.UserDetailsImpl;
import com.sparta.todoapppractice.global.dto.DataResponse;
import com.sparta.todoapppractice.global.dto.MessageResponse;
import com.sparta.todoapppractice.global.exception.BadRequestException;
import com.sparta.todoapppractice.global.exception.NotFoundException;
import com.sparta.todoapppractice.global.exception.UnauthorizedException;
import com.sparta.todoapppractice.global.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public MessageResponse signup(UserSignupRequestDto requestDto) {

        userRepository.findByUserId(requestDto.getUserId()).ifPresent((el) -> {
            throw new BadRequestException("이미 존재하는 아이디입니다.");
        });

        User user = new User(requestDto, UserRole.USER, UserStatus.NORMAL);

        String encryptionPassword = passwordEncoder.encode(requestDto.getPassword());
        user.encryptionPassword(encryptionPassword);

        userRepository.save(user);

        return new MessageResponse(201, "회원가입에 성공했습니다.");

    }

    @Transactional
    public void logout() {

        User user = getUser();
        user.updateRefreshToken(null);

    }

    @Transactional
    public void withdrawal(UserWithdrawalRequestDto requestDto) {

        User user = getUser();

        checkPassword(requestDto.getPassword(), user.getPassword());

        if (user.getStatus() == UserStatus.LEAVE) {
            throw new NotFoundException("이미 탈퇴한 회원입니다.");
        }

        UserStatus status = UserStatus.LEAVE;

        user.updateStatus(status);
        user.updateRefreshToken(null);

    }

    @Transactional
    public HttpHeaders refreshToken(HttpServletRequest request) {

        String token = getRefreshToken(request);

        if (!StringUtils.hasText(token)) {
            throw new BadRequestException("잘못된 요청입니다.");
        }

        return validateToken(token);

    }

    @Transactional
    public MessageResponse updateUserRole() {

        User user = getUser();

        user.updateUserRole(UserRole.ADMIN);

        return new MessageResponse(200, "회원 권한 변경에 성공했습니다.");
    }

    public User getUser() {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByUserId(userDetails.getUsername()).orElseThrow(() -> new NotFoundException("해당 회원은 존재하지 않습니다."));
    }

    private void checkPassword(String inputPassword, String dbPassword) {
        if (!passwordEncoder.matches(inputPassword, dbPassword)) {
            throw new BadRequestException("비밀번호를 확인해주세요.");
        }
    }

    private String getRefreshToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new BadRequestException("잘못된 요청입니다.");
        }

        for (Cookie el : cookies) {
            if ("refreshToken".equals(el.getName())) {
                return el.getValue();
            }
        }

        return null;
    }

    private HttpHeaders validateToken(String token) {

        try {

            Claims info = jwtProvider.getClaimsFromToken(token);
            User user = userRepository.findByUserId(info.getSubject()).orElseThrow(() -> new NotFoundException("해당 회원은 존재하지 않습니다."));

            if (!user.getRefreshToken().equals(token)) {
                throw new UnauthorizedException("유효하지 않은 토큰 입니다.");
            }

            return setHeaders(info, user);

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("만료된 토큰 입니다.");
        } catch (JwtException e) {
            throw new UnauthorizedException("유효하지 않은 토큰 입니다.");
        }

    }

    private HttpHeaders setHeaders(Claims info, User user) {

        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getRole().getRole());
        String refreshToken = jwtProvider.generateToken(user.getUserId(), user.getRole().getRole(), info.getExpiration());
        ResponseCookie responseCookie = jwtProvider.createCookieRefreshToken(refreshToken, info.getExpiration());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        user.updateRefreshToken(refreshToken);

        return headers;

    }

}