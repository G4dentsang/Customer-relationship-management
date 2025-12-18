package com.b2b.b2b.modules.crm.company.payloads;

public record CompanyResponseDTO(
        Integer companyId,
        String companyName,
        String companyWebsite,
        String companyIndustry
) {
}
