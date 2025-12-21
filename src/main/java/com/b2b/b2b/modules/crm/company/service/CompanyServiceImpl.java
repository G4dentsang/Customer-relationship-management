package com.b2b.b2b.modules.crm.company.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.contact.entity.Contacts;
import com.b2b.b2b.modules.crm.contact.payloads.ContactResponseDTO;
import com.b2b.b2b.modules.crm.contact.repository.ContactRepository;
import com.b2b.b2b.modules.crm.deal.entity.Deals;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.repository.DealRepository;
import com.b2b.b2b.modules.crm.deal.utils.DealUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService
{
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final DealRepository dealRepository;
    private final DealUtils dealUtils;

    public CompanyServiceImpl(CompanyRepository companyRepository, ContactRepository contactRepository, DealRepository dealRepository, DealUtils dealUtils) {
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.dealRepository = dealRepository;
        this.dealUtils = dealUtils;
    }

    @Override
    public CompanyResponseDTO addCompany(CompanyDTO companyDTO, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary Organization"))
                .getOrganization();
        Company company = new Company();
        company.setCompanyName(companyDTO.getCompanyName());
        company.setWebsite(companyDTO.getCompanyWebsite());
        company.setIndustry(companyDTO.getCompanyIndustry());
        company.setOrganization(organization);
        Company savedCompany = companyRepository.save(company);

        return new CompanyResponseDTO(
                savedCompany.getId(),
                savedCompany.getCompanyName(),
                savedCompany.getWebsite(),
                savedCompany.getIndustry()
        ) ;
    }

    @Override
    public List<CompanyResponseDTO> getAllCompanies(User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary Organization"))
                .getOrganization();
        List<Company> companiesList = companyRepository.findAllByOrganization(organization);
        return companiesList.stream().map(company ->
         new CompanyResponseDTO(
                company.getId(),
                 company.getCompanyName(),
                 company.getWebsite(),
                 company.getIndustry()
        )
        ).toList();
    }

    @Override
    public CompanyResponseDTO getCompany(Integer companyId, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary Organization"))
                .getOrganization();
        Company companyFrmDB = companyRepository.findById(companyId).orElseThrow(()-> new APIException("Company Not Found"));
        return new CompanyResponseDTO(
                companyFrmDB.getId(),
                companyFrmDB.getCompanyName(),
                companyFrmDB.getWebsite(),
                companyFrmDB.getIndustry()
        );
    }

    @Override
    public List<ContactResponseDTO> getCompanyContacts(Integer companyId, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary Organization"))
                .getOrganization();
        List<Contacts> companyContacts = contactRepository.findAllContactsByCompanyIdAndOrganization(companyId,organization);
        return companyContacts.stream().map(c -> new ContactResponseDTO(
                c.getId(),
                c.getFirstName(),
                c.getLastName(),
                c.getEmail(),
                c.getPhone(),
                c.getCompany().getCompanyName()
        ) ).toList();
    }

    @Override
    public List<DealResponseDTO> getCompanyDeals(Integer companyId, User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary Organization"))
                .getOrganization();
        List<Deals> companyDeals = dealRepository.findAllDealsByCompanyIdAndOrganization(companyId, organization);
        return companyDeals.stream().map(deals -> dealUtils.createDealResponseDTO(deals)).toList();
    }
}
