package com.b2b.b2b.modules.crm.deal.api;

import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DealPublicApi {
    DealResponseDTO convertFromLead(Integer id);
    Page<DealResponseDTO> getContactDeals(Integer id, Pageable pageable);
    Page<DealResponseDTO> getCompanyDeals(Integer id,  Pageable pageable);
}
