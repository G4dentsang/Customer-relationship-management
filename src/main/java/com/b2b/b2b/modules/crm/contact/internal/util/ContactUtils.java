package com.b2b.b2b.modules.crm.contact.internal.util;

import com.b2b.b2b.modules.crm.contact.internal.entity.Contact;
import com.b2b.b2b.modules.crm.contact.internal.dto.ContactResponseDTO;
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
