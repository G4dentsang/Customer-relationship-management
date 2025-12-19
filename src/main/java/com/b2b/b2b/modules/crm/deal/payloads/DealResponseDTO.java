package com.b2b.b2b.modules.crm.deal.payloads;

import com.b2b.b2b.modules.crm.company.entity.Company;
import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;

import java.time.LocalDateTime;

public record DealResponseDTO(
        Long id,
        String dealName,
        Double dealAmount,
        DealStatus dealStatus,
        LocalDateTime closedAt,
        LocalDateTime createdAt,
        CompanyResponseDTO company
) {
}
