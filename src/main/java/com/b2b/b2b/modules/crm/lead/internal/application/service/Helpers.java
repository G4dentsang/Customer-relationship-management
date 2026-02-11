package com.b2b.b2b.modules.crm.lead.internal.application.service;

import com.b2b.b2b.exception.ResourceNotFoundException;
import com.b2b.b2b.modules.organization.model.Organization;
import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.user.persistence.UserRepository;
import com.b2b.b2b.modules.crm.company.internal.entity.Company;
import com.b2b.b2b.modules.crm.company.internal.repository.CompanyRepository;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto.UpdateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.internal.util.LeadUtils;
import com.b2b.b2b.shared.DomainEventPublisher;
import com.b2b.b2b.modules.crm.lead.api.event.LeadAssignedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;


@Component("leadHelpers")
@Slf4j
@RequiredArgsConstructor
//package-private
class Helpers {
    private final ModelMapper modelMapper;
    private final LeadUtils leadUtils;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final DomainEventPublisher domainEventPublisher;
    private final View error;


    Lead convertToEntity(CreateLeadRequestDTO request, Organization organization, Company company) {
        Lead lead = modelMapper.map(request, Lead.class);
        lead.setId(null); //mapper being too smart
        lead.setOrganization(organization);
        lead.setCompany(company);
        return lead;
    }

    void updateDtoToEntity(UpdateLeadRequestDTO request, Lead lead) {
        if (request.getLeadName() != null) lead.setLeadName(request.getLeadName());
        if (request.getLeadEmail() != null) lead.setLeadEmail(request.getLeadEmail());
        if (request.getLeadPhone() != null) lead.setLeadPhone(request.getLeadPhone());// need "" string at least
    }

    Page<LeadResponseDTO> toDTOList(Page<Lead> leads) {
        return leads.map(leadUtils::createLeadResponseDTO);
    }

    Company getOrCreateCompany(CreateLeadRequestDTO request, Organization org) {
        return companyRepository.findById(request.getCompanyId())
                .orElseGet(() -> {
                    Company company = new Company();
                    company.setWebsite(request.getWebsite());
                    company.setIndustry(request.getIndustry());
                    company.setOrganization(org);
                    company.setCompanyName("not given company name");
                    return companyRepository.save(company);
                });
    }

    void assignUser(UpdateLeadRequestDTO request, Lead lead, User oldOwner) {
        if (request.getNewOwnerId() != null) {
            User newOwner = userRepository.findById(request.getNewOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getNewOwnerId()));
            if (!oldOwner.equals(newOwner)) {
                lead.setAssignedUser(newOwner);
                log.info("Lead {} assigned to {}",
                        lead.getId(), newOwner.getUserName());
                domainEventPublisher.publishEvent(new LeadAssignedEvent(lead, newOwner));
            } else{
                throw new IllegalStateException("You are already the assigned one : " + oldOwner.getUserName());
            }
        }
    }


}
