package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.payloads.LeadCreateDTO;
import com.b2b.b2b.modules.crm.lead.repository.LeadRepository;
import com.b2b.b2b.modules.workflow.events.DomainEventPublisher;
import com.b2b.b2b.modules.workflow.events.LeadCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class LeadServiceImpl implements LeadService {
    private final LeadRepository leadRepository;
    private final DomainEventPublisher domainEventPublisher;
    public LeadServiceImpl(LeadRepository leadRepository, DomainEventPublisher domainEventPublisher) {
        this.leadRepository = leadRepository;
        this.domainEventPublisher = domainEventPublisher;
    }
    @Override
    public void createLead(LeadCreateDTO leadCreateDTO) {

    Lead lead = new Lead(leadCreateDTO.getLeadName(), leadCreateDTO.getLeadEmail(),
            leadCreateDTO.getLeadPhone());
    leadRepository.save(lead);

    domainEventPublisher.publishEvent(new LeadCreatedEvent(lead, lead.getId()));
    //event listener later after workflow rules and conditions added*************
    }

    @Override
    public void updateLead(Integer leadId, LeadCreateDTO leadCreateDTO) {
        Lead lead = leadRepository.findById(leadId).orElseThrow();
    }
}
