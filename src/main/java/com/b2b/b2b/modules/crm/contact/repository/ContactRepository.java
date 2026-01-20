package com.b2b.b2b.modules.crm.contact.repository;

import com.b2b.b2b.modules.crm.contact.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    List<Contact> findAllByCompanyId(Integer id);
}
