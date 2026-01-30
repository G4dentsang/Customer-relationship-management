package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.RefreshToken;
import com.b2b.b2b.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>
{
    Optional<RefreshToken> findByToken(String refreshToken);
    Optional<RefreshToken> findByUser_UserId(Integer userId);
    void deleteAllByUser_UserId(Integer userId);
    @Modifying
    int deleteByUser(User user); // revocation/delete
}
