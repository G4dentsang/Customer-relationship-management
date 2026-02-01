package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.auth.entity.Invitation;
import com.b2b.b2b.modules.auth.entity.User;

public interface EmailService {
    void sendVerificationEmail(User user);
    void verifyToken(String token);
    void resendVerificationEmail(String email);
    void sendResetPasswordEmail(String email, String token);
    void sendInvitationEmail(Invitation invitation);
}
