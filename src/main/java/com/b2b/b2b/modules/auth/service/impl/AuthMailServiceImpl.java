package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.modules.auth.service.AuthMailService;
import com.b2b.b2b.modules.notification.api.NotificationApi;
import com.b2b.b2b.modules.notification.api.dto.EmailElements;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthMailServiceImpl implements AuthMailService {
    private final NotificationApi api;

    @Override
    public void sendVerificationEmail(String email, String token) {
        String url = AuthLinkGenerator.getEmailVerificationLink(token);
        String html = "<h3>Welcome to the CRM!</h3>" +
                "<p>Please click the link below to verify your:</p>" +
                "<a href='" + url + "'>Verify Account</a>";
        EmailElements emailElements = new EmailElements(email,"Verify your CRM account",html );

        api.sendHtmlEmail(emailElements);
    }

    @Override
    public void sendResetPasswordEmail(String email, String token) {
        String url = AuthLinkGenerator.getEmailResetPasswordLink(token);
        String html = "<h3>Reset Password</h3>" + "<a href='" + url + "'>Click here to reset</a>";

        EmailElements emailElements = new EmailElements(email,"RE-set your CRM account",html );

        api.sendHtmlEmail(emailElements);
    }
}
