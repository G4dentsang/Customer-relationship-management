package com.b2b.b2b.modules.crm.lead.payloads;


import java.time.LocalDateTime;

public record LeadResponseDTO(
        Integer id,
        String leadName,
        String leadEmail,
        String leadPhone,
        String leadStatus,
        LocalDateTime createdAt
) { }
