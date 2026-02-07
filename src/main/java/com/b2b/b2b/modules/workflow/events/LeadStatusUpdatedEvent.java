package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;

public record LeadStatusUpdatedEvent(Lead lead, LeadStatus oldStatus, LeadStatus newStatus) {
}

