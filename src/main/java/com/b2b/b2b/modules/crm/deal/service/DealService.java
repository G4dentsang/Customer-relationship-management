package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.payloads.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;

import java.util.List;

public interface DealService {
    DealResponseDTO convertFromLead(Integer leadId, User user); // add this service method inside leadService
    DealResponseDTO createDeal(DealCreateRequestDTO dealCreateRequestDTO, User user);
    List<DealResponseDTO> getAllDeals(User user);
    List<DealResponseDTO> getAllUserOwnedDeals(User user);
    DealResponseDTO getDealById(Integer dealId, User user);
}
