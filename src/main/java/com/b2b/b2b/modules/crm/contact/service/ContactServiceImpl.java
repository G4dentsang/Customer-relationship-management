package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.contact.entity.Contact;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.repository.ContactRepository;
import com.b2b.b2b.modules.crm.contact.util.ContactUtils;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final AuthUtil authUtil;
    private final ContactUtils contactUtils;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public ContactResponseDTO addContact(ContactDTO request, User user) {

        Company company = companyRepository.findByCompanyNameAndOrganization(request.getCompanyName(), getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Company", "name", request.getCompanyName()));
        Contact contact = convertToEntity(request, company);

        Contact savedContact = contactRepository.save(contact);
        return contactUtils.createContactResponseDTO(savedContact);
    }

    @Override
    public ContactResponseDTO getContact(Integer id, User user) {
        return contactUtils.createContactResponseDTO(contactRepository.findByIdAndCompanyOrganization(id, getOrg(user)));
    }

    @Override
    public List<ContactResponseDTO> getAllContacts(User user) {
        return toDTOList(contactRepository.findAllByCompanyOrganization(getOrg(user)));
    }

    @Override
    public List<ContactResponseDTO> getCompanyContacts(Integer id, User user) {
        return toDTOList(contactRepository.findAllContactsByCompanyIdAndCompanyOrganization(id, getOrg(user)));
    }

    @Override
    public ContactResponseDTO updateContact(ContactDTO request, User user) {
        return null;
    }

    @Override
    public ContactResponseDTO deleteContact(ContactDTO request, User user) {
        return null;
    }

    /********Helper methods********/

    private Organization getOrg(User user) {
        return authUtil.getPrimaryOrganization(user);
    }

    private List<ContactResponseDTO> toDTOList(List<Contact> contacts) {
        return contacts.stream()
                .map(contactUtils::createContactResponseDTO).toList();
    }

    private Contact convertToEntity(ContactDTO request, Company company) {
        Contact contact = modelMapper.map(request, Contact.class);
        contact.setCompany(company);
        return contact;
    }
}
