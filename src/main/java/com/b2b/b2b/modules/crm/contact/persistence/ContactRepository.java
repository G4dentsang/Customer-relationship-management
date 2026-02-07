package com.b2b.b2b.modules.crm.contact.persistence;

import com.b2b.b2b.modules.crm.contact.model.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ContactRepository extends JpaRepository<Contact, Integer>, JpaSpecificationExecutor<Contact> {
    Page<Contact> findAllByCompanyId(Integer id, Pageable pageable);
}
