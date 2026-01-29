package com.b2b.b2b.modules.auth.service;

import com.b2b.b2b.modules.auth.entity.RefreshToken;


public interface RefreshTokenService {
    RefreshToken findByToken(String refreshToken);
    RefreshToken createRefreshToken(Integer userId, Integer activeOrgId);
    void verifyExpiration(RefreshToken refreshToken);
    void updateActiveOrg(Integer userId, Integer activeOrgId);

}
