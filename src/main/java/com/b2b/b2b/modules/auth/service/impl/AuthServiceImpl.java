package com.b2b.b2b.modules.auth.service.impl;

import com.b2b.b2b.exception.BadRequestException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.exception.InvalidTokenException;
import com.b2b.b2b.modules.auth.exception.RateLimitExceededException;
import com.b2b.b2b.modules.auth.exception.TokenExpiredException;
import com.b2b.b2b.modules.auth.service.AuthMailService;
import com.b2b.b2b.modules.auth.service.AuthService;
import com.b2b.b2b.modules.notification.model.EmailVerificationToken;
import com.b2b.b2b.modules.notification.persistence.EmailVerificationTokenRepository;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService
{
     private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final AuthMailService authMailService;

    @Override
    public void verifyToken(String token) {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("verification-token", "token", token));

        if (emailVerificationToken.isUsed()) {
            throw new InvalidTokenException("This link has already been used. Please log in.");
        }
        if (emailVerificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Verification link expired. Please request a new one.");
        }

        User user = emailVerificationToken.getUser();
        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            user.setUserActive(true);
            userRepository.save(user);
        }

        emailVerificationToken.setUsed(true);
        emailVerificationTokenRepository.save(emailVerificationToken);
    }

    @Override
    public void createAndSendVerificationCode(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setUser(user);
        emailVerificationToken.setUsed(false);
        emailVerificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        emailVerificationTokenRepository.save(emailVerificationToken);

        authMailService.sendVerificationEmail(user.getEmail(), token);

    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        if(user.isEmailVerified()) {
            throw new BadRequestException("This account is already verified. Please log in.");
        }

        Optional<EmailVerificationToken> lastToken = emailVerificationTokenRepository.findFirstByUserOrderByCreatedAtDesc(user);
        if(lastToken.isPresent() && lastToken.get().getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5))) {
            throw new RateLimitExceededException("Please wait 5 minutes before requesting another link.");
        }
        createAndSendVerificationCode(user);
    }

}
