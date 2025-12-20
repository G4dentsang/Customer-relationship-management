package com.b2b.b2b.modules.crm.deal.payloads;

import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;

import java.time.LocalDateTime;

public record DealResponseDTO(
        Integer id,
        String dealName,
        Double dealAmount,
        DealStatus dealStatus,
        LocalDateTime createdAt,
        LocalDateTime closedAt,
        CompanyResponseDTO company
) {
}
