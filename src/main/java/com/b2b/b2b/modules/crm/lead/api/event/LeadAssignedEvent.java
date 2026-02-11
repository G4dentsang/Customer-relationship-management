package com.b2b.b2b.modules.crm.lead.api.event;

import com.b2b.b2b.modules.user.model.User;
import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;

public record LeadAssignedEvent(Lead lead, User newOwner) {
}
