package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>
{
}

