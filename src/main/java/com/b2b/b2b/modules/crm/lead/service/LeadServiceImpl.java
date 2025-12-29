package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.payloads.UpdateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.lead.util.LeadUtils;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageService;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import com.b2b.b2b.modules.workflow.events.LeadStatusUpdatedEvent;
import com.b2b.b2b.shared.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final ModelMapper modelMapper;
    private final CompanyRepository companyRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final PipelineService pipelineService;
    private final LeadUtils leadUtils;
    private final PipelineStageService pipelineStageService;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public LeadResponseDTO create(CreateLeadRequestDTO request, User user) {

        Organization org = authUtil.getPrimaryOrganization(user);
        Company company = getOrCreateCompany(request, org);
        Lead lead = convertToEntity(request, org, company);

        pipelineService.assignDefaultPipeline(lead, PipelineType.LEAD);

        Lead savedLead = leadRepository.save(lead);
        domainEventPublisher.publishEvent(new LeadCreatedEvent(savedLead));
        return leadUtils.createLeadResponseDTO(savedLead);
    }

    @Override
    @Transactional
    public LeadResponseDTO update(Integer leadId, UpdateLeadRequestDTO request, User user) {

        Lead lead = leadRepository.findByIdAndOrganization(leadId, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        LeadStatus oldStatus = lead.getLeadStatus();
        mapUpdateDtoToEntity(request, lead);
        LeadStatus newStatus = request.getLeadStatus();

        if (!oldStatus.equals(newStatus)) {
            lead.setLeadStatus(request.getLeadStatus());
            pipelineStageService.promoteToNextStage(lead);
            domainEventPublisher.publishEvent(new LeadStatusUpdatedEvent(lead, oldStatus, newStatus));
            log.info("lead status has been promoted to next pipeline stage");
        }

        Lead savedLead = leadRepository.save(lead);
        return leadUtils.createLeadResponseDTO(savedLead);
    }


    @Override
    public List<LeadResponseDTO> findAllByOrganization(User user) {
        return toDTOList(leadRepository.findAllByOrganization(getOrg(user)));
    }

    @Override
    public List<LeadResponseDTO> findAllByUser(User user) {
        return toDTOList(leadRepository.findAllByOwnerAndOrganization(user,getOrg(user)));
    }

    @Override
    public LeadResponseDTO getById(Integer leadId, User user) {
        Lead lead = leadRepository.findByIdAndOrganization(leadId, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));
        return leadUtils.createLeadResponseDTO(lead);
    }

    private Organization getOrg(User user){
        return authUtil.getPrimaryOrganization(user);
    }

    /********Helper methods********/

    private Lead convertToEntity(CreateLeadRequestDTO request, Organization organization, Company company) {
        Lead lead = modelMapper.map(request, Lead.class);
        lead.setOrganization(organization);
        lead.setCompany(company);
        return lead;
    }

    private Company getOrCreateCompany(CreateLeadRequestDTO request, Organization org) {
        return companyRepository.findByCompanyNameAndOrganization(request.getCompanyName(), org)
                .orElseGet(() -> companyRepository.save(new Company(
                        request.getCompanyName(),
                        request.getWebsite(),
                        request.getIndustry(),
                        org
                )));    //might add this to company side
    }

    private void mapUpdateDtoToEntity(UpdateLeadRequestDTO request, Lead lead) {
        lead.setLeadName(request.getLeadName());
        lead.setLeadEmail(request.getLeadEmail());
        lead.setLeadPhone(request.getLeadPhone());
    }

    private List<LeadResponseDTO> toDTOList(List<Lead> leads) {
        return leads.stream()
                .map(leadUtils::createLeadResponseDTO).toList();
    }
}
