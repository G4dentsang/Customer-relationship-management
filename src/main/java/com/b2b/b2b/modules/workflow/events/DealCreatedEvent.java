package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.deal.entity.Deal;

public record DealCreatedEvent(Deal deal) {

}
