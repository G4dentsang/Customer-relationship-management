package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.EmailVerificationToken;
import com.b2b.b2b.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long>
{
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findFirstByUserOrderByCreatedAtDesc(User user);
}
