package com.b2b.b2b.modules.auth.payloads;

import com.b2b.b2b.modules.auth.entity.AppRoles;
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
