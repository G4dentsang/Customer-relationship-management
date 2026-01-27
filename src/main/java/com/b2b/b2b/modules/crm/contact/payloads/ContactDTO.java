package com.b2b.b2b.modules.crm.contact.payloads;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
public class ContactDTO {
    @NotBlank(message = "first name name is required")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "last name name is required")
    @Size(max = 50)
    private String lastName;

    @Email(message = "Please provide a valid email address")
    @Size(max = 255)
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^\\+?[0-9.]{7,15}$", message = "Invalid phone number format")
    @Size(max = 20)
    private String phone;

    @NotNull(message = "Company ID is required to think this contact")
    private Integer companyId;
}
