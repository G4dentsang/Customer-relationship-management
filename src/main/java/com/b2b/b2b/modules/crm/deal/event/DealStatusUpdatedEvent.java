package com.b2b.b2b.modules.crm.deal.event;

import com.b2b.b2b.modules.crm.deal.model.Deal;
import com.b2b.b2b.modules.crm.deal.model.DealStatus;

public record DealStatusUpdatedEvent(
        Deal deal, DealStatus oldStatus, DealStatus newStatus
) {
}
