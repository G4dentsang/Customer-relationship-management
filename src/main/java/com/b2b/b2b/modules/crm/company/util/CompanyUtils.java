package com.b2b.b2b.modules.crm.company.util;

import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CompanyUtils {
    public CompanyResponseDTO createCompanyResponse(Company company) {
        return new CompanyResponseDTO(
                company.getId(),
                company.getCompanyName(),
                company.getWebsite(),
                company.getIndustry()
        );
    }
}

