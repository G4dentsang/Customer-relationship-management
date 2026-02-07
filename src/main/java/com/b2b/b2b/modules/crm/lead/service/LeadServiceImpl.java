package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.exception.BadRequestException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.repository.OrganizationRepository;
import com.b2b.b2b.modules.auth.repository.UserRepository;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import com.b2b.b2b.modules.crm.lead.payloads.*;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.lead.util.LeadSpecifications;
import com.b2b.b2b.modules.crm.lead.util.LeadUtils;
import com.b2b.b2b.modules.crm.pipeline.service.LeadPipelineService;
import com.b2b.b2b.modules.crm.pipelineStage.entity.LeadPipelineStage;
import com.b2b.b2b.modules.crm.pipelineStage.repository.LeadPipelineStageRepository;
import com.b2b.b2b.modules.workflow.events.*;
import com.b2b.b2b.shared.AuthUtil;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final LeadUtils leadUtils;
    private final AuthUtil authUtil;
    private final OrganizationRepository organizationRepository;
    private final Helpers helpers;
    private final UserRepository userRepository;
    private final LeadPipelineStageRepository pipelineStageRepository;
    private final LeadPipelineService leadPipelineService;


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
        leadPipelineService.assignDefaultPipeline(lead);
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

    @Override
    public Page<LeadResponseDTO> findAllByOrganization(LeadFilterDTO filter, Pageable pageable) {
        Specification<Lead> spec = LeadSpecifications.createSearch(filter);
        return helpers.toDTOList(leadRepository.findAll(spec,pageable));
    }

    @Override
    public Page<LeadResponseDTO> findMyList(LeadFilterDTO filter, Pageable pageable) {
        filter.setOwnerId(authUtil.loggedInUserId());
        Specification<Lead> spec = LeadSpecifications.createSearch(filter);
        return helpers.toDTOList(leadRepository.findAll(spec, pageable));
    }

    @Override
    public LeadResponseDTO getById(Integer leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));
        return leadUtils.createLeadResponseDTO(lead);
    }

    @Override
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

}
