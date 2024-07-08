package com.sparta.todoapppractice.global.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityResponse {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        String json = objectMapper.writeValueAsString(message);

        response.setStatus(status.value());
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(json);
    }
}
