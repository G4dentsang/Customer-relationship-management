package com.b2b.b2b.modules.crm.lead.internal.application.port.in;

import com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto.*;

public interface LeadCommandUseCase {
    LeadResponseDTO create(CreateLeadRequestDTO request);
    LeadResponseDTO update(Integer id, UpdateLeadRequestDTO request);
    LeadResponseDTO updateStatus(Integer id, LeadUpdateStatusRequestDTO request);
    LeadResponseDTO changeStage(Integer id,  ChangeStageRequestDTO request);
    void delete(Integer id);
}
