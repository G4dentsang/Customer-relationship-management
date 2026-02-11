package com.b2b.b2b.modules.crm.contact.internal.service;

import com.b2b.b2b.modules.crm.company.internal.entity.Company;
import com.b2b.b2b.modules.crm.contact.internal.entity.Contact;
import com.b2b.b2b.modules.crm.contact.internal.dto.ContactRequestDTO;
import com.b2b.b2b.modules.crm.contact.internal.dto.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.internal.util.ContactUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component("contactHelpers")
@RequiredArgsConstructor
class Helpers {

    private final ContactUtils contactUtils;
    private final ModelMapper modelMapper;

    Page<ContactResponseDTO> toDTOList(Page<Contact> contacts) {
        return contacts.map(contactUtils::createContactResponseDTO);
    }

    Contact convertToEntity(ContactRequestDTO request, Company company) {
        Contact contact = modelMapper.map(request, Contact.class);
        contact.setCompany(company);
        return contact;
    }

    void updateDtoToEntity(ContactRequestDTO request, Contact contact) {
        if (request.getFirstName() != null) contact.setFirstName(request.getFirstName());
        if (request.getLastName() != null) contact.setLastName(request.getLastName());
        if (request.getEmail() != null) contact.setEmail(request.getEmail());
        if (request.getPhone() != null) contact.setPhone(request.getPhone());

    }
}
