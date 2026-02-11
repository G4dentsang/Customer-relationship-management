package com.b2b.b2b.modules.crm.deal.api.event;

import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;
import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.DealStatus;

public record DealStatusUpdatedEvent(
        Deal deal, DealStatus oldStatus, DealStatus newStatus
) {
}
