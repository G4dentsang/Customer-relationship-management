package com.b2b.b2b.modules.crm.lead.api;

import com.b2b.b2b.modules.crm.lead.api.payloads.LeadSummaryDTO;

import java.util.Optional;

public interface LeadPublicApi {
    Optional<LeadSummaryDTO> getLeadSummary(Integer leadId);
    boolean exists(Integer leadId);
}
