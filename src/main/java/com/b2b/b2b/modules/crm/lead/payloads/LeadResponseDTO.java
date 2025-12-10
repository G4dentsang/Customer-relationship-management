package com.b2b.b2b.modules.crm.lead.payloads;



import com.b2b.b2b.modules.auth.payloads.OrganizationDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyDTO;
import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;

import java.time.LocalDateTime;

public record LeadResponseDTO(
        Integer id,
        String leadName,
        String leadEmail,
        String leadPhone,
        LeadStatus leadStatus,
        LocalDateTime createdAt,
        OrganizationDTO organization,
        CompanyDTO company
) { }
