package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;

import java.util.List;

public interface ContactService {
    ContactResponseDTO  add(ContactDTO request);
    ContactResponseDTO  update(Integer id,ContactDTO request);
    void  delete(Integer id);
    ContactResponseDTO get(Integer id);
    List<ContactResponseDTO> getContacts();
    List<ContactResponseDTO> getCompanyContacts(Integer id);
}
