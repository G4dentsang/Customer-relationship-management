package com.b2b.b2b.modules.workflow.events;

import com.b2b.b2b.modules.crm.deal.entity.Deal;
import lombok.Getter;

@Getter
public class DealDeletedEvent {
    private final Deal deal;
    public DealDeletedEvent(Deal deal) {
        this.deal = deal;
    }
}
