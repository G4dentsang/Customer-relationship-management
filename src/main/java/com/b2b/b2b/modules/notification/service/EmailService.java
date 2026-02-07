package com.b2b.b2b.modules.notification.service;

import com.b2b.b2b.modules.organization.model.Invitation;
import com.b2b.b2b.modules.user.model.User;

public interface EmailService {
    void sendVerificationEmail(User user);
    void verifyToken(String token);
    void resendVerificationEmail(String email);
    void sendResetPasswordEmail(String email, String token);
    void sendInvitationEmail(Invitation invitation);
}
