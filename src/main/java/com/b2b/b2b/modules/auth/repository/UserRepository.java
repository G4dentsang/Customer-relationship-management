package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>
{
    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUserName(String username);
    Boolean existsByEmail(String email);
}

