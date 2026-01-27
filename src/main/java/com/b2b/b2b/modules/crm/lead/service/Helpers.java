package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.repository.UserRepository;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.payloads.UpdateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.util.LeadUtils;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageService;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import com.b2b.b2b.modules.workflow.events.LeadAssignedEvent;
import com.b2b.b2b.modules.workflow.events.LeadStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
//package-private
class Helpers {
    private final ModelMapper modelMapper;
    private final LeadUtils leadUtils;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final PipelineStageService pipelineStageService;


    Lead convertToEntity(CreateLeadRequestDTO request, Organization organization, Company company) {
        Lead lead = modelMapper.map(request, Lead.class);
        lead.setOrganization(organization);
        lead.setCompany(company);
        return lead;
    }

    void updateDtoToEntity(UpdateLeadRequestDTO request, Lead lead) {
        if (request.getLeadName() != null) lead.setLeadName(request.getLeadName());
        if (request.getLeadEmail() != null) lead.setLeadEmail(request.getLeadEmail());
        if (request.getLeadPhone() != null) lead.setLeadPhone(request.getLeadPhone());// need "" string at least
    }

    Page<LeadResponseDTO> toDTOList(Page<Lead> leads) {
        return leads.map(leadUtils::createLeadResponseDTO);
    }

    Company getOrCreateCompany(CreateLeadRequestDTO request, Organization org) {
        if (request.getCompanyId() != null) {
            return companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.getCompanyId()));
        } else {
            Company company = new Company();
            company.setWebsite(request.getWebsite());
            company.setIndustry(request.getIndustry());
            company.setOrganization(org);
            return companyRepository.save(company);
        }
    }

    void assignUser(UpdateLeadRequestDTO request, Lead lead, User oldOwner) {
        if (request.getOwnerId() != null) {
            User newOwner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOwnerId()));
            if (oldOwner == null || !oldOwner.equals(newOwner)) {
                lead.setAssignedUser(newOwner);
                log.info("Lead {} assigned to {}",
                        lead.getId(), newOwner.getUserName());
                domainEventPublisher.publishEvent(new LeadAssignedEvent(lead, newOwner));
            }
        }
    }

    void processStatusChange(LeadStatus newStatus, LeadStatus oldStatus, Lead lead) {
        lead.setLeadStatus(newStatus);
        log.info("Lead {} status changed from {} to {}",
                lead.getId(), oldStatus, newStatus);
        if (newStatus.getGroupId() == 3) {
            if (newStatus == LeadStatus.CONVERTED) {
                lead.setReadyForConversion(true);
            }
        } else {
            if (newStatus.getGroupId() != oldStatus.getGroupId()) {
                pipelineStageService.promoteToNextStage(lead);
            }

        }
        domainEventPublisher.publishEvent(new LeadStatusUpdatedEvent(lead, oldStatus, newStatus));
    }

}
