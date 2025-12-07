package com.b2b.b2b.modules.auth.security.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingInResponseDTO {
    private Integer id;
    private String userName;
    private String email;
    private List<String> role;
}
