package com.b2b.b2b.modules.organization.persistence;

import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.organization.model.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Integer>, JpaSpecificationExecutor<UserOrganization> {
    boolean existsByUser(User user);
    boolean existsByUserAndOrganization(User user, Organization organization);
    boolean existsByUser_UserIdAndOrganization(Integer userId,  Organization organization);
    boolean existsByUserAndIsDefaultHomeTrue(User user);
    Optional<UserOrganization> findByOrganization_OrganizationIdAndUser_UserId(Integer organizationId, Integer userId);
    Optional<UserOrganization> findByUser_UserIdAndOrganization(Integer userId, Organization organization);
}
