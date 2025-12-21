package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;

import java.util.List;

public interface ContactService {
    ContactResponseDTO  addContact(ContactDTO contactDTO, User user);
    ContactResponseDTO  updateContact(ContactDTO contactDTO, User user);
    ContactResponseDTO  deleteContact(ContactDTO contactDTO, User user);
    ContactResponseDTO getContact(Integer id, User user);
    List<ContactResponseDTO> getAllContacts(User user);
    List<DealResponseDTO> getDealsByContact(Integer contactId,User user);
}
