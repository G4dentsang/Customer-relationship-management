package com.b2b.b2b.modules.notification.api;


import com.b2b.b2b.modules.notification.api.dto.EmailElements;

public interface NotificationApi {
    void sendHtmlEmail(EmailElements emailElements);
}
