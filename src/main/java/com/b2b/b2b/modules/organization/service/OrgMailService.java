package com.b2b.b2b.modules.organization.service;

import com.b2b.b2b.modules.organization.model.Invitation;

public interface OrgMailService {
    void sendInvitationEmail(Invitation invitation);
}
