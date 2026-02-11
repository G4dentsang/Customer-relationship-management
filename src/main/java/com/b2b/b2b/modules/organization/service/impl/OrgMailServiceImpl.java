package com.b2b.b2b.modules.organization.service.impl;

import com.b2b.b2b.modules.notification.api.NotificationApi;
import com.b2b.b2b.modules.notification.api.dto.EmailElements;
import com.b2b.b2b.modules.organization.model.Invitation;
import com.b2b.b2b.modules.organization.service.OrgMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrgMailServiceImpl implements OrgMailService
{
    private final NotificationApi api;
    @Override
    public void sendInvitationEmail(Invitation invitation) {
        String url = OrganizationLinkGenerator.getInvitationEmailLink(invitation.getToken());
        String role = invitation.getRole().getAppRoles().name();
        String orgName = invitation.getOrganization().getOrganizationName();

        String html = String.format("<h3>Welcome to %s!</h3>" +
                "<p>You have been invited to join as a <b>%s</b>.</p>" +
                "<p>Please click the link below to set up your account:</p>" +
                "<a href='%s'>Accept Invitation</a>",
                orgName, role, url);

        EmailElements emailElements = new EmailElements(invitation.getEmail(), "You're Invited!", html);

        api.sendHtmlEmail(emailElements);
    }
}
