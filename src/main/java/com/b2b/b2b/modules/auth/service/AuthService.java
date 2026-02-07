package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.user.model.User;

public interface AuthService {
    void verifyToken(String token);

    void resendVerificationEmail(String email);

    void createAndSendVerificationCode(User user);
}
