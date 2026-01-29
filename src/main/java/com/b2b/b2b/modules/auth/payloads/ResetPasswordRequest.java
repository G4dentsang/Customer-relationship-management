package com.b2b.b2b.modules.auth.payloads;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
