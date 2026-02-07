package com.b2b.b2b.modules.crm.lead.service;

import com.b2b.b2b.modules.crm.lead.payloads.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LeadService {
    LeadResponseDTO create(CreateLeadRequestDTO request);
    LeadResponseDTO update(Integer id, UpdateLeadRequestDTO request);
    LeadResponseDTO updateStatus(Integer id, LeadUpdateStatusRequestDTO request);
    void delete(Integer id);
    Page<LeadResponseDTO> findAllByOrganization(LeadFilterDTO filter,Pageable pageable);
    Page<LeadResponseDTO> findMyList(LeadFilterDTO filter, Pageable pageable);
    LeadResponseDTO getById(Integer id);
    LeadResponseDTO changeStage(Integer id,  ChangeStageRequestDTO request);

}
