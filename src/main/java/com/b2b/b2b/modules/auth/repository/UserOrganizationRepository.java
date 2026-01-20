package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.entity.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Integer> {
    boolean existsByUser(User user);
    boolean existsByUser_UserId(Integer userId);
    Optional<UserOrganization> findByIsAccountOwnerTrue();
    Optional<UserOrganization> findByUser_UserId(Integer userId);
}
