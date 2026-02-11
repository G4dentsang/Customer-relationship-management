package com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto;



import com.b2b.b2b.modules.organization.payload.MemberResponseDTO;
import com.b2b.b2b.modules.organization.payload.OrganizationDTO;
import com.b2b.b2b.modules.crm.company.internal.dto.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadStatus;

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
