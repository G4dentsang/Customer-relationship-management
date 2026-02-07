package com.b2b.b2b.modules.crm.lead.event;

import com.b2b.b2b.modules.crm.lead.model.Lead;

public record LeadDeletedEvent(Lead lead) {
}
