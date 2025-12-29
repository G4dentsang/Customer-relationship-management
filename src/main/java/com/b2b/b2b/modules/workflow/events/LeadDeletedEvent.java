package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import lombok.Getter;

@Getter
public class LeadDeletedEvent {
    private final Lead lead;
    public LeadDeletedEvent(Lead lead) {
        this.lead = lead;
    }
}
