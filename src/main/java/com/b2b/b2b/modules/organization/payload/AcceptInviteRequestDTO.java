package com.b2b.b2b.modules.organization.payload;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AcceptInviteRequestDTO {
    String token;
    String username;
    String password;
}
