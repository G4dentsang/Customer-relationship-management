package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.payloads.UpdateLeadRequestDTO;

import java.util.List;

public interface LeadService {
    LeadResponseDTO createLead(CreateLeadRequestDTO createLeadRequestDTO, User user);
    LeadResponseDTO updateLead(Integer leadId, UpdateLeadRequestDTO updateLeadRequestDTO);
    List<LeadResponseDTO> getAllOrganizationLeads(User user);
    List<LeadResponseDTO> getAllUserOwnedLeads(User user);
    LeadResponseDTO getLeadById(Integer leadId, User user);
}
