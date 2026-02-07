package com.b2b.b2b.modules.crm.deal.model;

import lombok.Getter;

@Getter
public enum DealStatus {
    //active group...
    OPEN(1),
    ACTIVE(1),
    NEEDS_ANALYSIS(1),

    //pending group...
    PENDING(2),
    ON_HOLD(2),

    //terminal group...
    CLOSED_WON(3),
    CLOSED_LOST(3),
    SOFT_DELETED(3);

    private final int groupId;
    DealStatus(int groupId) {
        this.groupId = groupId;
    }

}
