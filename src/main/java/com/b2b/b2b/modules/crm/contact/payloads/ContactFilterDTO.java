package com.b2b.b2b.modules.crm.contact.payloads;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactFilterDTO {
    private String searchText;
    private Integer companyId;
    private String jobTitle;
}
