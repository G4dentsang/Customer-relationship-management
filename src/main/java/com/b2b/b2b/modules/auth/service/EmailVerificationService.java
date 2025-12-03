package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.auth.entity.EmailVerificationToken;
import com.b2b.b2b.modules.auth.entity.User;

public interface EmailVerificationService {
    void sendVerificationEmail(User user);
    void verifyToken(String token);
}
