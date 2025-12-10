package com.b2b.b2b.modules.crm.lead.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLeadRequestDTO {
    @Size(min = 1, max = 50)
    private String leadName;
    @Size(min = 1, max = 50)
    @Email
    private String leadEmail;
    @Size(min = 1, max = 20)
    private String leadPhone;
    private String leadStatus;
}
