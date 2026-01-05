package com.b2b.b2b.modules.crm.contact.repository;

import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.contact.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Optional<Contact> findByIdAndCompanyOrganization(Integer id, Organization org);
    List<Contact> findAllByCompanyOrganization(Organization org);
    List<Contact> findAllContactsByCompanyIdAndCompanyOrganization(Integer id, Organization org);
}
