package com.b2b.b2b.modules.auth.payloads;

import java.time.LocalDate;

public record MemberResponseDTO(
        Integer id,
        String name,
        String email,
        String role,
        boolean isActive,
        LocalDate joinedAt
) {
}
