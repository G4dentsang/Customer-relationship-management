package com.b2b.b2b.modules.auth.service;


public interface PasswordResetService {
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
}
