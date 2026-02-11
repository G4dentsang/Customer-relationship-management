package com.b2b.b2b.modules.notification.api.dto;

public record EmailElements(
        String to,
        String subject,
        String htmlBody
) {
}
