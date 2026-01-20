package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.payloads.UpdateLeadRequestDTO;

import java.util.List;

public interface LeadService {
    LeadResponseDTO create(CreateLeadRequestDTO request);
    LeadResponseDTO update(Integer id, UpdateLeadRequestDTO updateLeadRequestDTO);
    void delete(Integer id);
    List<LeadResponseDTO> findAllByOrganization();
    List<LeadResponseDTO> findMyList();
    LeadResponseDTO getById(Integer id);

}
