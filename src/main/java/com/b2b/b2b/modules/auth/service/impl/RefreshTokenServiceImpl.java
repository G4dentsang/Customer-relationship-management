package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.exception.TokenRefreshException;
import com.b2b.b2b.modules.auth.entity.RefreshToken;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.repository.RefreshTokenRepository;
import com.b2b.b2b.modules.auth.repository.UserRepository;
import com.b2b.b2b.modules.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "token", token));
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Integer userId, Integer activeOrgId) {
        RefreshToken refreshToken = new RefreshToken();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        refreshToken.setUser(user);
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
    @Transactional
    public void logoutAllSession(Integer userId) {
        refreshTokenRepository.deleteAllByUser_UserId(userId);
        log.info("All sessions revoked for User ID: {}", userId);
    }

    @Override
    public void updateActiveOrg(Integer userId, Integer activeOrgId) {
        RefreshToken token = refreshTokenRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "userId", userId));

        token.setCurrentActiveOrgId(activeOrgId);
        refreshTokenRepository.save(token);
    }
}
