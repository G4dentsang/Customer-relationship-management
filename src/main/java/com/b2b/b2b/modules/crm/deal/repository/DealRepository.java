package com.b2b.b2b.modules.crm.deal.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.entity.Deals;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DealRepository extends JpaRepository<Deals, Integer> {
    List<Deals> findAllByOrganization(Organization organization);
    List<Deals> findAllByOwnerAndOrganization(User user, Organization organization);
    Deals findDealByIdAndOrganization(Integer dealId, Organization organization);
    List<Deals> findAllDealsByCompanyIdAndOrganization(Integer companyId, Organization organization);
    List<Deals> findAllDealsByCompanyContactsIdAndOrganization(Integer contactId, Organization organization);
}

