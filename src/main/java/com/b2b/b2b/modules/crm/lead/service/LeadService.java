package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.modules.crm.lead.payloads.CreateLeadRequestDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadFilterDTO;
import com.b2b.b2b.modules.crm.lead.payloads.LeadResponseDTO;
import com.b2b.b2b.modules.crm.lead.payloads.UpdateLeadRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LeadService {
    LeadResponseDTO create(CreateLeadRequestDTO request);
    LeadResponseDTO update(Integer id, UpdateLeadRequestDTO updateLeadRequestDTO);
    void delete(Integer id);
    Page<LeadResponseDTO> findAllByOrganization(LeadFilterDTO filter,Pageable pageable);
    Page<LeadResponseDTO> findMyList(LeadFilterDTO filter, Pageable pageable);
    LeadResponseDTO getById(Integer id);

}
