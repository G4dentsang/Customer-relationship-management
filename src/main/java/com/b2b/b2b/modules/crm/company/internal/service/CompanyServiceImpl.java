package com.b2b.b2b.modules.crm.company.internal.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.organization.persistence.OrganizationRepository;
import com.b2b.b2b.modules.crm.company.internal.entity.Company;
import com.b2b.b2b.modules.crm.company.internal.dto.CompanyDTO;
import com.b2b.b2b.modules.crm.company.internal.dto.CompanyFilterDTO;
import com.b2b.b2b.modules.crm.company.internal.dto.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.company.internal.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.company.internal.util.CompanySpecifications;
import com.b2b.b2b.modules.crm.company.internal.util.CompanyUtils;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyUtils companyUtils;
    private final OrganizationRepository organizationRepository;
    private final Helpers helpers;


    @Override
    public CompanyResponseDTO create(CompanyDTO request) {
        Integer orgId = OrganizationContext.getOrgId();
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        if (companyRepository.existsByCompanyName(request.getCompanyName())) {
            throw new APIException("Company with name " + request.getCompanyName() + " already exists");
        }

        Company company = helpers.convertToEntity(request, org);
        Company savedCompany = companyRepository.save(company);

        return companyUtils.createCompanyResponse(savedCompany);
    }

    @Override
    public Page<CompanyResponseDTO> listAll(CompanyFilterDTO filter, Pageable pageable) {
        Specification<Company> spec = CompanySpecifications.createSearch(filter);
        return helpers.toDTOList(companyRepository.findAll(spec, pageable));
    }

    @Override
    public CompanyResponseDTO getById(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return companyUtils.createCompanyResponse(company);
    }

    @Override
    public CompanyResponseDTO update(Integer id, CompanyDTO request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        helpers.updateDtoToEntity(request, company);
        return companyUtils.createCompanyResponse(companyRepository.save(company));
    }

    @Override
    public CompanyResponseDTO delete(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        company.setIsDeleted(true); //soft delete
        return companyUtils.createCompanyResponse(companyRepository.save(company));
    }
}
