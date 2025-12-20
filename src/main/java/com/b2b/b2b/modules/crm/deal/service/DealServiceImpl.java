package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import com.b2b.b2b.modules.crm.deal.entity.Deals;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealServiceImpl implements DealService
{
    Logger logger = LoggerFactory.getLogger(DealServiceImpl.class);

    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final DealUtils dealUtils;
    private final DomainEventPublisher domainEventPublisher;
    private final PipelineService pipelineService;

    public DealServiceImpl(LeadRepository leadRepository, DealRepository dealRepository, DealUtils dealUtils, DomainEventPublisher domainEventPublisher, PipelineService pipelineService) {
        this.leadRepository = leadRepository;
        this.dealRepository = dealRepository;
        this.dealUtils = dealUtils;
        this.domainEventPublisher = domainEventPublisher;
        this.pipelineService = pipelineService;
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
        logger.info("Deals saved successfully");
        domainEventPublisher.publishEvent(new DealCreatedEvent(savedDeal));

        return dealUtils.createDealResponseDTO(savedDeal);
    }
}
