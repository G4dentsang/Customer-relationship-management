package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.auth.security.request.SignUpRequestDTO;

public interface AuthService {
    void registerOrganizationAndAdmin(SignUpRequestDTO signUpRequestDTO);
}
