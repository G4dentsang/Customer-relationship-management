package com.b2b.b2b.modules.crm.contact.util;

import com.b2b.b2b.modules.crm.contact.entity.Contact;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import org.springframework.stereotype.Component;
@Component
public class ContactUtils {
    public ContactResponseDTO createContactResponseDTO(Contact contact) {
        return new ContactResponseDTO(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getCompany().getCompanyName()
        );
    }
}
