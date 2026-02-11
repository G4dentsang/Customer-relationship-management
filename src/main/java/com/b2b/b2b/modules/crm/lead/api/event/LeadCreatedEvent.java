package com.b2b.b2b.modules.crm.lead.api.event;

import com.b2b.b2b.modules.crm.lead.internal.infrastructure.persistence.Lead;

public record LeadCreatedEvent(Lead lead) {


}
