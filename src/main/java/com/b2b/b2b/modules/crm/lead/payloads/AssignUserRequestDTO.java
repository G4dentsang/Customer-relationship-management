package com.b2b.b2b.modules.crm.lead.payloads;

import com.b2b.b2b.modules.auth.entity.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssignUserRequestDTO {
    private User newOwner;
}
