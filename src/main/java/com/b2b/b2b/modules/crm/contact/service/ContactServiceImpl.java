package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.contact.entity.Contact;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.repository.ContactRepository;
import com.b2b.b2b.modules.crm.contact.util.ContactUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final ContactUtils contactUtils;
    private final Helpers helpers;


    @Override
    @Transactional
    public ContactResponseDTO add(ContactDTO request) {
        Company company = companyRepository.findByCompanyName(request.getCompanyName())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "name", request.getCompanyName()));

        Contact contact = helpers.convertToEntity(request, company);
        return contactUtils.createContactResponseDTO(contactRepository.save(contact));
    }

    @Override
    @Transactional
    public ContactResponseDTO update(Integer id, ContactDTO request) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));

        if(request.getCompanyName() != null) {
            Company company = companyRepository.findByCompanyName(request.getCompanyName())
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "name", request.getCompanyName()));
            existingContact.setCompany(company);
        }
        helpers.updateDtoToEntity(request, existingContact);

        return contactUtils.createContactResponseDTO(contactRepository.save(existingContact));
    }

    @Override
    public void delete(Integer id) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
        contactRepository.delete(contact);
    }

    @Override
    public ContactResponseDTO get(Integer id) {
        Contact contact = contactRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", id));
         return contactUtils.createContactResponseDTO(contact);
    }

    @Override
    public List<ContactResponseDTO> getContacts() {
        return helpers.toDTOList(contactRepository.findAll());
    }

    @Override
    public List<ContactResponseDTO> getCompanyContacts(Integer id) {
        return helpers.toDTOList(contactRepository.findAllByCompanyId(id));
    }
}
