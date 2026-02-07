package com.b2b.b2b.modules.organization.payload;

import java.time.LocalDate;

public record OrganizationDTO(
         String organizationName,
         LocalDate organizationCreatedAt
) {
}
