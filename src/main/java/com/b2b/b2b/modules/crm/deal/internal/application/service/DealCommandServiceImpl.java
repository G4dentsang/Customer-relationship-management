package com.b2b.b2b.modules.crm.deal.internal.application.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.crm.company.internal.entity.Company;
import com.b2b.b2b.modules.crm.company.internal.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.deal.api.event.DealCreatedEvent;
import com.b2b.b2b.modules.crm.deal.api.event.DealDeletedEvent;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealUpdateDTO;
import com.b2b.b2b.modules.crm.deal.internal.application.port.in.DealCommandUseCase;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealRepository;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealStatus;
import com.b2b.b2b.modules.crm.deal.internal.utils.DealUtils;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.service.DealPipelineService;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.organization.persistence.OrganizationRepository;
import com.b2b.b2b.shared.DomainEventPublisher;
import com.b2b.b2b.shared.multitenancy.OrganizationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class DealCommandServiceImpl implements DealCommandUseCase {
    private final OrganizationRepository organizationRepository;
    private final CompanyRepository companyRepository;
    private final LeadRepository leadRepository;
    private final Helpers helpers;
    private final DealPipelineService dealPipelineService;
    private final DealRepository dealRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final DealUtils dealUtils;

    @Override
    @Transactional
    public DealResponseDTO create(DealCreateRequestDTO request) {
        Integer orgId = OrganizationContext.getOrgId();
        Integer comId = request.getCompanyId();
        Integer leadId = request.getLeadId();
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));
        Company company = companyRepository.findById(comId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", comId));
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", leadId));

        if (lead.isConverted()) {
            throw new APIException("This Lead has already been converted to a deal.");
        }

        Deal deal = helpers.convertToEntity(request, org, company, lead);
        dealPipelineService.assignDefaultPipeline(deal);

        lead.setConverted(true);
        leadRepository.save(lead);

        Deal savedDeal = dealRepository.save(deal);
        domainEventPublisher.publishEvent(new DealCreatedEvent(savedDeal));
        return dealUtils.createDealResponseDTO(savedDeal);
    }

    @Override
    @Transactional
    public DealResponseDTO update(Integer id, DealUpdateDTO request) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));

        DealStatus oldStatus = deal.getDealStatus();

        if (request.getDealName() != null) deal.setDealName(request.getDealName());
        if (request.getDealAmount() != null) deal.setDealAmount(request.getDealAmount());

        if (request.getDealStatus() != null && !request.getDealStatus().equals(oldStatus)) {
            // helpers.processStatusChange(deal, request.getDealStatus(), oldStatus);
        }

        return dealUtils.createDealResponseDTO(dealRepository.save(deal));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));

        deal.setDealStatus(DealStatus.SOFT_DELETED);
        dealRepository.save(deal);

        domainEventPublisher.publishEvent(new DealDeletedEvent(deal));
    }
}
