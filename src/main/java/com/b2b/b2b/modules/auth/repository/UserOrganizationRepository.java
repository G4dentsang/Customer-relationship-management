package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.entity.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Integer> {
    boolean existsByUser(User user);
    boolean existsByUser_UserIdAndOrganization_OrganizationId(Integer userId, Integer organizationId);
    List<UserOrganization> findByOrganization_OrganizationId(Integer organizationID);
    Optional<UserOrganization> findByUser_UserIdAndOrganization_OrganizationId(Integer userId, Integer organizationId);
}
