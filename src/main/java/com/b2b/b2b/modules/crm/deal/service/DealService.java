package com.b2b.b2b.modules.crm.deal.service;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.deal.payloads.DealResponseDTO;

public interface DealService {
    DealResponseDTO convertFromLead(Integer leadId, User user);
}
