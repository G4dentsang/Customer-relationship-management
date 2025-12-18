package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.payloads.OrganizationDTO;
import com.b2b.b2b.modules.auth.repository.UserRepository;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.lead.util.LeadUtils;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadServiceImpl implements LeadService {
    private final LeadRepository leadRepository;
    private final ModelMapper modelMapper;
    private final CompanyRepository companyRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final PipelineService pipelineService;
    private final LeadUtils leadUtils;
    private final UserRepository userRepository;

    public LeadServiceImpl(LeadRepository leadRepository, ModelMapper modelMapper, CompanyRepository companyRepository,
                           DomainEventPublisher domainEventPublisher, PipelineService pipelineService, LeadUtils leadUtils, UserRepository userRepository) {
        this.leadRepository = leadRepository;
        this.modelMapper = modelMapper;
        this.companyRepository = companyRepository;
        this.domainEventPublisher = domainEventPublisher;
        this.pipelineService = pipelineService;
        this.leadUtils = leadUtils;
        this.userRepository = userRepository;
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
    pipelineService.assignDefaultPipeline(savedLead);
    domainEventPublisher.publishEvent(new LeadCreatedEvent(savedLead));
    return leadUtils.createLeadResponseDTO(savedLead);
    }


    @Override
    public void updateLead(Integer leadId, CreateLeadRequestDTO createLeadRequestDTO) {
      //  Lead lead = leadRepository.findById(leadId).orElseThrow();
    }

    @Override //all leads including user owned leads
    public List<LeadResponseDTO> getAllOrganizationLeads(User user) {
        Organization organization = user.getUserOrganizations()
                .stream()
                .filter((userOrganization -> userOrganization.isPrimary()))
                .findFirst()
                .orElseThrow(()-> new APIException("User has no primary organization"))
                .getOrganization();
        List<Lead> leads = leadRepository.findAllByOrganizationOrganizationId(organization.getOrganizationId());
        return leads.stream()
                .map(lead -> leadUtils.createLeadResponseDTO(lead)).toList();
    }

    @Override
    public List<LeadResponseDTO> getAllUserOwnedLeads(User user) {
        List<Lead> leads = leadRepository.findAllByOwnerUserId(user.getUserId());
        return leads.stream()
                .map(lead -> leadUtils.createLeadResponseDTO(lead)).toList();
    }

    @Override
    public LeadResponseDTO getLeadById(Integer leadId, User user) {
        Lead leadFromDB = leadRepository.findById(leadId).orElseThrow(() -> new APIException("Lead with id " + leadId + " not found"));
        return leadUtils.createLeadResponseDTO(leadFromDB);
    }
}
