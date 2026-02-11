package com.b2b.b2b.modules.crm.company.internal.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyFilterDTO {
    @Size(max = 100, message = "Search text is too long")
    private String searchText;

    @Size(max = 100)
    private String industry;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String country;
}
