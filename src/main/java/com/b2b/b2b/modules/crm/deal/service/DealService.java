package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;

import java.util.List;

public interface DealService {
    DealResponseDTO convertFromLead(Integer id, User user);
    DealResponseDTO create(DealCreateRequestDTO request, User user);
    List<DealResponseDTO> findAllByOrganization(User user);
    List<DealResponseDTO> findAllByUser(User user);
    DealResponseDTO getById(Integer id, User user);
    List<DealResponseDTO> getCompanyDeals(Integer id, User user);
    List<DealResponseDTO> getContactDeals(Integer id, User user);
}
