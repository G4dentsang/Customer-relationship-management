package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.lead.entity.Lead;
import com.b2b.b2b.modules.crm.lead.entity.LeadStatus;
import lombok.Getter;

@Getter
public class LeadStatusUpdatedEvent
{
    private final Lead lead;
    private final LeadStatus oldStatus;
    private final LeadStatus newStatus;
    public LeadStatusUpdatedEvent(Lead lead,  LeadStatus oldStatus, LeadStatus newStatus) {

        this.lead = lead;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}

