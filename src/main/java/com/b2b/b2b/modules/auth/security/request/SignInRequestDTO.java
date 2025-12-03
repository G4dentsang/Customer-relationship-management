package com.b2b.b2b.modules.auth.security.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignInRequestDTO {
    @NotBlank
    @Size(max = 20)
    @Column(name = "username")
    private String userName;

    @NotBlank
    @Size(max = 120)
    @Column(name = "password")
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email")
    private String email;
    private Integer organizationId;

}
