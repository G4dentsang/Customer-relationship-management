package com.b2b.b2b.modules.crm.lead.payloads;



import com.b2b.b2b.modules.organization.payload.MemberResponseDTO;
import com.b2b.b2b.modules.organization.payload.OrganizationDTO;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.lead.model.LeadStatus;

import java.time.LocalDateTime;

public record LeadResponseDTO(
        Integer id,
        String leadName,
        String leadEmail,
        String leadPhone,
        LeadStatus leadStatus,
        String currentStageName,
        Integer stageOrder,
        Double progressPercentage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        MemberResponseDTO assignedUser,
        OrganizationDTO organization,
        CompanyResponseDTO company
) { }
