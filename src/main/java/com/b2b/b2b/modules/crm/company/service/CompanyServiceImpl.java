package com.b2b.b2b.modules.crm.company.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.company.util.CompanyUtils;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;
    private final CompanyUtils companyUtils;


    @Override
    public CompanyResponseDTO create(CompanyDTO request, User user) {

        Organization org = getOrg(user);
        if (companyRepository.existsByCompanyNameAndOrganization(request.getCompanyName(), org)) {
            throw new APIException("Company with name " + request.getCompanyName() + " already exists");
        }

        Company company = convertToEntity(request, org);
        Company savedCompany = companyRepository.save(company);

        return companyUtils.createCompanyResponse(savedCompany);
    }

    @Override
    public List<CompanyResponseDTO> listAll(User user) {
        List<Company> companiesList = companyRepository.findAllByOrganization(getOrg(user));
        return toDTOList(companiesList);
    }

    @Override
    public CompanyResponseDTO getById(Integer id, User user) {
        Company company = companyRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return companyUtils.createCompanyResponse(company);
    }

    @Override
    public CompanyResponseDTO update(Integer id, CompanyDTO request, User user) {
        Company company = companyRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        updateDtoToEntity(request, company);
        return companyUtils.createCompanyResponse(companyRepository.save(company));
    }

    @Override
    public CompanyResponseDTO delete(Integer id, User user) {
        Company company = companyRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        company.setIsDeleted(true); //soft delete
        return companyUtils.createCompanyResponse(companyRepository.save(company));
    }

    /********Helper methods********/

    private Organization getOrg(User user) {
        return authUtil.getPrimaryOrganization(user);
    }

    private Company convertToEntity(CompanyDTO request, Organization org) {
        Company company = modelMapper.map(request, Company.class);
        company.setOrganization(org);
        return company;
    }

    private List<CompanyResponseDTO> toDTOList(List<Company> companies) {
        return companies.stream()
                .map(companyUtils::createCompanyResponse).toList();
    }

    private void updateDtoToEntity(CompanyDTO request, Company company) {
        if (request.getCompanyName() != null) company.setCompanyName(request.getCompanyName());
        if (request.getCompanyWebsite() != null) company.setWebsite(request.getCompanyWebsite());
        if (request.getCompanyIndustry() != null) company.setIndustry(request.getCompanyIndustry());// need "" string at least
    }

}
