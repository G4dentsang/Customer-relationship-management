package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.repository.DealRepository;
import com.b2b.b2b.modules.crm.deal.utils.DealUtils;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.modules.workflow.events.DealCreatedEvent;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
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
public class DealServiceImpl implements DealService
{
    private final CompanyRepository companyRepository;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final DealUtils dealUtils;
    private final DomainEventPublisher domainEventPublisher;
    private final PipelineService pipelineService;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public DealResponseDTO create(DealCreateRequestDTO request, User user) {

        Organization org = authUtil.getPrimaryOrganization(user);
        Company company = findCompany(request.getCompanyId(), org);
        Lead lead = findLead(request.getLeadId(), org);

        if (lead.isConverted()) {
            throw new APIException("This Lead has already been converted to a deal.");
        }

        Deal deal = convertToEntity(request, org, company, lead);
        pipelineService.assignDefaultPipeline(deal, PipelineType.DEAL);

        lead.setConverted(true);
        leadRepository.save(lead);

        Deal savedDeal = dealRepository.save(deal);
        domainEventPublisher.publishEvent(new DealCreatedEvent(savedDeal));
        return dealUtils.createDealResponseDTO(savedDeal);
    }

    @Override
    public List<DealResponseDTO> findAllByOrganization(User user) {
        return toDTOList(dealRepository.findAllByOrganization(getOrg(user)));
    }

    @Override
    public List<DealResponseDTO> findAllByUser(User user) {
        return toDTOList(dealRepository.findAllByOwnerAndOrganization(user, getOrg(user)));
    }

    @Override
    public DealResponseDTO getById(Integer id, User user) {
        Deal deal = dealRepository.findDealByIdAndOrganization(id,getOrg(user))
                .orElseThrow(()-> new ResourceNotFoundException("Deal", "id", id));
        return dealUtils.createDealResponseDTO(deal);
    }

    @Override
    public List<DealResponseDTO> getCompanyDeals(Integer companyId, User user) {
        return toDTOList(dealRepository.findAllDealsByCompanyIdAndOrganization(companyId, getOrg(user)));
    }

    @Override
    public List<DealResponseDTO> getContactDeals(Integer id, User user) {
        return toDTOList(dealRepository.findAllDealsByCompanyContactsIdAndOrganization(id, getOrg(user)));
    }

    @Override
    @Transactional
    public DealResponseDTO convertFromLead(Integer id, User user) {

        Organization org = getOrg(user);
        Lead lead = leadRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));

        validateLeadForConversion(lead);

        Deal deal = createDealFromLead(lead, org);
        pipelineService.assignDefaultPipeline(deal, PipelineType.DEAL);
        Deal savedDeal = dealRepository.save(deal);

        lead.markAsConverted();
        leadRepository.save(lead);

        domainEventPublisher.publishEvent(new DealCreatedEvent(savedDeal));
        return dealUtils.createDealResponseDTO(savedDeal);
    }

    /********Helper methods********/

    private Deal createDealFromLead(Lead lead, Organization org){
        Deal deal = new Deal();
        deal.setDealName(lead.getLeadName());
        deal.setDealStatus(DealStatus.CREATED);
        deal.setCompany(lead.getCompany());
        deal.setLead(lead);
        deal.setOrganization(org);
        return deal;
    }

    private void validateLeadForConversion(Lead lead){
        if(!lead.isReadyForConversion()){
            throw new APIException("Lead is not in final stage; cannot convert.");
        }
        if(lead.isConverted()){
            throw new APIException("Lead is already converted");
        }
    }

    private Company findCompany(Integer id, Organization org) {
        return companyRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    private Lead findLead(Integer id, Organization org) {
        return leadRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new ResourceNotFoundException("Lead", "id", id));
    }

    private List<DealResponseDTO> toDTOList(List<Deal> deals) {
        return deals.stream()
                .map(dealUtils::createDealResponseDTO).toList();
    }

    private Deal convertToEntity(DealCreateRequestDTO request, Organization org, Company company, Lead lead) {
        Deal deal = modelMapper.map(request, Deal.class);
        deal.setOrganization(org);
        deal.setLead(lead);
        deal.setCompany(company);
        return deal;
    }

    private Organization getOrg(User user){
        return authUtil.getPrimaryOrganization(user);
    }
}
