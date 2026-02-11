package com.b2b.b2b.modules.crm.contact.internal.dto;


public record ContactResponseDTO(
        Integer id,
        String firstName,
        String  lastName,
        String  email,
        String  phone,
        String companyName
) {
}
