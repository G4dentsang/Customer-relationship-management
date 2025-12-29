package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.auth.entity.User;
import com.b2b.b2b.modules.crm.lead.entity.Lead;
import lombok.Getter;

@Getter
public class LeadAssignedEvent {
    private final Lead lead;
    private final User newOwner;
    public LeadAssignedEvent(Lead lead, User newOwner) {
        this.lead = lead;
        this.newOwner = newOwner;
    }
}
