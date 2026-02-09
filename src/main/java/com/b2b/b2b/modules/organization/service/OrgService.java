package com.b2b.b2b.modules.organization.service;

import com.b2b.b2b.modules.organization.payload.RegisterOrganizationRequestDTO;

public interface OrgService {
    void registerOrganizationAndAdmin(RegisterOrganizationRequestDTO registerOrganizationRequestDTO);
}
