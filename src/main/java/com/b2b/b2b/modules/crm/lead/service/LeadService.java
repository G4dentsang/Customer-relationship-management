package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.payloads.UpdateLeadRequestDTO;

import java.util.List;

public interface LeadService {
    LeadResponseDTO create(CreateLeadRequestDTO request, User user);
    LeadResponseDTO update(Integer id, UpdateLeadRequestDTO updateLeadRequestDTO, User user);
    List<LeadResponseDTO> findAllByOrganization(User user);
    List<LeadResponseDTO> findAllByUser(User user);
    LeadResponseDTO getById(Integer id, User user);
}
