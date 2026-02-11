package com.b2b.b2b.modules.crm.deal.internal.application.port.in;

import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealCreateRequestDTO;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealResponseDTO;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealUpdateDTO;

public interface DealCommandUseCase {
    DealResponseDTO create(DealCreateRequestDTO request);
    DealResponseDTO update(Integer id, DealUpdateDTO request);
    void delete(Integer id);
}
