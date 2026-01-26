package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealFilterDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DealService {
    DealResponseDTO convertFromLead(Integer id);
    DealResponseDTO create(DealCreateRequestDTO request);
    Page<DealResponseDTO> findAll(DealFilterDTO filter, Pageable pageable);
    Page<DealResponseDTO> findAllByOwner(DealFilterDTO filter, Pageable pageable);
    DealResponseDTO getById(Integer id);
    Page<DealResponseDTO> getCompanyDeals(Integer id,  Pageable pageable);
    Page<DealResponseDTO> getContactDeals(Integer id, Pageable pageable);
    DealResponseDTO update(Integer id, DealUpdateDTO request);
    void delete(Integer id);

}
