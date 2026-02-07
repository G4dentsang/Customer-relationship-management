package com.b2b.b2b.modules.user.persistence;

import com.b2b.b2b.modules.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>
{
    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}

