package com.b2b.b2b.modules.auth.service;


public interface AuthMailService {
    void sendVerificationEmail(String email, String token);
    void sendResetPasswordEmail(String email, String token);
}
