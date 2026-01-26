package com.b2b.b2b.modules.crm.contact.repository;

import com.b2b.b2b.modules.crm.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ContactRepository extends JpaRepository<Contact, Integer>, JpaSpecificationExecutor<Contact> {
    Page<Contact> findAllByCompanyId(Integer id, Pageable pageable);
}
