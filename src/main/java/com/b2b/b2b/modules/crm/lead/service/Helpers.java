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
import org.springframework.stereotype.Component;

import java.util.List;

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

    List<LeadResponseDTO> toDTOList(List<Lead> leads) {
        return leads.stream()
                .map(leadUtils::createLeadResponseDTO).toList();
    }

    Company getOrCreateCompany(CreateLeadRequestDTO request, Organization org) {
        return companyRepository.findByCompanyName(request.getCompanyName())
                .orElseGet(() -> companyRepository.save(new Company(
                        request.getCompanyName(),
                        request.getWebsite(),
                        request.getIndustry(),
                        org
                )));    //might add this to company side
    }

    void assignUser(UpdateLeadRequestDTO request, Lead lead, User oldOwner) {
        if (request.getOwner().getUserId() != null) {
            User newOwner = userRepository.findById(request.getOwner().getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOwner().getUserId()));
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
