package com.b2b.b2b.modules.crm.contact.internal.service;

import com.b2b.b2b.modules.crm.contact.internal.dto.ContactRequestDTO;
import com.b2b.b2b.modules.crm.contact.internal.dto.ContactFilterDTO;
import com.b2b.b2b.modules.crm.contact.internal.dto.ContactResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ContactService {
    ContactResponseDTO  create(ContactRequestDTO request);
    ContactResponseDTO  update(Integer id, ContactRequestDTO request);
    void  delete(Integer id);
    ContactResponseDTO get(Integer id);
    Page<ContactResponseDTO> getContacts(ContactFilterDTO filter, Pageable pageable);
    Page<ContactResponseDTO> getCompanyContacts(Integer id, Pageable pageable);
}
