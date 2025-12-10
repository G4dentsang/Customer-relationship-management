package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.payloads.OrganizationDTO;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.payloads.LeadCreateDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class LeadServiceImpl implements LeadService {
    private final LeadRepository leadRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final ModelMapper modelMapper;
    private final CompanyRepository companyRepository;

    public LeadServiceImpl(LeadRepository leadRepository, DomainEventPublisher domainEventPublisher, ModelMapper modelMapper, CompanyRepository companyRepository) {
        this.leadRepository = leadRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.modelMapper = modelMapper;
        this.companyRepository = companyRepository;
    }
    @Override
    public LeadResponseDTO createLead(LeadCreateDTO leadCreateDTO, User user) {

    Lead lead = modelMapper.map(leadCreateDTO,Lead.class);
    Organization organization = user.getUserOrganizations()
            .stream()
            .filter((userOrganization -> userOrganization.isPrimary()))
            .findFirst()
            .orElseThrow(()-> new APIException("User has no primary organization"))
            .getOrganization();
    Company company = companyRepository.findByCompanyNameAndOrganization(leadCreateDTO.getCompanyName(), organization);

    if(company==null){
        company = new Company(leadCreateDTO.getCompanyName(),leadCreateDTO.getWebsite(),
        leadCreateDTO.getIndustry(), organization);
        };
    companyRepository.save(company);

    lead.setLeadName(leadCreateDTO.getLeadName());
    lead.setLeadEmail(leadCreateDTO.getLeadEmail());
    lead.setLeadPhone(leadCreateDTO.getLeadPhone());
    lead.setOrganization(organization);
    lead.setCompany(company);
    Lead savedLead = leadRepository.save(lead);

    OrganizationDTO orgDTO = new  OrganizationDTO(
            savedLead.getOrganization().getOrganizationName(),
            savedLead.getOrganization().getCreatedAt()

    );
    CompanyDTO companyDTO = new  CompanyDTO(
            savedLead.getCompany().getCompanyName(),
            savedLead.getCompany().getWebsite(),
            savedLead.getCompany().getIndustry()
    );
    //Async
    domainEventPublisher.publishEvent(new LeadCreatedEvent(savedLead, savedLead.getId()));
    //event listener later after workflow rules and conditions added*************

    return new LeadResponseDTO(
            savedLead.getId(),
            savedLead.getLeadName(),
            savedLead.getLeadEmail(),
            savedLead.getLeadPhone(),
            savedLead.getLeadStatus(),
            savedLead.getCreatedAt(),
            orgDTO,
            companyDTO
    );

    }


    @Override
    public void updateLead(Integer leadId, LeadCreateDTO leadCreateDTO) {
      //  Lead lead = leadRepository.findById(leadId).orElseThrow();
    }
}
