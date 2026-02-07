package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.deal.entity.Deal;
import com.b2b.b2b.modules.crm.deal.entity.DealStatus;

public record DealStatusUpdatedEvent(
        Deal deal, DealStatus oldStatus, DealStatus newStatus
) {
}
