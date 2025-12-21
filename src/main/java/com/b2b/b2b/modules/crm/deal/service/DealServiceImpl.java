package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import com.b2b.b2b.modules.crm.deal.entity.Deals;
import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.repository.DealRepository;
import com.b2b.b2b.modules.crm.deal.utils.DealUtils;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.crm.pipeline.entity.PipelineType;
import com.b2b.b2b.modules.crm.pipeline.service.PipelineService;
import com.b2b.b2b.modules.workflow.events.DealCreatedEvent;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealServiceImpl implements DealService
{
    private final CompanyRepository companyRepository;
    Logger logger = LoggerFactory.getLogger(DealServiceImpl.class);

    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final DealUtils dealUtils;
    private final DomainEventPublisher domainEventPublisher;
    private final PipelineService pipelineService;

    public DealServiceImpl(LeadRepository leadRepository, DealRepository dealRepository, DealUtils dealUtils, DomainEventPublisher domainEventPublisher, PipelineService pipelineService, CompanyRepository companyRepository) {
        this.leadRepository = leadRepository;
        this.dealRepository = dealRepository;
        this.dealUtils = dealUtils;
        this.domainEventPublisher = domainEventPublisher;
        this.pipelineService = pipelineService;
        this.companyRepository = companyRepository;
    }

    @Override
    public DealResponseDTO convertFromLead(Integer leadId, User user) {

        Lead lead = leadRepository.findById(leadId).orElseThrow(()-> new APIException("Lead not found"));

        Organization leadOrganization = lead.getOrganization();
        Organization userOrganization = user.getUserOrganizations()
                .stream()
                .filter(userOrg -> userOrg.isPrimary() )
                .findFirst()
                .orElseThrow(() -> new APIException("user organization not found"))
                .getOrganization();

        if(!lead.isReadyForConversion()){
            throw new APIException("Lead is not in final stage, thus not ready for conversion");
        }
        if(lead.getLeadStatus() == LeadStatus.CONVERTED){
            throw new APIException("Lead is already converted");
        }
        if(leadOrganization != userOrganization){
            throw new APIException("Lead organization or UserOrganization are not of same organization");
        }

        Deals deal = new Deals();
        deal.setDealName(lead.getLeadName());
        deal.setDealStatus(DealStatus.CREATED);
        deal.setCompany(lead.getCompany());
        deal.setLead(lead);
        deal.setOrganization(leadOrganization);
        pipelineService.assignDefaultPipeline(deal, PipelineType.DEAL);
        Deals savedDeal = dealRepository.save(deal);


        lead.setLeadStatus(LeadStatus.CONVERTED);
        leadRepository.save(lead);
        logger.info("Deals converted successfully");
        domainEventPublisher.publishEvent(new DealCreatedEvent(savedDeal));

        return dealUtils.createDealResponseDTO(savedDeal);
    }

    @Override
    public DealResponseDTO createDeal(DealCreateRequestDTO dealCreateRequestDTO, User user) {

        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrganization -> userOrganization.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User's organization not found"))
                .getOrganization();
        Company company = companyRepository.findByIdAndOrganization(dealCreateRequestDTO.getCompanyId(), organization);
        Lead lead = leadRepository.findByIdAndOrganization(dealCreateRequestDTO.getLeadId(), organization);

        Deals deal = new Deals();
        deal.setDealName(dealCreateRequestDTO.getDealName());
        deal.setDealStatus(dealCreateRequestDTO.getDealStatus());
        deal.setDealAmount(dealCreateRequestDTO.getDealAmount());
        deal.setOrganization(organization);
        deal.setLead(lead);
        deal.setCompany(company);

        pipelineService.assignDefaultPipeline(deal, PipelineType.DEAL);
        Deals savedDeal = dealRepository.save(deal);
        logger.info("Deals created successfully");

        domainEventPublisher.publishEvent(new DealCreatedEvent(savedDeal));
        return dealUtils.createDealResponseDTO(savedDeal);
    }

    @Override
    public List<DealResponseDTO> getAllDeals(User user) {

        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrganization -> userOrganization.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User's organization not found"))
                .getOrganization();
        List<Deals> listOfDeals = dealRepository.findAllByOrganization(organization);
        return listOfDeals.stream().map(deals -> dealUtils.createDealResponseDTO(deals)).toList();
    }

    @Override
    public List<DealResponseDTO> getAllUserOwnedDeals(User user) {

        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrganization -> userOrganization.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User's organization not found"))
                .getOrganization();

        List<Deals> ownersDeals = dealRepository.findAllByOwnerAndOrganization(user, organization);
        return ownersDeals.stream()
                .map(deal -> dealUtils.createDealResponseDTO(deal)).toList();

    }

    @Override
    public DealResponseDTO getDealById(Integer dealId, User user) {

        Organization organization = user.getUserOrganizations()
                .stream()
                .filter(userOrganization -> userOrganization.isPrimary())
                .findFirst()
                .orElseThrow(()-> new APIException("User's organization not found"))
                .getOrganization();
        Deals dealFromDB = dealRepository.findDealByIdAndOrganization(dealId,organization);
        return dealUtils.createDealResponseDTO(dealFromDB);
    }

}
