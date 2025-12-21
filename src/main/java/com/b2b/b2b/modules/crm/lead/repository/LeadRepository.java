package com.b2b.b2b.modules.crm.lead.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Integer> {
    List<Lead> findAllByOrganizationOrganizationId(Integer id);
    List<Lead> findAllByOwnerAndOrganization(User user, Organization organization);
    Lead findByIdAndOrganization(Integer id, Organization organization);

}
