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
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.lead.util.LeadUtils;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.modules.crm.pipelineStage.service.PipelineStageService;
import com.b2b.b2b.modules.workflow.events.*;
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
    private final UserRepository userRepository;

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
        User oldOwner = lead.getAssignedUser();

        updateDtoToEntity(request, lead);

        assignUser(request, lead, oldOwner);
        if(request.getLeadStatus() != null && !request.getLeadStatus().equals(oldStatus)) {
            processStatusChange(request.getLeadStatus(), oldStatus, lead);
        }

        Lead savedLead = leadRepository.save(lead);
        return leadUtils.createLeadResponseDTO(savedLead);
    }

    @Override
    public void delete(Integer id, User user) {
        Lead lead = leadRepository.findByIdAndOrganization(id, getOrg(user))
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
    public List<LeadResponseDTO> findAllByOrganization(User user) {
        return toDTOList(leadRepository.findAllByOrganization(getOrg(user)));
    }

    @Override
    public List<LeadResponseDTO> findAllByUser(User user) {
        return toDTOList(leadRepository.findAllByOwnerAndOrganization(user,getOrg(user)));
    }

    @Override
    public LeadResponseDTO getById(Integer id, User user) {
        Lead lead = leadRepository.findByIdAndOrganization(id, getOrg(user))
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
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

    private void updateDtoToEntity(UpdateLeadRequestDTO request, Lead lead) {
        if (request.getLeadName() != null) lead.setLeadName(request.getLeadName());
        if (request.getLeadEmail() != null) lead.setLeadEmail(request.getLeadEmail());
        if (request.getLeadPhone() != null) lead.setLeadPhone(request.getLeadPhone());// need "" string at least
    }

    private List<LeadResponseDTO> toDTOList(List<Lead> leads) {
        return leads.stream()
                .map(leadUtils::createLeadResponseDTO).toList();
    }

    private void assignUser(UpdateLeadRequestDTO request, Lead lead, User oldOwner) {
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

    private void processStatusChange(LeadStatus newStatus, LeadStatus oldStatus, Lead lead) {
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
