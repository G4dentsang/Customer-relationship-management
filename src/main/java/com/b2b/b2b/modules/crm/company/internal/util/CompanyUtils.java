package com.b2b.b2b.modules.crm.company.internal.util;

import com.b2b.b2b.modules.crm.company.internal.entity.Company;
import com.b2b.b2b.modules.crm.company.internal.dto.CompanyResponseDTO;
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

