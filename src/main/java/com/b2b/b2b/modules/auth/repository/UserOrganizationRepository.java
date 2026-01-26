package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.entity.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Integer>, JpaSpecificationExecutor<UserOrganization> {
    boolean existsByUser(User user);
    boolean existsByUser_UserId(Integer userId);
    Optional<UserOrganization> findByIsAccountOwnerTrue();
    Optional<UserOrganization> findByUser_UserId(Integer userId);
}
