package com.b2b.b2b.modules.crm.deal.internal.application.port.in;

import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealFilterDTO;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto.DealResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DealQueryUseCase {
    Page<DealResponseDTO> findAll(DealFilterDTO filter, Pageable pageable);
    Page<DealResponseDTO> findAllByOwner(DealFilterDTO filter, Pageable pageable);
    DealResponseDTO getById(Integer id);
}
