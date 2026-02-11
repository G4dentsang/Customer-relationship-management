package com.b2b.b2b.modules.crm.lead.internal.application.port.in;

import com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto.LeadFilterDTO;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto.LeadResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeadQueryUseCase {
    Page<LeadResponseDTO> findAllByOrganization(LeadFilterDTO filter, Pageable pageable);
    Page<LeadResponseDTO> findMyList(LeadFilterDTO filter, Pageable pageable);
    LeadResponseDTO getById(Integer id);
}
