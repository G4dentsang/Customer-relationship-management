package com.b2b.b2b.modules.auth.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Integer>
{
    boolean existsByOrganizationName(String organizationName);
}
