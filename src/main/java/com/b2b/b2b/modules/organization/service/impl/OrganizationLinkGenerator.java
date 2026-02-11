package com.b2b.b2b.modules.organization.service.impl;

import org.springframework.stereotype.Component;

@Component
public class OrganizationLinkGenerator {
    //link to form
    public static String getInvitationEmailLink(String token) {
        return String.format("http://localhost:8080/app/v1/management/users/accept-invitation-page?token=%s", token);
    }

}
