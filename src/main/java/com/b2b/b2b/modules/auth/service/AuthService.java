package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.auth.payload.AuthResult;
import com.b2b.b2b.modules.auth.security.request.LogInRequestDTO;
import com.b2b.b2b.modules.user.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    void verifyToken(String token);

    void resendVerificationEmail(String email);

    void createAndSendVerificationCode(User user);

    AuthResult processRefreshToken(HttpServletRequest request);

    AuthResult processLogin(LogInRequestDTO request);

    AuthResult processLogout(HttpServletRequest request);

    AuthResult switchOrganization(Integer targetOrgId);
}
