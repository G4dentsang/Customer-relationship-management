package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.exception.APIException;
import com.b2b.b2b.modules.auth.entity.Organization;
import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.utils.DealUtils;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component("dealHelpers")
@RequiredArgsConstructor
class Helpers {
    private final DomainEventPublisher domainEventPublisher;
    private final DealUtils dealUtils;
    private final ModelMapper modelMapper;

//
//    void processStatusChange(Deal deal, DealStatus newStatus, DealStatus oldStatus) {
//        deal.setDealStatus(newStatus);
//        if (newStatus.getGroupId() == 3) {
//            deal.setClosedAt(LocalDateTime.now());
//        } else {
//            if (newStatus.getGroupId() != oldStatus.getGroupId()) {
//                pipelineStageService.promoteToNextStage(deal);
//            }
//        }
//        domainEventPublisher.publishEvent(new DealStatusUpdatedEvent(deal, oldStatus, newStatus));
//    }

    Deal createDealFromLead(Lead lead, Organization org) {
        Deal deal = new Deal();
        deal.setDealName(lead.getLeadName());
        deal.setDealStatus(DealStatus.OPEN);
        deal.setCompany(lead.getCompany());
        deal.setLead(lead);
        deal.setOrganization(org);
        deal.setAssignedUser(lead.getAssignedUser());
        return deal;
    }

    void validateLeadForConversion(Lead lead) {
        if (!lead.isReadyForConversion()) {
            throw new APIException("Lead is not in final stage; cannot convert.");
        }
        if (lead.isConverted()) {
            throw new APIException("Lead is already converted");
        }
    }


    Page<DealResponseDTO> toDTOList(Page<Deal> deals) {
        return deals.map(dealUtils::createDealResponseDTO);
    }

    Deal convertToEntity(DealCreateRequestDTO request, Organization org, Company company, Lead lead) {
        Deal deal = modelMapper.map(request, Deal.class);
        deal.setOrganization(org);
        deal.setLead(lead);
        deal.setCompany(company);
        return deal;
    }
}
