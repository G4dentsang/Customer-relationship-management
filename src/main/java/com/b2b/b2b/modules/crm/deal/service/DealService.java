package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealUpdateDTO;

import java.util.List;

public interface DealService {
    DealResponseDTO convertFromLead(Integer id);
    DealResponseDTO create(DealCreateRequestDTO request);
    List<DealResponseDTO> findAll();
    List<DealResponseDTO> findAllByOwner();
    DealResponseDTO getById(Integer id);
    List<DealResponseDTO> getCompanyDeals(Integer idr);
    List<DealResponseDTO> getContactDeals(Integer id);
    DealResponseDTO update(Integer id, DealUpdateDTO request);
    void delete(Integer id);

}
