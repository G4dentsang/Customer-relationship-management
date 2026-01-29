package com.b2b.b2b.modules.auth.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Component
@Slf4j
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: {} | Path: {}", authException.getMessage(), request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> responseObject = new HashMap<>();
        responseObject.put("timestamp", LocalDateTime.now().toString());
        responseObject.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        responseObject.put("error", "Unauthorized attempt");
        responseObject.put("message", "Full authentication is required to access this resource");
        responseObject.put("path", request.getRequestURI());


        mapper.writeValue(response.getOutputStream(), responseObject); // converts response & body to JSON format
    }
}
