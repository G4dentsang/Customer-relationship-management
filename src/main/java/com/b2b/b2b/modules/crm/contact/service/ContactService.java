package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactFilterDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ContactService {
    ContactResponseDTO  add(ContactDTO request);
    ContactResponseDTO  update(Integer id,ContactDTO request);
    void  delete(Integer id);
    ContactResponseDTO get(Integer id);
    Page<ContactResponseDTO> getContacts(ContactFilterDTO filter, Pageable pageable);
    Page<ContactResponseDTO> getCompanyContacts(Integer id, Pageable pageable);
}
