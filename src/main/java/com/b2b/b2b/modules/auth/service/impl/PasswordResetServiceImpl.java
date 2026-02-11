package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.modules.auth.exception.AccountLockedException;
import com.b2b.b2b.modules.auth.exception.InvalidTokenException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.exception.TokenExpiredException;
import com.b2b.b2b.modules.auth.service.AuthMailService;
import com.b2b.b2b.modules.user.model.PasswordResetToken;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.user.persistence.UserRepository;
import com.b2b.b2b.modules.auth.repository.PasswordResetTokenRepository;
import com.b2b.b2b.modules.auth.service.LoginAttemptService;
import com.b2b.b2b.modules.auth.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService
{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthMailService authMailService;
    private final LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        User userDB = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        if (!userDB.isAccountNonLocked()) {
            if (loginAttemptService.isAccUnlockedWhenTimeExpired(userDB)) {
                log.info("Account unlocked for user: {} ", userDB.getEmail());
            } else {
                throw new AccountLockedException("Account is locked. Try again in 15 min");
            }
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(userDB);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(resetToken);
        authMailService.sendResetPasswordEmail(userDB.getEmail(), token);
    }


    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset link."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Reset link expired. Please request a new one.");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
    }
}
