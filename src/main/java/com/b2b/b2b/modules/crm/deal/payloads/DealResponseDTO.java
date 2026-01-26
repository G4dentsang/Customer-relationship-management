package com.b2b.b2b.modules.crm.deal.payloads;

import com.b2b.b2b.modules.crm.company.payloads.CompanyResponseDTO;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;

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
