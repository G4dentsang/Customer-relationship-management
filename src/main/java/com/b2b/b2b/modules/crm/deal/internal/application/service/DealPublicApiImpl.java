package com.b2b.b2b.modules.crm.deal.internal.application.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.deal.api.DealPublicApi;
import com.b2b.b2b.modules.crm.deal.api.event.DealCreatedEvent;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealRepository;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.deal.internal.utils.DealUtils;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.model.DealPipeline;
import com.b2b.b2b.modules.crm.pipeline.service.DealPipelineService;
import com.b2b.b2b.modules.crm.pipelineStage.service.DealPipelineStageService;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.organization.persistence.OrganizationRepository;
import com.b2b.b2b.shared.DomainEventPublisher;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class DealPublicApiImpl implements DealPublicApi
{
    private final LeadRepository leadRepository;
    private final Helpers helpers;
    private final OrganizationRepository organizationRepository;
    private final DealPipelineService dealPipelineService;
    private final DealPipelineStageService dealPipelineStageService;
    private final DealRepository dealRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final DealUtils dealUtils;

    @Override
    @Transactional
    public DealResponseDTO convertFromLead(Integer id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));

        helpers.validateLeadForConversion(lead);

        Integer orgId = OrganizationContext.getOrgId();
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(()-> new ResourceNotFoundException("Organization", "id", orgId));

        Deal deal = helpers.createDealFromLead(lead, org);

        DealPipeline defaultPipeline = dealPipelineService.assignDefaultPipeline(deal);
        dealPipelineStageService.assignDefaultStage(defaultPipeline,  deal);

        Deal savedDeal = dealRepository.save(deal);

        lead.markAsConverted();
        leadRepository.save(lead);

        domainEventPublisher.publishEvent(new DealCreatedEvent(savedDeal));
        return dealUtils.createDealResponseDTO(savedDeal);
    }

    @Override
    public Page<DealResponseDTO> getContactDeals(Integer id, Pageable pageable) {
        return helpers.toDTOList(dealRepository.findAllByCompanyContactsId(id, pageable));
    }

    @Override
    public Page<DealResponseDTO> getCompanyDeals(Integer companyId, Pageable pageable) {
        return helpers.toDTOList(dealRepository.findAllByCompanyId(companyId, pageable));
    }
}
