package com.b2b.b2b.modules.organization.payload;

import com.b2b.b2b.modules.organization.model.AppRoles;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InviteMemberRequestDTO {
    private String email;
    private AppRoles role;
}
