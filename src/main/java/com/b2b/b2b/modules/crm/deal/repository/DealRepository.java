package com.b2b.b2b.modules.crm.deal.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DealRepository extends JpaRepository<Deal, Integer> {
    List<Deal> findAllByOrganization(Organization org);
    List<Deal> findAllByOwnerAndOrganization(User user, Organization org);
    Optional<Deal> findDealByIdAndOrganization(Integer id, Organization org);
    List<Deal> findAllDealsByCompanyIdAndOrganization(Integer id, Organization org);
    List<Deal> findAllDealsByCompanyContactsIdAndOrganization(Integer id, Organization org);
    List<Deal> findAllByLead(Lead lead);
}

