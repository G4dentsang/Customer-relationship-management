package com.b2b.b2b.modules.crm.lead.internal.application.service;

import com.b2b.b2b.modules.crm.lead.api.LeadPublicApi;
import com.b2b.b2b.modules.crm.lead.api.payloads.LeadSummaryDTO;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeadPublicApiImpl implements LeadPublicApi
{
    private final LeadRepository leadRepository;

    @Override
    public Optional<LeadSummaryDTO> getLeadSummary(Integer leadId) {
        return leadRepository.findById(leadId)
                .map(lead -> new LeadSummaryDTO(
                        lead.getId(),
                        lead.getLeadName(),
                        lead.getLeadEmail(),
                        lead.getLeadPhone(),
                        lead.getLeadStatus().name(),
                        lead.getAssignedUser() != null ? lead.getAssignedUser().getUserId() : null)
                );
    }

    @Override
    public boolean exists(Integer leadId) {
        return leadRepository.existsById(leadId);
    }
}
