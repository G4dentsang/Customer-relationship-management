package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.modules.auth.service.AuthMailService;
import com.b2b.b2b.modules.notification.service.EmailInfrastructureService;
import com.b2b.b2b.modules.notification.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthMailServiceImpl implements AuthMailService {
    private final EmailInfrastructureService emailSender;

    @Override
    public void sendVerificationEmail(String email, String token) {
        String url = Utils.getEmailVerificationLink(token);
        String html = "<h3>Welcome to the CRM!</h3>" +
                "<p>Please click the link below to verify your:</p>" +
                "<a href='" + url + "'>Verify Account</a>";

        emailSender.sendHtmlEmail(email, "Verify your CRM account", html);
    }

    @Override
    public void sendResetPasswordEmail(String email, String token) {
        String url = Utils.getEmailResetPasswordLink(token);
        String html = "<h3>Reset Password</h3>" + "<a href='" + url + "'>Click here to reset</a>";

        emailSender.sendHtmlEmail(email, "Verify your CRM account", html);
    }
}
