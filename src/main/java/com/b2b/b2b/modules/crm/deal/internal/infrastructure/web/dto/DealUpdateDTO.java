package com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto;

import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealStatus;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
public class DealUpdateDTO {
    @Size(max = 100)
    private String dealName;

    @PositiveOrZero(message = "Deal amount cannot be negative")
    @Max(value = 999999999, message = "Deal amount exceeds maximum allowable limit")
    @Digits(integer = 9, fraction = 2, message = "Amount must be a valid monetary format (up to 2 decimal places)")
    private BigDecimal dealAmount;

    private DealStatus dealStatus;
    private Integer ownerId;

}
