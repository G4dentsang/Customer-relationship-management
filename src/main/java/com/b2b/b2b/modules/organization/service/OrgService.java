package com.b2b.b2b.modules.organization.service;

import com.b2b.b2b.modules.auth.security.request.SignUpRequestDTO;

public interface OrgService {
    void registerOrganizationAndAdmin(SignUpRequestDTO signUpRequestDTO);
}
