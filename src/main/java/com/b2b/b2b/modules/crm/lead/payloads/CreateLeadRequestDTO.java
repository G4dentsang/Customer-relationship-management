package com.b2b.b2b.modules.crm.lead.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;


@Data
public class CreateLeadRequestDTO {
    @NotBlank(message = "Lead name cannot be empty")
    @Size(max = 100)
    private String leadName;

    @NotBlank(message = "Email is required")
    @Size(max = 255)
    @Email(message = "Please provide a valid email address")
    private String leadEmail;

    @Pattern(regexp = "^\\+?[0-9.]{7,15}$", message = "Invalid phone number format")
    private String leadPhone;

    private Integer companyId;

    @URL(message = "Invalid website URL")
    private String website;

    @Size(max = 50)
    private String industry;

    private Integer assignedUserId;
}
