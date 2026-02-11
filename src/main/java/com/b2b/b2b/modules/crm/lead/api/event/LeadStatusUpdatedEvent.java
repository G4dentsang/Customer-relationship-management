package com.b2b.b2b.modules.crm.lead.api.event;

import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.LeadStatus;

public record LeadStatusUpdatedEvent(Lead lead, LeadStatus oldStatus, LeadStatus newStatus) {
}

