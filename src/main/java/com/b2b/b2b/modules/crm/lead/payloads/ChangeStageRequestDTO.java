package com.b2b.b2b.modules.crm.lead.payloads;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeStageRequestDTO {
    @NotNull(message = "Stage Id is required")
    private Integer destinationStageId;
}
