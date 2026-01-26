package com.b2b.b2b.modules.crm.lead.payloads;

import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class UpdateLeadRequestDTO {
    @Size(max = 100)
    private String leadName;

    @Email
    @Size(max = 255)
    private String leadEmail;

    @Pattern(regexp = "^\\+?[0-9.]{7,15}$", message = "Invalid phone number format")
    private String leadPhone;

    private LeadStatus leadStatus;
    private Integer ownerId;
}
