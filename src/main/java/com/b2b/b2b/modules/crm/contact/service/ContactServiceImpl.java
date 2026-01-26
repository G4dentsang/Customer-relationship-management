package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.contact.entity.Contact;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactFilterDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.repository.ContactRepository;
import com.b2b.b2b.modules.crm.contact.util.ContactSpecifications;
import com.b2b.b2b.modules.crm.contact.util.ContactUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    public Page<ContactResponseDTO> getContacts(ContactFilterDTO filter, Pageable pageable) {
        Specification<Contact> spec = ContactSpecifications.createSearch(filter);
        return helpers.toDTOList(contactRepository.findAll(spec,pageable));
    }

    @Override
    public Page<ContactResponseDTO> getCompanyContacts(Integer id, Pageable pageable) {
        return helpers.toDTOList(contactRepository.findAllByCompanyId(id, pageable));
    }
}
