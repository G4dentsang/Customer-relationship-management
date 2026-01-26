package com.b2b.b2b.modules.crm.company.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyFilterDTO {
    private String searchText;
    private String industry;
    private String city;
    private String state;
    private String country;
}
