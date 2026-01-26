package com.b2b.b2b.modules.crm.lead.payloads;

import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class LeadUpdateStatusRequestDTO {
    @NotNull(message = "A status must be provided")
    private LeadStatus leadStatus;
}
