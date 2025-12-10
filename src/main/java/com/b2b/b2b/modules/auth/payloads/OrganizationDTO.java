package com.b2b.b2b.modules.auth.payloads;

import java.time.LocalDate;

public record OrganizationDTO(
         String organizationName,
         LocalDate organizationCreatedAt
) {
}
