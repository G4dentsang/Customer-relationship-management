package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.exception.*;
import com.b2b.b2b.modules.auth.entity.EmailVerificationToken;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.repository.EmailVerificationTokenRepository;
import com.b2b.b2b.modules.auth.repository.UserRepository;
import com.b2b.b2b.modules.auth.service.EmailService;
import com.b2b.b2b.modules.auth.util.HelperMethods;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.mail.properties.domain_name}")
    private String domainName;

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JavaMailSender emailSender;

    @Async
    @Override
    @Transactional
    public void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(passwordEncoder.encode(token));
        emailVerificationToken.setUser(user);
        emailVerificationToken.setUsed(false);
        emailVerificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        emailVerificationTokenRepository.save(emailVerificationToken);

        String verificationUrl = HelperMethods.getEmailVerificationToken(domainName, token);
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlContent = "<h3>Welcome to the CRM!</h3>" +
                    "<p>Please click the link below to verify your account:</p>" +
                    "<a href='" + verificationUrl + "'>Verify Account</a>";

            helper.setTo(user.getEmail());
            helper.setSubject("Verification CRM account");
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
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(passwordEncoder.encode(token))
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
        String resetPasswordUrl = HelperMethods.getEmailResetPasswordToken(domainName, token);
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
}
