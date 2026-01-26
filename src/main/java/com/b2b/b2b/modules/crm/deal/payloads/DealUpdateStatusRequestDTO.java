package com.b2b.b2b.modules.crm.deal.payloads;

import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DealUpdateStatusRequestDTO {
    @NotNull(message = "Status must be provided")
    private DealStatus status;
}
