package com.b2b.b2b.modules.crm.lead.internal.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
public class AssignUserRequestDTO {
    @NotNull(message = "New owner ID is required")
    private Integer newOwnerId;
}
