package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long>
{
    EmailVerificationToken findByToken(String token);
}
