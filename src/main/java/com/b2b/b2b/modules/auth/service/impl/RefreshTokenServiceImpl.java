package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.exception.TokenRefreshException;
import com.b2b.b2b.modules.auth.entity.RefreshToken;
import com.b2b.b2b.modules.auth.repository.RefreshTokenRepository;
import com.b2b.b2b.modules.auth.service.RefreshTokenService;
import com.b2b.b2b.shared.AuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUtil authUtil;
    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, AuthUtil authUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authUtil = authUtil;
    }

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "token", token));
    }

    @Override
    public RefreshToken createRefreshToken(Integer userId, Integer activeOrgId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(authUtil.loggedInUser());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCurrentActiveOrgId(activeOrgId);

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void verifyExpiration(RefreshToken token) {
        if(token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please login again");
        }
    }

    @Override
    public void updateActiveOrg(Integer userId, Integer activeOrgId) {
        RefreshToken token = refreshTokenRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "userId", userId));

        token.setCurrentActiveOrgId(activeOrgId);
        refreshTokenRepository.save(token);
    }
}
