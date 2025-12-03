package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.auth.entity.EmailVerificationToken;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.repository.EmailVerificationTokenRepository;
import com.b2b.b2b.modules.auth.util.HelperMethods;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class EmailVerificaitonServiceImpl implements EmailVerificationService
{
    @Value("${spring.mail.properties.domain_name}")
    private String domainName;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JavaMailSender emailSender;
    public EmailVerificaitonServiceImpl(EmailVerificationTokenRepository emailVerificationTokenRepository, JavaMailSender emailSender)
    {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailSender = emailSender;
    }
    @Override
    public void sendVerificationEmail(User user) {
        //create token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(user);
        emailVerificationToken.setUsed(false);
        emailVerificationToken.setExpiryDate(LocalDateTime.now().plusHours(3));
        emailVerificationTokenRepository.save(emailVerificationToken);

        // send email with token
       String link = HelperMethods.getEmailVerificationToken(token);
       SimpleMailMessage message = new SimpleMailMessage();
       message.setTo(user.getEmail());
       message.setSubject("Verification Email : crm email manager");
       message.setText("Please click here to verify:  " + link);
       message.setFrom(domainName);
       emailSender.send(message);
    }

    @Override
    public void verifyToken(String token) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token);
        if(emailVerificationToken.isUsed()) throw new RuntimeException("Token is already used");
        if(emailVerificationToken.getExpiryDate().isBefore(LocalDateTime.now())) throw new RuntimeException("Token is expired");
        User user = emailVerificationToken.getUser();
        user.setEmailVerified(true);
        emailVerificationTokenRepository.save(emailVerificationToken);
    }
}
