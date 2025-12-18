package com.b2b.b2b.modules.crm.contact.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.contact.entity.Contacts;
import com.b2b.b2b.modules.crm.contact.payloads.ContactDTO;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;

    public ContactServiceImpl(CompanyRepository companyRepository, ContactRepository contactRepository) {
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
    }

    @Override
    public ContactResponseDTO addContact(ContactDTO contactDTO, User user) {

        Organization organization = user.getUserOrganizations()
         .stream()
         .filter(userOrganization -> userOrganization.isPrimary())
         .findFirst()
         .orElseThrow(() -> new APIException("User has no primary organization"))
         .getOrganization();

        Company companyFromDB = companyRepository.findByCompanyNameAndOrganization(contactDTO.getCompanyName(), organization);
        Contacts contacts = new Contacts();
        contacts.setEmail(contactDTO.getEmail());
        contacts.setFirstName(contactDTO.getFirstName());
        contacts.setLastName(contactDTO.getLastName());
        contacts.setPhone(contactDTO.getPhone());
        contacts.setCompany(companyFromDB);
        Contacts savedContact = contactRepository.save(contacts);

        return new ContactResponseDTO(
                savedContact.getId(),
                savedContact.getFirstName(),
                savedContact.getLastName(),
                savedContact.getEmail(),
                savedContact.getPhone(),
                savedContact.getCompany().getCompanyName()
        );
    }
    @Override
    public ContactResponseDTO getContact(Integer id, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrganization -> userOrganization.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary organization"))
                .getOrganization();
        Contacts contact = contactRepository.findByIdAndOrganization(id, organization);
        return new ContactResponseDTO(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getCompany().getCompanyName()
        );
    }

    @Override
    public List<ContactResponseDTO> getAllContacts(User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrganization -> userOrganization.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary organization"))
                .getOrganization();
        List<Contacts> contactsList = contactRepository.findAllByOrganization(organization);

        return contactsList.stream().map(c-> new ContactResponseDTO(
                c.getId(),
                c.getFirstName(),
                c.getLastName(),
                c.getEmail(),
                c.getPhone(),
                c.getCompany().getCompanyName()
        )).toList();
    }

    @Override
    public ContactResponseDTO updateContact(ContactDTO contactDTO, User user) {
        return null;
    }

    @Override
    public ContactResponseDTO deleteContact(ContactDTO contactDTO, User user) {
        return null;
    }



}
