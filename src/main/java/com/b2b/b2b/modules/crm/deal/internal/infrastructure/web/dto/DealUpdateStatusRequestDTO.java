package com.b2b.b2b.modules.crm.deal.internal.infrastructure.web.dto;

import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DealUpdateStatusRequestDTO {
    @NotNull(message = "Status must be provided")
    private DealStatus status;
}
