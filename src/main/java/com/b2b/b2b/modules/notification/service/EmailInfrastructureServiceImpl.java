package com.b2b.b2b.modules.notification.service;

import com.b2b.b2b.exception.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailInfrastructureServiceImpl implements EmailInfrastructureService
{
    private final String domainName;
    private final JavaMailSender emailSender;

    public EmailInfrastructureServiceImpl(@Value("${spring.mail.properties.domain_name}") String domainName,
                                          JavaMailSender emailSender) {
        this.domainName = domainName;
        this.emailSender = emailSender;
    }

    @Async
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom(domainName);

            emailSender.send(mimeMessage);
            log.info("Email sent successfully to {} ", to);
        } catch (MessagingException me) {
            log.error("Error sending verification URL email to {} ", to, me);
        }
    }

}
