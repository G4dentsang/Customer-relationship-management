package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.repository.OrganizationRepository;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealFilterDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealUpdateDTO;
import com.b2b.b2b.modules.crm.deal.repository.DealRepository;
import com.b2b.b2b.modules.crm.deal.utils.DealSpecifications;
import com.b2b.b2b.modules.crm.deal.utils.DealUtils;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.modules.workflow.events.DealCreatedEvent;
import com.b2b.b2b.modules.workflow.events.DealDeletedEvent;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
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
public class DealServiceImpl implements DealService
{
    private final CompanyRepository companyRepository;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final DealUtils dealUtils;
    private final DomainEventPublisher domainEventPublisher;
    private final PipelineService pipelineService;
    private final AuthUtil authUtil;
    private final OrganizationRepository organizationRepository;
    private final Helpers helpers;

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
        pipelineService.assignDefaultPipeline(deal, PipelineType.DEAL);

        lead.setConverted(true);
        leadRepository.save(lead);

        Deal savedDeal = dealRepository.save(deal);
        domainEventPublisher.publishEvent(new DealCreatedEvent(savedDeal));
        return dealUtils.createDealResponseDTO(savedDeal);
    }

    @Override
    public Page<DealResponseDTO> findAll(DealFilterDTO filter, Pageable pageable) {
        Specification<Deal> spec = DealSpecifications.createSearch(filter);
        return helpers.toDTOList(dealRepository.findAll(spec, pageable));
    }

    @Override
    public Page<DealResponseDTO> findAllByOwner(DealFilterDTO filter, Pageable pageable) {
        if(filter.getOwnerId() == null) filter.setOwnerId(authUtil.loggedInUserId());
        Specification<Deal> spec = DealSpecifications.createSearch(filter);
        return helpers.toDTOList(dealRepository.findAll(spec, pageable));
    }

    @Override
    public DealResponseDTO getById(Integer id) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Deal", "id", id));
        return dealUtils.createDealResponseDTO(deal);
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
            helpers.processStatusChange(deal, request.getDealStatus(), oldStatus);
        }

        return dealUtils.createDealResponseDTO(dealRepository.save(deal));
    }

    @Override
    public void delete(Integer id) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));

        deal.setDealStatus(DealStatus.SOFT_DELETED);
        dealRepository.save(deal);

        domainEventPublisher.publishEvent(new DealDeletedEvent(deal));
    }

    @Override
    public Page<DealResponseDTO> getCompanyDeals(Integer companyId, Pageable pageable) {
        return helpers.toDTOList(dealRepository.findAllByCompanyId(companyId, pageable));
    }

    @Override
    public Page<DealResponseDTO> getContactDeals(Integer id, Pageable pageable) {
        return helpers.toDTOList(dealRepository.findAllByCompanyContactsId(id, pageable));
    }


    @Override
    @Transactional
    public DealResponseDTO convertFromLead(Integer id) {
        Integer orgId = OrganizationContext.getOrgId();
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(()-> new ResourceNotFoundException("Organization", "id", orgId));
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));

        helpers.validateLeadForConversion(lead);

        Deal deal = helpers.createDealFromLead(lead, org);
        pipelineService.assignDefaultPipeline(deal, PipelineType.DEAL);
        Deal savedDeal = dealRepository.save(deal);

        lead.markAsConverted();
        leadRepository.save(lead);

        domainEventPublisher.publishEvent(new DealCreatedEvent(savedDeal));
        return dealUtils.createDealResponseDTO(savedDeal);
    }
}
