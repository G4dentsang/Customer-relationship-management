package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;

import java.util.List;

public interface ContactService {
    ContactResponseDTO  add(ContactDTO request, User user);
    ContactResponseDTO  update(Integer id,ContactDTO request, User user);
    void  delete(Integer id, User user);
    ContactResponseDTO get(Integer id, User user);
    List<ContactResponseDTO> getContacts(User user);
    List<ContactResponseDTO> getCompanyContacts(Integer id, User user);
}
