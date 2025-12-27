package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;

import java.util.List;

public interface ContactService {
    ContactResponseDTO  addContact(ContactDTO request, User user);
    ContactResponseDTO  updateContact(ContactDTO request, User user);
    ContactResponseDTO  deleteContact(ContactDTO request, User user);
    ContactResponseDTO getContact(Integer id, User user);
    List<ContactResponseDTO> getAllContacts(User user);
    List<ContactResponseDTO> getCompanyContacts(Integer id, User user);
}
