package com.b2b.b2b.modules.organization.persistence;

import com.b2b.b2b.modules.organization.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Integer>
{
    boolean existsByOrganizationName(String organizationName);
}
