package com.b2b.b2b.modules.crm.lead.event;

import com.b2b.b2b.modules.crm.lead.model.Lead;
import com.b2b.b2b.modules.crm.lead.model.LeadStatus;

public record LeadStatusUpdatedEvent(Lead lead, LeadStatus oldStatus, LeadStatus newStatus) {
}

