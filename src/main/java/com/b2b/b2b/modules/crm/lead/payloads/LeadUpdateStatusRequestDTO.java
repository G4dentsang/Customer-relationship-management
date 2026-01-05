package com.b2b.b2b.modules.crm.lead.payloads;

import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LeadUpdateStatusRequestDTO {
    private LeadStatus leadStatus;
}
