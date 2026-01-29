package com.b2b.b2b.modules.auth.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class LogInRequestDTO {
    @NotBlank
    private String identifier;

    @NotBlank
    private String password;

}
