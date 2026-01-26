package com.b2b.b2b.modules.crm.deal.payloads;

import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;


@Data
public class DealCreateRequestDTO {
    @NotBlank(message = "Deal name is required")
    @Size(max = 100)
    private String dealName;

    @PositiveOrZero(message = "Deal amount cannot be negative")
    @Max(value = 999999999, message = "Deal amount exceeds maximum allowable limit")
    @Digits(integer = 9, fraction = 2, message = "Amount must be a valid monetary format (up to 2 decimal places)")
    private BigDecimal dealAmount;

    @NotNull
    @Column(name = "deal_status", nullable = false, length = 50)
    private DealStatus dealStatus;

    @NotNull(message = "Lead is required")
    private Integer leadId;
    private Integer companyId;
    private Integer pipelineId;
    private Integer pipelineStageId;
}
