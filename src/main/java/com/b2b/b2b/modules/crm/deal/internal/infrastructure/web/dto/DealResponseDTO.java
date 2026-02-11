package com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto;

import com.b2b.b2b.modules.crm.company.internal.dto.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DealResponseDTO(
        Integer id,
        String dealName,
        BigDecimal dealAmount,
        DealStatus dealStatus,
        String stageName,
        Integer stageOrder,
        Double progressPercentage,
        LocalDateTime createdAt,
        LocalDateTime closedAt,
        CompanyResponseDTO company

) {
}
