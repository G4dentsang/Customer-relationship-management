package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.entity.Lead;

public record LeadAssignedEvent(Lead lead, User newOwner) {
}
