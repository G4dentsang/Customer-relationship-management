package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import lombok.Getter;

@Getter
public class LeadCreatedEvent {
    private final Lead lead;
    public LeadCreatedEvent(Lead lead) {
        this.lead = lead;
    }


}
