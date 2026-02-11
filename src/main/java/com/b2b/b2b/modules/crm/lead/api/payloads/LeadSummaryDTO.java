package com.b2b.b2b.modules.crm.lead.api.payloads;


public record LeadSummaryDTO(
        Integer id,
        String leadName,
        String leadEmail,
        String leadPhone,
        String leadStatus,
        Integer assignedUser

) {
}
