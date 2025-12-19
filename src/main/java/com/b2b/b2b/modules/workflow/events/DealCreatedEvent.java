package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.deal.entity.Deals;
import lombok.Getter;

@Getter
public class DealCreatedEvent {

    private final Deals deal;
    public DealCreatedEvent(Deals deal) {
        this.deal = deal;
    }
}
