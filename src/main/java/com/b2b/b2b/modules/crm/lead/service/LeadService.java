package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;

public interface LeadService {
    LeadResponseDTO createLead(CreateLeadRequestDTO createLeadRequestDTO, User user);
    void updateLead(Integer leadId,  CreateLeadRequestDTO createLeadRequestDTO);
}
