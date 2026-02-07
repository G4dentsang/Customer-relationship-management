package com.b2b.b2b.modules.notification.service;

import com.b2b.b2b.exception.*;
import com.b2b.b2b.modules.auth.exception.InvalidTokenException;
import com.b2b.b2b.modules.auth.exception.RateLimitExceededException;
import com.b2b.b2b.modules.auth.exception.TokenExpiredException;
import com.b2b.b2b.modules.notification.model.EmailVerificationToken;
import com.b2b.b2b.modules.notification.persistence.EmailVerificationTokenRepository;
import com.b2b.b2b.modules.organization.model.Invitation;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.user.persistence.UserRepository;
import com.b2b.b2b.modules.notification.util.Utils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

  // @Value("${spring.mail.properties.domain_name}")
    private final String domainName = "tenzin.gadentsang2024@campus-eni.fr";

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JavaMailSender emailSender;

    @Async
    @Override
    @Transactional
    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(user);
        emailVerificationToken.setUsed(false);
        emailVerificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        emailVerificationTokenRepository.save(emailVerificationToken);

        String verificationUrl = Utils.getEmailVerificationLink(token);
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = "<h3>Welcome to the CRM!</h3>" +
                    "<p>Please click the link below to verify your account:</p>" +
                    "<a href='" + verificationUrl + "'>Verify Account</a>";

            helper.setTo(user.getEmail());
            helper.setSubject("Verification your CRM account");
            helper.setText(htmlContent, true);
            helper.setFrom(domainName);
            emailSender.send(mimeMessage);
        } catch (MessagingException me) {
            log.error("Error sending verification URL email to {} ", user.getEmail(), me);
        }

    }


    @Override
    @Transactional
    public void verifyToken(String token) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("verification-token", "token", token));

        if (emailVerificationToken.isUsed()) {
            throw new InvalidTokenException("This link has already been used. Please log in.");
        }
        if (emailVerificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Verification link expired. Please request a new one.");
        }

        User user = emailVerificationToken.getUser();
        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            user.setUserActive(true);
            userRepository.save(user);
        }

        emailVerificationToken.setUsed(true);
        emailVerificationTokenRepository.save(emailVerificationToken);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        if(user.isEmailVerified()) {
            throw new BadRequestException("This account is already verified. Please log in.");
        }

        Optional<EmailVerificationToken> lastToken = emailVerificationTokenRepository.findFirstByUserOrderByCreatedAtDesc(user);
        if(lastToken.isPresent() && lastToken.get().getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
           throw new RateLimitExceededException("Please wait 5 minutes before requesting another link.");
        }

        sendVerificationEmail(user);
    }

    @Override
    public void sendResetPasswordEmail(String email, String token) {
        String resetPasswordUrl = Utils.getEmailResetPasswordLink(token);
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = "<h3>Welcome to the CRM!</h3>" +
                    "<p>Please click the link below to reset your password:</p>" +
                    "<a href='" + resetPasswordUrl + "'>Reset Password</a>";

            helper.setTo(email);
            helper.setSubject("Reset Password");
            helper.setText(htmlContent, true);
            helper.setFrom(domainName);
            emailSender.send(mimeMessage);

        } catch (MessagingException me) {
            log.error("Error sending reset password URL email to {} ", email, me);
        }
    }

    @Override
    public void sendInvitationEmail(Invitation invitation) {
        String verificationUrl = Utils.getInvitationEmailLink(invitation.getToken());
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = String.format("<h3>Welcome to %s!</h3>" +
                            "<p>You have been invited to join as a <b>%s</b>.</p>" +
                            "<p>Please click the link below to set up your account:</p>" +
                            "<a href='%s' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Accept Invitation</a>",
                    invitation.getOrganization().getOrganizationName(), invitation.getRole().getAppRoles().name(), verificationUrl);

            helper.setTo(invitation.getEmail());
            helper.setSubject("CRM Accept Account Invitation");
            helper.setText(htmlContent, true);
            helper.setFrom(domainName);
            emailSender.send(mimeMessage);

        } catch (MessagingException me) {
            log.error("Error sending verification URL email to {} ", invitation.getEmail(), me);
        }

    }
}
