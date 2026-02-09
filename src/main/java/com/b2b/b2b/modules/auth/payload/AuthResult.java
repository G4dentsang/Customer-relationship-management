package com.b2b.b2b.modules.auth.payload;

import com.b2b.b2b.modules.auth.security.response.LogInResponseDTO;
import org.springframework.http.ResponseCookie;

public record AuthResult(
        ResponseCookie accessTokenCookie,
        ResponseCookie refreshTokenCookie,
        LogInResponseDTO responseDTO
) {
}
