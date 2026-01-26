package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.auth.repository.OrganizationRepository;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadFilterDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.payloads.UpdateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.lead.util.LeadSpecifications;
import com.b2b.b2b.modules.crm.lead.util.LeadUtils;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
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
    private final PipelineService pipelineService;
    private final LeadUtils leadUtils;
    private final AuthUtil authUtil;
    private final OrganizationRepository organizationRepository;
    private final Helpers helpers;

    @Override
    @Transactional
    public LeadResponseDTO create(CreateLeadRequestDTO request) {
        Integer orgId = OrganizationContext.getOrgId();
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));
        Company company = helpers.getOrCreateCompany(request, org);
        Lead lead = helpers.convertToEntity(request, org, company);

        pipelineService.assignDefaultPipeline(lead, PipelineType.LEAD);

        Lead savedLead = leadRepository.save(lead);
        domainEventPublisher.publishEvent(new LeadCreatedEvent(savedLead));
        return leadUtils.createLeadResponseDTO(savedLead);
    }

    @Override
    @Transactional
    public LeadResponseDTO update(Integer leadId, UpdateLeadRequestDTO request) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        LeadStatus oldStatus = lead.getLeadStatus();
        User oldOwner = lead.getAssignedUser();

        helpers.updateDtoToEntity(request, lead);

        helpers.assignUser(request, lead, oldOwner);
        if(request.getLeadStatus() != null && !request.getLeadStatus().equals(oldStatus)) {
            helpers.processStatusChange(request.getLeadStatus(), oldStatus, lead);
        }

        Lead savedLead = leadRepository.save(lead);
        return leadUtils.createLeadResponseDTO(savedLead);
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
        if(filter.getOwnerId() == null) filter.setOwnerId(authUtil.loggedInUserId());
        Specification<Lead> spec = LeadSpecifications.createSearch(filter);
        return helpers.toDTOList(leadRepository.findAll(spec, pageable));
    }

    @Override
    public LeadResponseDTO getById(Integer id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
        return leadUtils.createLeadResponseDTO(lead);
    }

}
