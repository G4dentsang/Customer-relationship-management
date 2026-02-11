package com.b2b.b2b.modules.crm.contact.internal.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactFilterDTO {
    @Size(max = 100, message = "Search text is too long")
    private String searchText;

    private Integer companyId;

    @Size(max= 100)
    private String jobTitle;
}
