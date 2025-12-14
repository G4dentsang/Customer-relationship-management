package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.payloads.OrganizationDTO;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class LeadServiceImpl implements LeadService {
    private final LeadRepository leadRepository;
    private final ModelMapper modelMapper;
    private final CompanyRepository companyRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final PipelineService pipelineService;

    public LeadServiceImpl(LeadRepository leadRepository, ModelMapper modelMapper, CompanyRepository companyRepository,
                           DomainEventPublisher domainEventPublisher, PipelineService pipelineService) {
        this.leadRepository = leadRepository;
        this.modelMapper = modelMapper;
        this.companyRepository = companyRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.pipelineService = pipelineService;
    }
    @Override
    public LeadResponseDTO createLead(CreateLeadRequestDTO createLeadRequestDTO, User user) {

    Lead lead = modelMapper.map(createLeadRequestDTO,Lead.class);
    Organization organization = user.getUserOrganizations()
            .stream()
            .filter((userOrganization -> userOrganization.isPrimary()))
            .findFirst()
            .orElseThrow(()-> new APIException("User has no primary organization"))
            .getOrganization();
    Company company = companyRepository.findByCompanyNameAndOrganization(createLeadRequestDTO.getCompanyName(), organization);

    if(company==null){
        company = new Company(createLeadRequestDTO.getCompanyName(), createLeadRequestDTO.getWebsite(),
        createLeadRequestDTO.getIndustry(), organization);
        };
    companyRepository.save(company);

    lead.setLeadName(createLeadRequestDTO.getLeadName());
    lead.setLeadEmail(createLeadRequestDTO.getLeadEmail());
    lead.setLeadPhone(createLeadRequestDTO.getLeadPhone());
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
    pipelineService.assignDefaultPipeline(savedLead);
    domainEventPublisher.publishEvent(new LeadCreatedEvent(savedLead));

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
    public void updateLead(Integer leadId, CreateLeadRequestDTO createLeadRequestDTO) {
      //  Lead lead = leadRepository.findById(leadId).orElseThrow();
    }
}
