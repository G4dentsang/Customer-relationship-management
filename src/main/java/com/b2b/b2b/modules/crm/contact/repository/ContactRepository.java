package com.b2b.b2b.modules.crm.contact.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.contact.entity.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contacts, Integer> {

    @Query("SELECT c FROM Contacts c WHERE c.company.organization = :organization AND c.id = :id")
    Contacts findByIdAndOrganization(Integer id, Organization organization);

    @Query("SELECT c FROM Contacts c WHERE c.company.organization = :organization")
    List<Contacts> findAllByOrganization(Organization organization);
    @Query("SELECT c FROM Contacts c WHERE c.company.organization = :organization AND c.company.id = :companyId")
    List<Contacts> findAllContactsByCompanyIdAndOrganization(Integer companyId, Organization organization);
}
