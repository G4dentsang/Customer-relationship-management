package com.b2b.b2b.modules.crm.contact.payloads;

import com.b2b.b2b.modules.crm.company.entity.Company;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContactDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Company company;
}
