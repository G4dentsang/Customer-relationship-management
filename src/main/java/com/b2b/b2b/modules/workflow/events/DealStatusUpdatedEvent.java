package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;
import lombok.Getter;

@Getter
public class DealStatusUpdatedEvent
{
    private final Deal deal;
    private final DealStatus oldStatus;
    private final DealStatus newStatus;
    public DealStatusUpdatedEvent(Deal deal, DealStatus oldStatus, DealStatus newStatus)
    {
        this.deal = deal;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
