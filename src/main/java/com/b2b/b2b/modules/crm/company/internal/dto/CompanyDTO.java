package com.b2b.b2b.modules.crm.company.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Data
public class CompanyDTO {

    @NotBlank(message = "Company name is required")
    @Size(max = 100)
    String companyName;

    @URL(message = "Invalid website URL")
    String companyWebsite;

    @NotBlank(message = "Company's industry is required")
    @Size(max = 100)
    String companyIndustry;
}
