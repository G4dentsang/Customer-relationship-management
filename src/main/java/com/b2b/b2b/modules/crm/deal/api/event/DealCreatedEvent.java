package com.b2b.b2b.modules.crm.deal.api.event;

import com.b2b.b2b.modules.crm.deal.internal.infrastructure.persistence.Deal;

public record DealCreatedEvent(Deal deal) {

}
