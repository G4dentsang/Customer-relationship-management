package com.b2b.b2b.modules.notification.service;


public interface EmailInfrastructureService {
    void sendHtmlEmail(String to, String subject, String htmlBody);
}
