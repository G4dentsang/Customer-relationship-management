package com.b2b.b2b.modules.crm.contact.internal.repository;

import com.b2b.b2b.modules.crm.contact.internal.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

  //TO DELETE
public interface ContactRepository extends JpaRepository<Contact, Integer>, JpaSpecificationExecutor<Contact> {
    Page<Contact> findAllByCompanyId(Integer id, Pageable pageable);
}
