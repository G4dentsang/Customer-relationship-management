package com.b2b.b2b.modules.crm.lead.internal.application.service;

import com.b2b.b2b.exception.BadRequestException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.lead.api.event.LeadCreatedEvent;
import com.b2b.b2b.modules.crm.lead.api.event.LeadDeletedEvent;
import com.b2b.b2b.modules.crm.lead.api.event.LeadPipelineStageChangeEvent;
import com.b2b.b2b.modules.crm.lead.api.event.LeadStatusUpdatedEvent;
import com.b2b.b2b.modules.crm.lead.internal.application.port.in.LeadCommandUseCase;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto.*;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.organization.persistence.OrganizationRepository;
import com.b2b.b2b.modules.user.persistence.UserRepository;
import com.b2b.b2b.modules.crm.company.internal.entity.Company;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadStatus;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadRepository;
import com.b2b.b2b.modules.crm.lead.internal.util.LeadUtils;
import com.b2b.b2b.modules.crm.pipeline.model.LeadPipeline;
import com.b2b.b2b.modules.crm.pipeline.service.LeadPipelineService;
import com.b2b.b2b.modules.crm.pipelineStage.model.LeadPipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.persistence.LeadPipelineStageRepository;
import com.b2b.b2b.modules.crm.pipelineStage.service.LeadPipelineStageService;
import com.b2b.b2b.shared.DomainEventPublisher;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class LeadCommandServiceImpl implements LeadCommandUseCase {

    private final LeadRepository leadRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final LeadUtils leadUtils;
    private final OrganizationRepository organizationRepository;
    private final Helpers helpers;
    private final UserRepository userRepository;
    private final LeadPipelineStageRepository pipelineStageRepository;
    private final LeadPipelineService leadPipelineService;
    private final LeadPipelineStageService leadPipelineStageService;


    @Override
    @Transactional
    public LeadResponseDTO create(CreateLeadRequestDTO request) {
        Integer orgId = OrganizationContext.getOrgId();

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));
        Company company = helpers.getOrCreateCompany(request, org);

        Lead lead = helpers.convertToEntity(request, org, company);

        if(request.getAssignedUserId() == null) {
            lead.setAssignedUser(null);
        }
        User user = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getAssignedUserId()));

        lead.setAssignedUser(user);
        // ---- minimum one pipeline will be assigned to every new lead
        LeadPipeline defaultPipeline = leadPipelineService.assignDefaultPipeline(lead);
        leadPipelineStageService.assignDefaultStage(defaultPipeline,lead);

        Lead savedLead = leadRepository.save(lead);

        domainEventPublisher.publishEvent(new LeadCreatedEvent(savedLead));

        return leadUtils.createLeadResponseDTO(savedLead);
    }

    @Override
    @Transactional
    public LeadResponseDTO update(Integer leadId, UpdateLeadRequestDTO request) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        User oldOwner = lead.getAssignedUser();

        helpers.updateDtoToEntity(request, lead);
        helpers.assignUser(request, lead, oldOwner);

        return leadUtils.createLeadResponseDTO(leadRepository.save(lead));
    }

    @Override
    @Transactional
    public LeadResponseDTO updateStatus(Integer leadId, LeadUpdateStatusRequestDTO request) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        LeadStatus oldStatus = lead.getLeadStatus();
        lead.setLeadStatus(LeadStatus.LOST);
        lead.setLossReason(request.getLossReason());

        domainEventPublisher.publishEvent(new LeadStatusUpdatedEvent(lead, oldStatus, LeadStatus.LOST));

        return leadUtils.createLeadResponseDTO(leadRepository.save(lead));
    }

    @Override
    @Transactional
    public LeadResponseDTO changeStage(Integer leadId, ChangeStageRequestDTO request) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));
        LeadPipelineStage destinationStage = pipelineStageRepository.findById(request.getDestinationStageId())
                .orElseThrow(() -> new ResourceNotFoundException("Stage", "id", request.getDestinationStageId()));
        LeadPipelineStage oldStage = lead.getPipelineStage();

        if (!lead.getPipeline().getId().equals(oldStage.getPipeline().getId())) {
            throw new BadRequestException("Lead Pipeline Stage mismatch: Cannot move across pipelines");
        }

        lead.setPipelineStage(destinationStage);
        //Avoiding memory loop -> DB Query is used
        Integer maxOrder = pipelineStageRepository.findMaxOrder(lead.getPipeline().getId());

        if (destinationStage.getStageOrder().equals(maxOrder)) {
            lead.setLeadStatus(LeadStatus.READY_FOR_CONVERSION);
            lead.setReadyForConversion(true);
        } else {
            lead.setLeadStatus(destinationStage.getMappedStatus());
            lead.setReadyForConversion(false);
        }

        Lead savedLead = leadRepository.save(lead);
        domainEventPublisher.publishEvent(new LeadPipelineStageChangeEvent(lead, oldStage, destinationStage));

        return leadUtils.createLeadResponseDTO(savedLead);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
        log.info("Initiating GDPR-compliant erasure for Lead ID: {}", id);

        lead.setLeadName("GDPR_ERASED_" + id);
        lead.setLeadEmail(null);
        lead.setLeadPhone(null);
        lead.setLeadStatus(LeadStatus.SOFT_DELETED);

        domainEventPublisher.publishEvent(new LeadDeletedEvent(lead));
        leadRepository.save(lead);
        log.info("Lead {} has been successfully anonymized. Associated deals preserved.", id);
    }

}
