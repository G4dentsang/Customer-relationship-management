package com.b2b.b2b.modules.auth.payloads;

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
