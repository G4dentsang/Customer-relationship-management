package com.b2b.b2b.modules.crm.company.internal.service;

import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.crm.company.internal.entity.Company;
import com.b2b.b2b.modules.crm.company.internal.dto.CompanyDTO;
import com.b2b.b2b.modules.crm.company.internal.dto.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.company.internal.util.CompanyUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component("companyHelpers")
@RequiredArgsConstructor
class Helpers {
    private final ModelMapper modelMapper;
    private final CompanyUtils companyUtils;

    Company convertToEntity(CompanyDTO request, Organization org) {
        Company company = modelMapper.map(request, Company.class);
        company.setOrganization(org);
        return company;
    }

    Page<CompanyResponseDTO> toDTOList(Page<Company> companies) {
        return companies.map(companyUtils::createCompanyResponse);
    }

    void updateDtoToEntity(CompanyDTO request, Company company) {
        if (request.getCompanyName() != null) company.setCompanyName(request.getCompanyName());
        if (request.getCompanyWebsite() != null) company.setWebsite(request.getCompanyWebsite());
        if (request.getCompanyIndustry() != null)
            company.setIndustry(request.getCompanyIndustry());// need "" string at least
    }

}
