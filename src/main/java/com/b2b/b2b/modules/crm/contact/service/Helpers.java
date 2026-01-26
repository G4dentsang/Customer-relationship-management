package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.contact.entity.Contact;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.util.ContactUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
class Helpers {

    private final ContactUtils contactUtils;
    private final ModelMapper modelMapper;

    Page<ContactResponseDTO> toDTOList(Page<Contact> contacts) {
        return contacts.map(contactUtils::createContactResponseDTO);
    }

    Contact convertToEntity(ContactDTO request, Company company) {
        Contact contact = modelMapper.map(request, Contact.class);
        contact.setCompany(company);
        return contact;
    }

    void updateDtoToEntity(ContactDTO request, Contact contact) {
        if (request.getFirstName() != null) contact.setFirstName(request.getFirstName());
        if (request.getLastName() != null) contact.setLastName(request.getLastName());
        if (request.getEmail() != null) contact.setEmail(request.getEmail());
        if (request.getPhone() != null) contact.setPhone(request.getPhone());

    }
}
