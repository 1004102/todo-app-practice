package com.sparta.todoapppractice.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "Global Exception")
@Component
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.token.expiration}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh.token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    private static final String BEARER_PREFIX = "Bearer ";
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public String generateToken(String userId, String role, Date expirationDate) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("auth", role)
                .setExpiration(expirationDate)
                .setIssuedAt(new Date())
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    // 새로운 만료 일자 생성, 현재 시간에 넣어준 밀리초를 더하여 새로운 'Date' 객체를 생성
    private Date generateExpirationDate(long ms) {

        Date date = new Date();

        return new Date(date.getTime() + ms);
    }

    // 엑세스토큰 생성
    public String createAccessToken(String userId, String role) {

        Date expirationDate = generateExpirationDate(ACCESS_TOKEN_EXPIRATION);

        return generateToken(userId, role, expirationDate);
    }

    // 리프레쉬토큰 생성
    public String createRefreshToken(String userId, String role) {

        Date expirationDate = generateExpirationDate(REFRESH_TOKEN_EXPIRATION);

        return generateToken(userId, role, expirationDate);
    }

    // Http 요청에 Header 에서 엑세스토큰을 추출하는 메서드
    public String getAccessTokenFromHeader(HttpServletRequest request) {

        String authorizationHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return authorizationHeader.substring(7);
    }

    // Http 응답에 포함될 'Set-Cookie' Header 를 생성하는 메서드
    public ResponseCookie createCookieRefreshToken(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .maxAge(REFRESH_TOKEN_EXPIRATION / 1000)
                .path("/")
                .sameSite("Strict")
                .build();
    }

    // 특정 만료 날짜를 사용하여 리프레쉬토큰 쿠키를 생성하는 메서드
    public ResponseCookie createCookieRefreshToken(String refreshToken, Date expirationDate) {

        long maxAge = (expirationDate.getTime() - new Date().getTime()) / 1000;

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .maxAge(maxAge)
                .path("/")
                .sameSite("Strict")
                .build();
    }

    // Jwt 의 유효성을 검증하고, 유효한 경우 토큰에 클레임을 반환하는 메서드
    // 클레임이란 ? 토큰에 있는 PayLoad 부분에 포함된 실제 데이터
    public Claims getClaimsFromToken(String token) {

        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            log.error("만료된 토큰 입니다.");
            throw e;
        } catch (JwtException e) {
            log.error("유효하지 않은 토큰 입니다.");
            throw e;
        }

    }

}