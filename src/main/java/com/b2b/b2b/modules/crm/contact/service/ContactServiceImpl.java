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
    public ContactResponseDTO add(ContactDTO request, User user) {
        Company company = companyRepository.findByCompanyNameAndOrganization(request.getCompanyName(), getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Company", "name", request.getCompanyName()));

        Contact contact = convertToEntity(request, company);
        return contactUtils.createContactResponseDTO(contactRepository.save(contact));
    }

    @Override
    public ContactResponseDTO get(Integer id, User user) {
        Contact contact = contactRepository.findByIdAndCompanyOrganization(id, getOrg(user))
                        .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
         return contactUtils.createContactResponseDTO(contact);
    }

    @Override
    public List<ContactResponseDTO> getContacts(User user) {
        return toDTOList(contactRepository.findAllByCompanyOrganization(getOrg(user)));
    }

    @Override
    public List<ContactResponseDTO> getCompanyContacts(Integer id, User user) {
        return toDTOList(contactRepository.findAllContactsByCompanyIdAndCompanyOrganization(id, getOrg(user)));
    }

    @Override
    @Transactional
    public ContactResponseDTO update(Integer id, ContactDTO request, User user) {
        Contact existingContact = contactRepository.findByIdAndCompanyOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));

        if(request.getCompanyName() != null) {
            Company company = companyRepository.findByCompanyNameAndOrganization(request.getCompanyName(), getOrg(user))
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "name", request.getCompanyName()));
            existingContact.setCompany(company);
        }
        updateDtoToEntity(request, existingContact);

        return contactUtils.createContactResponseDTO(contactRepository.save(existingContact));
    }


    @Override
    public void delete(Integer id, User user) {
        Contact contact = contactRepository.findByIdAndCompanyOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
        contactRepository.delete(contact);
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

    private void updateDtoToEntity(ContactDTO request, Contact contact) {
        if(request.getFirstName() != null) contact.setFirstName(request.getFirstName());
        if(request.getLastName() != null) contact.setLastName(request.getLastName());
        if(request.getEmail() != null) contact.setEmail(request.getEmail());
        if(request.getPhone() != null) contact.setPhone(request.getPhone());

    }
}
